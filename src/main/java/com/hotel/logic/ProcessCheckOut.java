package com.hotel.logic;

import com.hotel.db.DBAccesslayer;
import com.hotel.model.CateringOrder;
import com.hotel.model.Reservation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.time.Duration;
import java.time.Instant;

public class ProcessCheckOut {

    private DBAccesslayer db = new DBAccesslayer();

    public String checkOutGuest(int tokenNumber) {
        Reservation res = db.getReservation(tokenNumber);
        if (res == null) {
            return "ERROR: No active reservation found for Token Number " + tokenNumber;
        }

        Instant checkIn = res.getArrivalDatetime().toInstant();
        Instant now = Instant.now();
        long hoursStayed = Duration.between(checkIn, now).toHours();
        long nightsStayed = hoursStayed / 24;
        if (hoursStayed % 24 > 0 || nightsStayed == 0) nightsStayed++; // partial day counts as a full night

        BigDecimal roomChargeSubtotal = res.getTariffAtCheckin().multiply(new BigDecimal(nightsStayed));

        List<CateringOrder> cateringOrders = db.getCateringOrders(tokenNumber);
        BigDecimal cateringChargeSubtotal = BigDecimal.ZERO;
        StringBuilder cateringDetails = new StringBuilder();

        for (CateringOrder order : cateringOrders) {
            cateringChargeSubtotal = cateringChargeSubtotal.add(order.getSubtotal());
            cateringDetails.append(String.format("   - %s (x%d) @ $%.2f: $%.2f\n",
                    order.getFoodItem(), order.getQuantity(), order.getUnitPrice(), order.getSubtotal()));
        }

        BigDecimal totalCharge = roomChargeSubtotal.add(cateringChargeSubtotal);
        BigDecimal discountAmt = BigDecimal.ZERO;

        if (res.getIdentityNumber() != null) {
            BigDecimal discountPct = db.getFrequentGuestDiscount(res.getIdentityNumber());
            discountAmt = totalCharge.multiply(discountPct).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal netBalance = totalCharge.subtract(discountAmt).subtract(res.getAdvancePaid());

        boolean dbSuccess = db.completeCheckOutDB(tokenNumber, res.getRoomNumber());
        if (!dbSuccess) {
            return "ERROR: Failed to update database during checkout.";
        }

        return formatBill(res, nightsStayed, roomChargeSubtotal, cateringDetails.toString(),
                cateringChargeSubtotal, totalCharge, discountAmt, netBalance);
    }

    private String formatBill(Reservation res, long nights, BigDecimal roomCharge,
                              String cateringDetails, BigDecimal cateringTotal,
                              BigDecimal totalCharge, BigDecimal discount, BigDecimal netBalance) {
        return "==========================================\n" +
                "           HOTEL CHECK-OUT BILL           \n" +
                "==========================================\n" +
                "Guest Name: " + res.getGuestName() + "\n" +
                "Room Number: " + res.getRoomNumber() + "\n" +
                "Token Number: " + res.getTokenNumber() + "\n" +
                "------------------------------------------\n" +
                "ROOM CHARGES:\n" +
                "   Tariff: $" + res.getTariffAtCheckin() + " x " + nights + " nights\n" +
                "   Subtotal: $" + roomCharge.setScale(2, RoundingMode.HALF_UP) + "\n\n" +
                "CATERING CHARGES:\n" +
                (cateringDetails.isEmpty() ? "   No catering charges.\n" : cateringDetails) +
                "   Subtotal: $" + cateringTotal.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "------------------------------------------\n" +
                "Gross Total: $" + totalCharge.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Loyalty Discount: -$" + discount.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Advance Paid: -$" + res.getAdvancePaid().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "==========================================\n" +
                "NET BALANCE PAYABLE: $" + netBalance.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "==========================================";
    }
}
