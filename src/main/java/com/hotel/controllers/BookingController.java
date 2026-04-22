package com.hotel.controllers;

import com.hotel.db.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingController {

    public static String bookGuest(String name, String identityNo, double advance, int duration, String bedType, String acType) {
        String findRoomQuery = "SELECT room_no FROM rooms WHERE bed_type = ? AND ac_type = ? AND is_occupied = FALSE LIMIT 1";
        String bookRoomQuery = "UPDATE rooms SET is_occupied = TRUE WHERE room_no = ?";
        String insertGuestQuery = "INSERT INTO guests (guest_name, identity_number, advance_paid, expected_duration, room_no) VALUES (?, ?, ?, ?, ?) RETURNING token_no";

        try (Connection conn = DatabaseHelper.getConnection()) {
            if (conn == null) return "Error: Database connection failed.";

            PreparedStatement findStmt = conn.prepareStatement(findRoomQuery);
            findStmt.setString(1, bedType);
            findStmt.setString(2, acType);
            ResultSet rs = findStmt.executeQuery();

            if (rs.next()) {
                int assignedRoom = rs.getInt("room_no");

                PreparedStatement updateRoomStmt = conn.prepareStatement(bookRoomQuery);
                updateRoomStmt.setInt(1, assignedRoom);
                updateRoomStmt.executeUpdate();

                PreparedStatement insertGuestStmt = conn.prepareStatement(insertGuestQuery);
                insertGuestStmt.setString(1, name);
                insertGuestStmt.setString(2, identityNo.isEmpty() ? null : identityNo);
                insertGuestStmt.setDouble(3, advance);
                insertGuestStmt.setInt(4, duration);
                insertGuestStmt.setInt(5, assignedRoom);

                ResultSet guestRs = insertGuestStmt.executeQuery();
                if (guestRs.next()) {
                    int tokenNo = guestRs.getInt("token_no");
                    return "Success! Room " + assignedRoom + " assigned. Guest Token No: " + tokenNo;
                }
            } else {
                return "Apology: No available rooms matching those requirements."; // [cite: 119]
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
        return "An unknown error occurred.";
    }
}