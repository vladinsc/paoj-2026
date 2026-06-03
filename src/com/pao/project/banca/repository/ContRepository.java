package com.pao.project.banca.repository;

import com.pao.project.banca.models.Cont;
import com.pao.project.banca.models.ContCurent;
import com.pao.project.banca.models.ContEconomii;
import com.pao.project.banca.models.Moneda;
import com.pao.project.banca.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContRepository implements Repository<Cont, String> {
    @Override
    public void save(Cont entity) {
        String sql = "INSERT INTO conturi (iban, client_id, tip_cont, moneda, sold, activ, nume_banca) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getIban());
            pstmt.setString(2, entity.getIdClient());
            pstmt.setString(3, entity.getTipCont());
            pstmt.setString(4, entity.getMonedaEnum().name());
            pstmt.setDouble(5, entity.getSold());
            pstmt.setBoolean(6, entity.isActiv());
            pstmt.setString(7, entity.getNumeBanca());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Cont> findById(String iban) {
        String sql = "SELECT * FROM conturi WHERE iban = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, iban);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tipCont = rs.getString("tip_cont");
                    String ib = rs.getString("iban");
                    String clientId = rs.getString("client_id");
                    Moneda mon = Moneda.valueOf(rs.getString("moneda"));
                    String numeBanca = rs.getString("nume_banca");
                    
                    Cont cont;
                    if ("ECONOMII".equalsIgnoreCase(tipCont) || "Cont Economii".equalsIgnoreCase(tipCont)) {
                        cont = new ContEconomii(ib, clientId, mon, numeBanca);
                    } else {
                        cont = new ContCurent(ib, clientId, mon, numeBanca);
                    }
                    cont.setSold(rs.getDouble("sold"));
                    cont.setActiv(rs.getBoolean("activ"));
                    return Optional.of(cont);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Cont> findAll() {
        List<Cont> conturi = new ArrayList<>();
        String sql = "SELECT * FROM conturi";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String tipCont = rs.getString("tip_cont");
                String ib = rs.getString("iban");
                String clientId = rs.getString("client_id");
                Moneda mon = Moneda.valueOf(rs.getString("moneda"));
                String numeBanca = rs.getString("nume_banca");

                Cont cont;
                if ("ECONOMII".equalsIgnoreCase(tipCont) || "Cont Economii".equalsIgnoreCase(tipCont)) {
                    cont = new ContEconomii(ib, clientId, mon, numeBanca);
                } else {
                    cont = new ContCurent(ib, clientId, mon, numeBanca);
                }
                cont.setSold(rs.getDouble("sold"));
                cont.setActiv(rs.getBoolean("activ"));
                conturi.add(cont);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conturi;
    }

    @Override
    public void update(Cont entity) {
        String sql = "UPDATE conturi SET sold = ?, activ = ?, nume_banca = ? WHERE iban = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, entity.getSold());
            pstmt.setBoolean(2, entity.isActiv());
            pstmt.setString(3, entity.getNumeBanca());
            pstmt.setString(4, entity.getIban());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String iban) {
        String sql = "DELETE FROM conturi WHERE iban = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, iban);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
