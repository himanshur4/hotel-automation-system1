package com.hotel.controllers;

import com.hotel.db.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    // Calculate current occupancy percentage
    public static double getOccupancyRate() {
        String query = "SELECT " +
                "(SELECT COUNT(*) FROM rooms WHERE is_occupied = TRUE) * 100.0 / " +
                "(SELECT COUNT(*) FROM rooms) AS occupancy_rate";

        try (Connection conn = DatabaseHelper.getConnection()) {
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("occupancy_rate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Adjust all room tariffs by a percentage (positive for increase, negative for decrease)
    public static String adjustTariff(double percentage) {
        String updateQuery = "UPDATE rooms SET base_rate = base_rate + (base_rate * (? / 100.0))";

        try (Connection conn = DatabaseHelper.getConnection()) {
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(updateQuery);
                stmt.setDouble(1, percentage);
                int rowsAffected = stmt.executeUpdate();
                return "Success: Updated tariffs for " + rowsAffected + " rooms by " + percentage + "%.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
        return "An unknown error occurred.";
    }
}