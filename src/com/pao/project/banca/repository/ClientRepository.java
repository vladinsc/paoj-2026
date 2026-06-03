package com.pao.project.banca.repository;

import com.pao.project.banca.models.Adresa;
import com.pao.project.banca.models.Client;
import com.pao.project.banca.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientRepository implements Repository<Client, String> {
    private final AdresaRepository adresaRepository = new AdresaRepository();

    @Override
    public void save(Client entity) {
        String adresaId = adresaRepository.saveAndGetId(entity.getAdresa());
        String sql = "INSERT INTO clienti (id, nume, prenume, cnp, email, telefon, adresa_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getId());
            pstmt.setString(2, entity.getNume());
            pstmt.setString(3, entity.getPrenume());
            pstmt.setString(4, entity.getCnp());
            pstmt.setString(5, entity.getEmail());
            pstmt.setString(6, entity.getTelefon());
            pstmt.setString(7, adresaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Adresa adresa = null;
        if (rs.getString("adresa_id") != null) {
            adresa = new Adresa(
                    rs.getString("strada"),
                    rs.getString("numar"),
                    rs.getString("oras"),
                    rs.getString("judet"),
                    rs.getString("cod_postal"),
                    rs.getString("tara")
            );
        }
        return new Client(
                rs.getString("id"),
                rs.getString("nume"),
                rs.getString("prenume"),
                rs.getString("cnp"),
                rs.getString("email"),
                rs.getString("telefon"),
                adresa
        );
    }

    @Override
    public Optional<Client> findById(String id) {
        String sql = "SELECT c.*, a.strada, a.numar, a.oras, a.judet, a.cod_postal, a.tara " +
                     "FROM clienti c " +
                     "LEFT JOIN adrese a ON c.adresa_id = a.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Client> findByCnp(String cnp) {
        String sql = "SELECT c.*, a.strada, a.numar, a.oras, a.judet, a.cod_postal, a.tara " +
                     "FROM clienti c " +
                     "LEFT JOIN adrese a ON c.adresa_id = a.id " +
                     "WHERE c.cnp = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cnp);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        List<Client> clienti = new ArrayList<>();
        String sql = "SELECT c.*, a.strada, a.numar, a.oras, a.judet, a.cod_postal, a.tara " +
                     "FROM clienti c " +
                     "LEFT JOIN adrese a ON c.adresa_id = a.id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                clienti.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clienti;
    }

    @Override
    public void update(Client entity) {
        String sql = "UPDATE clienti SET nume = ?, prenume = ?, email = ?, telefon = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNume());
            pstmt.setString(2, entity.getPrenume());
            pstmt.setString(3, entity.getEmail());
            pstmt.setString(4, entity.getTelefon());
            pstmt.setString(5, entity.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM clienti WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getClientiCuSoldTotal() {
        List<String> rezultate = new ArrayList<>();
        String sql = "SELECT c.nume, c.prenume, SUM(co.sold) as sold_total " +
                     "FROM clienti c " +
                     "JOIN conturi co ON c.id = co.client_id " +
                     "GROUP BY c.id";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rezultate.add(rs.getString("prenume") + " " + rs.getString("nume") + ": " + rs.getDouble("sold_total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultate;
    }

    public List<String> getTranzactiiRecenteClient(String clientId) {
        List<String> rezultate = new ArrayList<>();
        String sql = "SELECT t.tip_tranzactie, t.suma, t.timestamp " +
                     "FROM tranzactii t " +
                     "JOIN conturi co ON t.iban_sursa = co.iban OR t.iban_destinatie = co.iban " +
                     "JOIN clienti c ON co.client_id = c.id " +
                     "WHERE c.id = ? " +
                     "ORDER BY t.timestamp DESC LIMIT 5";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rezultate.add(rs.getString("tip_tranzactie") + " | " + rs.getDouble("suma") + " | " + rs.getString("timestamp"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultate;
    }

    public List<String> getTopClientiDupaNrCarduri() {
        List<String> rezultate = new ArrayList<>();
        String sql = "SELECT c.nume, c.prenume, COUNT(ca.numar_card) as nr_carduri " +
                     "FROM clienti c " +
                     "JOIN conturi co ON c.id = co.client_id " +
                     "JOIN carduri ca ON co.iban = ca.iban " +
                     "GROUP BY c.id " +
                     "ORDER BY nr_carduri DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rezultate.add(rs.getString("prenume") + " " + rs.getString("nume") + ": " + rs.getInt("nr_carduri") + " carduri");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultate;
    }
}
