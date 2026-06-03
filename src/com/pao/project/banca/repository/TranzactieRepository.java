package com.pao.project.banca.repository;

import com.pao.project.banca.models.TipTranzactie;
import com.pao.project.banca.models.Tranzactie;
import com.pao.project.banca.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TranzactieRepository implements Repository<Tranzactie, String> {
    @Override
    public void save(Tranzactie entity) {
        String sql = "INSERT INTO tranzactii (id, iban_sursa, iban_destinatie, suma, tip_tranzactie, descriere) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getId());
            pstmt.setString(2, entity.getIbanSursa());
            pstmt.setString(3, entity.getIbanDestinatie());
            pstmt.setDouble(4, entity.getSuma());
            pstmt.setString(5, entity.getTipTranzactie().name());
            pstmt.setString(6, entity.getDescriere());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Tranzactie> findById(String id) {
        String sql = "SELECT * FROM tranzactii WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Tranzactie(
                            rs.getString("id"),
                            rs.getString("iban_sursa"),
                            rs.getString("iban_destinatie"),
                            rs.getDouble("suma"),
                            TipTranzactie.valueOf(rs.getString("tip_tranzactie")),
                            rs.getString("descriere")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Tranzactie> findByIban(String iban) {
        List<Tranzactie> tranzactii = new ArrayList<>();
        String sql = "SELECT * FROM tranzactii WHERE iban_sursa = ? OR iban_destinatie = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, iban);
            pstmt.setString(2, iban);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tranzactii.add(new Tranzactie(
                            rs.getString("id"),
                            rs.getString("iban_sursa"),
                            rs.getString("iban_destinatie"),
                            rs.getDouble("suma"),
                            TipTranzactie.valueOf(rs.getString("tip_tranzactie")),
                            rs.getString("descriere")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tranzactii;
    }

    @Override
    public List<Tranzactie> findAll() {
        List<Tranzactie> tranzactii = new ArrayList<>();
        String sql = "SELECT * FROM tranzactii ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tranzactii.add(new Tranzactie(
                        rs.getString("id"),
                        rs.getString("iban_sursa"),
                        rs.getString("iban_destinatie"),
                        rs.getDouble("suma"),
                        TipTranzactie.valueOf(rs.getString("tip_tranzactie")),
                        rs.getString("descriere")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tranzactii;
    }

    @Override
    public void update(Tranzactie entity) {}

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM tranzactii WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
