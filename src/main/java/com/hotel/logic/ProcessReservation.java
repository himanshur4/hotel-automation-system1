package com.hotel.logic;

import com.hotel.db.DBAccesslayer;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class ProcessReservation {

    private DBAccesslayer db = new DBAccesslayer();

    public String bookRoom(String guestName, String bedType, String acType,
                           int duration, BigDecimal advance, Integer loyaltyId) {

        List<Room> availableRooms = db.getAvailableRooms(bedType, acType);

        if (availableRooms.isEmpty()) {
            return "APOLOGY: We sincerely apologize, but there are no " + bedType + " " + acType +
                    " rooms available for your requested dates.";
        }

        Room allocatedRoom = availableRooms.get(0); // lowest available room number

        BigDecimal currentTariff = db.getTariff(bedType, acType);
        if (currentTariff == null) {
            return "ERROR: Tariff not configured for this room type. Please contact Manager.";
        }

        Reservation newRes = new Reservation(
                guestName,
                allocatedRoom.getRoomNumber(),
                new Timestamp(System.currentTimeMillis()), // arrival time is now for walk-ins
                duration,
                advance,
                loyaltyId,
                currentTariff
        );

        int token = db.createReservation(newRes);

        if (token != -1) {
            return "SUCCESS: Room " + allocatedRoom.getRoomNumber() +
                    " allocated successfully.\nGuest Token Number: " + token;
        } else {
            return "ERROR: Failed to save reservation to the database.";
        }
    }
}
