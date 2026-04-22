package com.hotel.controllers;

import com.hotel.db.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckoutController {

    public static String processCheckout(int tokenNo) {
        String guestQuery = "SELECT g.guest_name, g.advance_paid, g.expected_duration, g.identity_number, r.room_no, r.base_rate " +
                "FROM guests g JOIN rooms r ON g.room_no = r.room_no " +
                "WHERE g.token_no = ? AND g.status = 'Checked-In'";

        String cateringQuery = "SELECT food_item, quantity FROM catering_orders WHERE token_no = ?";
        String updateGuestQuery = "UPDATE guests SET status = 'Checked-Out' WHERE token_no = ?";
        String updateRoomQuery = "UPDATE rooms SET is_occupied = FALSE WHERE room_no = ?";

        StringBuilder bill = new StringBuilder();

        try (Connection conn = DatabaseHelper.getConnection()) {
            if (conn == null) return "Error: Database connection failed.";

            // 1. Fetch Guest and Room Details
            PreparedStatement guestStmt = conn.prepareStatement(guestQuery);
            guestStmt.setInt(1, tokenNo);
            ResultSet guestRs = guestStmt.executeQuery();

            if (!guestRs.next()) {
                return "Error: Token Number " + tokenNo + " not found or already checked out.";
            }

            String name = guestRs.getString("guest_name");
            double advance = guestRs.getDouble("advance_paid");
            int duration = guestRs.getInt("expected_duration");
            String identityNo = guestRs.getString("identity_number");
            int roomNo = guestRs.getInt("room_no");
            double baseRate = guestRs.getDouble("base_rate");

            double roomTotal = duration * baseRate;

            // 2. Fetch Catering Orders (Assuming a flat Rs. 150 per item for this demo)
            PreparedStatement foodStmt = conn.prepareStatement(cateringQuery);
            foodStmt.setInt(1, tokenNo);
            ResultSet foodRs = foodStmt.executeQuery();

            double foodTotal = 0;
            bill.append("====================================\n");
            bill.append("        HOTEL FINAL BILL\n");
            bill.append("====================================\n");
            bill.append("Guest Name: ").append(name).append("\n");
            bill.append("Room No: ").append(roomNo).append("\n");
            bill.append("Duration: ").append(duration).append(" days\n");
            bill.append("------------------------------------\n");
            bill.append(String.format("Room Charges: Rs. %.2f\n", roomTotal));

            bill.append("Food Orders:\n");
            while (foodRs.next()) {
                String item = foodRs.getString("food_item");
                int qty = foodRs.getInt("quantity");
                double itemCost = qty * 150.0; // Mock price
                foodTotal += itemCost;
                bill.append(String.format(" - %dx %s: Rs. %.2f\n", qty, item, itemCost));
            }
            bill.append(String.format("Total Food Charges: Rs. %.2f\n", foodTotal));
            bill.append("------------------------------------\n");

            // 3. Calculate Totals and Discounts
            double subtotal = roomTotal + foodTotal;
            double discount = 0;

            if (identityNo != null && !identityNo.trim().isEmpty()) {
                discount = subtotal * 0.10; // 10% discount for frequent guests
                bill.append(String.format("Frequent Guest Discount (10%%): -Rs. %.2f\n", discount));
            }

            double grandTotal = subtotal - discount;
            double balance = grandTotal - advance;

            bill.append(String.format("Subtotal: Rs. %.2f\n", subtotal));
            bill.append(String.format("Advance Paid: -Rs. %.2f\n", advance));
            bill.append("====================================\n");
            bill.append(String.format("BALANCE PAYABLE: Rs. %.2f\n", balance));
            bill.append("====================================\n");

            // 4. Update Database Statuses (Checkout)
            PreparedStatement outGuest = conn.prepareStatement(updateGuestQuery);
            outGuest.setInt(1, tokenNo);
            outGuest.executeUpdate();

            PreparedStatement outRoom = conn.prepareStatement(updateRoomQuery);
            outRoom.setInt(1, roomNo);
            outRoom.executeUpdate();

            return bill.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        }
    }
}