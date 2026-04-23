package com.hotel.controller;

import com.hotel.logic.AdminLogic;
import com.hotel.logic.ProcessReservation;
import com.hotel.logic.ProcessCheckOut;
import com.hotel.logic.RecordCateringOrder;
import java.util.List;

public class DispatchTransaction {

    private RecordCateringOrder recordCateringOrder = new RecordCateringOrder();
    private ProcessReservation processReservation = new ProcessReservation();
    private ProcessCheckOut processCheckOut = new ProcessCheckOut();
    private AdminLogic adminLogic = new AdminLogic();

    public List<String> getMenu() {
        return recordCateringOrder.getAvailableMenu();
    }

    public String handleCateringOrder(int tokenNumber, String foodItem, int quantity) {
        return recordCateringOrder.processOrder(tokenNumber, foodItem, quantity);
    }

    public String handleReservation(ReservationRequest req) {
        if (req.guestName == null || req.guestName.trim().isEmpty()) {
            return "VALIDATION ERROR: Guest name cannot be empty.";
        }
        if (req.durationNights <= 0) {
            return "VALIDATION ERROR: Duration must be at least 1 night.";
        }
        return processReservation.bookRoom(
                req.guestName,
                req.bedType,
                req.acType,
                req.durationNights,
                req.advancePaid,
                req.loyaltyId
        );
    }

    public String handleCheckOut(int tokenNumber) {
        if (tokenNumber <= 0) {
            return "VALIDATION ERROR: Please enter a valid Token Number.";
        }
        return processCheckOut.checkOutGuest(tokenNumber);
    }

    public String handleTariffAdjustment(String bedType, String acType, double percentage) {
        return adminLogic.adjustTariff(bedType, acType, percentage);
    }

    public String handleOccupancyReport(int year, int month) {
        return adminLogic.generateOccupancyReport(year, month);
    }
}
