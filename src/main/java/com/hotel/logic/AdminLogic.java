package com.hotel.logic;

import com.hotel.db.DBAccesslayer;
import com.hotel.model.Reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AdminLogic {

    private DBAccesslayer db = new DBAccesslayer();

    public String adjustTariff(String bedType, String acType, double percentageString) {
        BigDecimal currentTariff = db.getTariff(bedType, acType);
        if (currentTariff == null) {
            return "ERROR: Room type not found in tariff table.";
        }

        // newTariff = current * (1 + pct / 100)
        BigDecimal multiplier = BigDecimal.ONE.add(new BigDecimal(percentageString).divide(new BigDecimal(100)));
        BigDecimal newTariff = currentTariff.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        boolean success = db.updateTariff(bedType, acType, newTariff);
        if (success) {
            return "SUCCESS: Tariff updated from $" + currentTariff + " to $" + newTariff;
        }
        return "ERROR: Database update failed.";
    }

    public String generateOccupancyReport(int year, int month) {
        YearMonth targetMonth = YearMonth.of(year, month);
        int daysInMonth = targetMonth.lengthOfMonth();
        int totalRooms = db.getTotalRoomsCount();

        if (totalRooms == 0) return "ERROR: No rooms configured in the database.";

        long totalAvailableNights = (long) totalRooms * daysInMonth;
        long totalOccupiedNights = 0;

        LocalDate monthStart = targetMonth.atDay(1);
        LocalDate monthEnd = targetMonth.atEndOfMonth();

        List<Reservation> allRes = db.getAllReservations();

        for (Reservation res : allRes) {
            LocalDate checkIn = res.getArrivalDatetime().toLocalDateTime().toLocalDate();
            // tokenNumber holds checkout time in Unix seconds — set by getAllReservations()
            LocalDate checkOut = LocalDate.ofEpochDay(res.getTokenNumber() / 86400);

            // Clip the reservation to only the days that fall within this month
            LocalDate overlapStart = checkIn.isAfter(monthStart) ? checkIn : monthStart;
            LocalDate overlapEnd = checkOut.isBefore(monthEnd) ? checkOut : monthEnd;

            if (!overlapStart.isAfter(overlapEnd)) {
                long nights = java.time.temporal.ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                if (nights == 0) nights = 1; // same-day check-in/out counts as 1 night
                totalOccupiedNights += nights;
            }
        }

        double occupancyRate = ((double) totalOccupiedNights / totalAvailableNights) * 100;

        return String.format("Occupancy Report for %02d/%04d\n" +
                        "--------------------------------\n" +
                        "Total Available Room-Nights: %d\n" +
                        "Total Occupied Room-Nights: %d\n\n" +
                        "Average Occupancy Rate: %.2f%%",
                month, year, totalAvailableNights, totalOccupiedNights, occupancyRate);
    }
}
