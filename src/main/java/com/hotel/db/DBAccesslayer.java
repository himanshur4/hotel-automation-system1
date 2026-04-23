package com.hotel.db;

import com.hotel.model.CateringOrder;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAccesslayer {

    private static final String URL = "jdbc:postgresql://localhost:5432/hotel_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_PG_PASSWORD";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Room> getAvailableRooms(String bedType, String acType) {
        List<Room> availableRooms = new ArrayList<>();
        String query = "SELECT * FROM rooms WHERE bed_type = ? AND ac_type = ? AND status = 'available' ORDER BY room_number ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bedType);
            pstmt.setString(2, acType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availableRooms.add(new Room(
                            rs.getInt("room_number"),
                            rs.getString("bed_type"),
                            rs.getString("ac_type"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getAvailableRooms): " + e.getMessage());
        }
        return availableRooms;
    }

    public BigDecimal getTariff(String bedType, String acType) {
        String query = "SELECT tariff_per_night FROM tariffs WHERE bed_type = ? AND ac_type = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bedType);
            pstmt.setString(2, acType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("tariff_per_night");
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getTariff): " + e.getMessage());
        }
        return null;
    }

    public int createReservation(Reservation res) {
        String insertRes = "INSERT INTO reservations (guest_name, room_number, arrival_datetime, approx_duration_nights, advance_paid, identity_number, tariff_at_checkin) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING token_number";
        String updateRoom = "UPDATE rooms SET status = 'occupied' WHERE room_number = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtRes = conn.prepareStatement(insertRes);
                 PreparedStatement pstmtRoom = conn.prepareStatement(updateRoom)) {

                pstmtRes.setString(1, res.getGuestName());
                pstmtRes.setInt(2, res.getRoomNumber());
                pstmtRes.setTimestamp(3, res.getArrivalDatetime());
                pstmtRes.setInt(4, res.getApproxDurationNights());
                pstmtRes.setBigDecimal(5, res.getAdvancePaid());
                if (res.getIdentityNumber() != null) {
                    pstmtRes.setInt(6, res.getIdentityNumber());
                } else {
                    pstmtRes.setNull(6, java.sql.Types.INTEGER);
                }
                pstmtRes.setBigDecimal(7, res.getTariffAtCheckin());

                int generatedToken = -1;
                try (ResultSet rs = pstmtRes.executeQuery()) {
                    if (rs.next()) generatedToken = rs.getInt("token_number");
                }

                pstmtRoom.setInt(1, res.getRoomNumber());
                pstmtRoom.executeUpdate();

                conn.commit();
                return generatedToken;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Database Error (createReservation): " + e.getMessage());
        }
        return -1;
    }

    public Reservation getReservation(int tokenNumber) {
        String query = "SELECT * FROM reservations WHERE token_number = ? AND checkout_datetime IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tokenNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation res = new Reservation(
                            rs.getString("guest_name"),
                            rs.getInt("room_number"),
                            rs.getTimestamp("arrival_datetime"),
                            rs.getInt("approx_duration_nights"),
                            rs.getBigDecimal("advance_paid"),
                            rs.getObject("identity_number") != null ? rs.getInt("identity_number") : null,
                            rs.getBigDecimal("tariff_at_checkin")
                    );
                    res.setTokenNumber(rs.getInt("token_number"));
                    return res;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getReservation): " + e.getMessage());
        }
        return null;
    }

    public List<CateringOrder> getCateringOrders(int tokenNumber) {
        List<CateringOrder> orders = new ArrayList<>();
        String query = "SELECT c.food_item, c.quantity, m.unit_price " +
                "FROM catering_orders c JOIN menu_items m ON c.food_item = m.food_item " +
                "WHERE c.token_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tokenNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new CateringOrder(
                            rs.getString("food_item"),
                            rs.getInt("quantity"),
                            rs.getBigDecimal("unit_price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getCateringOrders): " + e.getMessage());
        }
        return orders;
    }

    public BigDecimal getFrequentGuestDiscount(int identityNumber) {
        String query = "SELECT discount_pct FROM frequent_guests WHERE identity_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, identityNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("discount_pct");
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getFrequentGuestDiscount): " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public boolean completeCheckOutDB(int tokenNumber, int roomNumber) {
        String updateRes = "UPDATE reservations SET checkout_datetime = CURRENT_TIMESTAMP WHERE token_number = ?";
        String updateRoom = "UPDATE rooms SET status = 'available' WHERE room_number = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtRes = conn.prepareStatement(updateRes);
                 PreparedStatement pstmtRoom = conn.prepareStatement(updateRoom)) {

                pstmtRes.setInt(1, tokenNumber);
                pstmtRes.executeUpdate();

                pstmtRoom.setInt(1, roomNumber);
                pstmtRoom.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Database Error (completeCheckOutDB): " + e.getMessage());
        }
        return false;
    }

    public boolean isTokenActive(int tokenNumber) {
        String query = "SELECT 1 FROM reservations WHERE token_number = ? AND checkout_datetime IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tokenNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Database Error (isTokenActive): " + e.getMessage());
        }
        return false;
    }

    public List<String> getMenuFoodItems() {
        List<String> items = new ArrayList<>();
        String query = "SELECT food_item FROM menu_items ORDER BY food_item ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                items.add(rs.getString("food_item"));
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getMenuFoodItems): " + e.getMessage());
        }
        return items;
    }

    public boolean createCateringOrder(int tokenNumber, String foodItem, int quantity) {
        String query = "INSERT INTO catering_orders (token_number, food_item, quantity, order_datetime) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tokenNumber);
            pstmt.setString(2, foodItem);
            pstmt.setInt(3, quantity);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database Error (createCateringOrder): " + e.getMessage());
        }
        return false;
    }

    public boolean updateTariff(String bedType, String acType, BigDecimal newTariff) {
        BigDecimal oldTariff = getTariff(bedType, acType);
        if (oldTariff == null) return false;

        String updateTariffSql = "UPDATE tariffs SET tariff_per_night = ? WHERE bed_type = ? AND ac_type = ?";
        String logSql = "INSERT INTO tariff_change_log (bed_type, ac_type, old_tariff, new_tariff) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateTariffSql);
                 PreparedStatement pstmtLog = conn.prepareStatement(logSql)) {

                pstmtUpdate.setBigDecimal(1, newTariff);
                pstmtUpdate.setString(2, bedType);
                pstmtUpdate.setString(3, acType);
                pstmtUpdate.executeUpdate();

                // log the old and new value for auditing
                pstmtLog.setString(1, bedType);
                pstmtLog.setString(2, acType);
                pstmtLog.setBigDecimal(3, oldTariff);
                pstmtLog.setBigDecimal(4, newTariff);
                pstmtLog.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            System.err.println("Database Error (updateTariff): " + e.getMessage());
        }
        return false;
    }

    public int getTotalRoomsCount() {
        String query = "SELECT COUNT(*) AS total FROM rooms";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String query = "SELECT * FROM reservations";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getTimestamp("arrival_datetime"),
                        rs.getInt("approx_duration_nights"),
                        rs.getBigDecimal("advance_paid"),
                        rs.getObject("identity_number") != null ? rs.getInt("identity_number") : null,
                        rs.getBigDecimal("tariff_at_checkin")
                );
                // Store checkout time as Unix seconds in tokenNumber for occupancy calculations
                Timestamp checkout = rs.getTimestamp("checkout_datetime");
                res.setTokenNumber((int)((checkout != null ? checkout.getTime() : System.currentTimeMillis()) / 1000));
                list.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
