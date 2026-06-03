package com.pao.project.banca.repository;

import com.pao.project.banca.models.Card;
import com.pao.project.banca.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRepository implements Repository<Card, String> {
    @Override
    public void save(Card entity) {
        String sql = "INSERT INTO carduri (numar_card, iban, pin, tip_card, nume_detinator, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getNumarCard());
            pstmt.setString(2, entity.getIban());
            pstmt.setString(3, entity.getPIN());
            pstmt.setString(4, entity.getTipCard().name());
            pstmt.setString(5, entity.getNumeDetinator());
            pstmt.setString(6, entity.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Card> findById(String numarCard) {
        String sql = "SELECT * FROM carduri WHERE numar_card = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numarCard);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Card card = new Card(
                            rs.getString("numar_card"),
                            rs.getString("iban"),
                            rs.getString("pin"),
                            Card.TipCard.valueOf(rs.getString("tip_card")),
                            rs.getString("nume_detinator")
                    );
                    card.setStatus(Card.StatusCard.valueOf(rs.getString("status")));
                    return Optional.of(card);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Card> findAll() {
        List<Card> carduri = new ArrayList<>();
        String sql = "SELECT * FROM carduri";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Card card = new Card(
                        rs.getString("numar_card"),
                        rs.getString("iban"),
                        rs.getString("pin"),
                        Card.TipCard.valueOf(rs.getString("tip_card")),
                        rs.getString("nume_detinator")
                );
                card.setStatus(Card.StatusCard.valueOf(rs.getString("status")));
                carduri.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carduri;
    }

    @Override
    public void update(Card entity) {
        String sql = "UPDATE carduri SET status = ?, pin = ? WHERE numar_card = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entity.getStatus().name());
            pstmt.setString(2, entity.getPIN());
            pstmt.setString(3, entity.getNumarCard());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String numarCard) {
        String sql = "DELETE FROM carduri WHERE numar_card = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, numarCard);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
