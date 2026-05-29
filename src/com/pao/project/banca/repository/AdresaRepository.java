package com.pao.project.banca.repository;

import com.pao.project.banca.models.Adresa;
import com.pao.project.banca.utils.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AdresaRepository implements Repository<Adresa, String> {
    @Override
    public void save(Adresa entity) {
        String sql = "INSERT INTO adrese (id, strada, numar, oras, judet, cod_postal, tara) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            String id = UUID.randomUUID().toString();
            pstmt.setString(1, id);
            pstmt.setString(2, entity.getStrada());
            pstmt.setString(3, entity.getNumar());
            pstmt.setString(4, entity.getOras());
            pstmt.setString(5, entity.getJudet());
            pstmt.setString(6, entity.getCodPostal());
            pstmt.setString(7, entity.getTara());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String saveAndGetId(Adresa entity) {
        String id = UUID.randomUUID().toString();
        String sql = "INSERT INTO adrese (id, strada, numar, oras, judet, cod_postal, tara) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, entity.getStrada());
            pstmt.setString(3, entity.getNumar());
            pstmt.setString(4, entity.getOras());
            pstmt.setString(5, entity.getJudet());
            pstmt.setString(6, entity.getCodPostal());
            pstmt.setString(7, entity.getTara());
            pstmt.executeUpdate();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<Adresa> findById(String id) {
        String sql = "SELECT * FROM adrese WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Adresa(
                            rs.getString("strada"),
                            rs.getString("numar"),
                            rs.getString("oras"),
                            rs.getString("judet"),
                            rs.getString("cod_postal"),
                            rs.getString("tara")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Adresa> findAll() {
        List<Adresa> adrese = new ArrayList<>();
        String sql = "SELECT * FROM adrese";
        try (PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                adrese.add(new Adresa(
                        rs.getString("strada"),
                        rs.getString("numar"),
                        rs.getString("oras"),
                        rs.getString("judet"),
                        rs.getString("cod_postal"),
                        rs.getString("tara")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adrese;
    }

    @Override
    public void update(Adresa entity) {}

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM adrese WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getInstance().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
