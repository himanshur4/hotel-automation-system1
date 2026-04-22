package com.hotel.controllers;

import com.hotel.db.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CateringController {

    public static String addFoodOrder(int tokenNo, String foodItem, int quantity) {
        String checkTokenQuery = "SELECT guest_name FROM guests WHERE token_no = ? AND status = 'Checked-In'";
        String insertOrderQuery = "INSERT INTO catering_orders (token_no, food_item, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection()) {
            if (conn == null) return "Error: Database connection failed.";

            // 1. Verify the token exists and the guest is still checked in
            PreparedStatement checkStmt = conn.prepareStatement(checkTokenQuery);
            checkStmt.setInt(1, tokenNo);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String guestName = rs.getString("guest_name");

                // 2. Insert the food order
                PreparedStatement insertStmt = conn.prepareStatement(insertOrderQuery);
                insertStmt.setInt(1, tokenNo);
                insertStmt.setString(2, foodItem);
                insertStmt.setInt(3, quantity);

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    return "Success: Added " + quantity + "x " + foodItem + " to " + guestName + "'s tab (Token: " + tokenNo + ").";
                }
            } else {
                return "Error: Token Number " + tokenNo + " not found or guest has already checked out.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
        return "An unknown error occurred.";
    }
}