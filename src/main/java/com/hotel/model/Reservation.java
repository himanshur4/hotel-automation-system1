package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Reservation {
    private int tokenNumber;
    private String guestName;
    private int roomNumber;
    private Timestamp arrivalDatetime;
    private int approxDurationNights;
    private BigDecimal advancePaid;
    private Integer identityNumber; // null if guest has no loyalty ID
    private BigDecimal tariffAtCheckin;

    public Reservation(String guestName, int roomNumber, Timestamp arrivalDatetime,
                       int approxDurationNights, BigDecimal advancePaid,
                       Integer identityNumber, BigDecimal tariffAtCheckin) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.arrivalDatetime = arrivalDatetime;
        this.approxDurationNights = approxDurationNights;
        this.advancePaid = advancePaid;
        this.identityNumber = identityNumber;
        this.tariffAtCheckin = tariffAtCheckin;
    }

    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }
    public Timestamp getArrivalDatetime() { return arrivalDatetime; }
    public int getApproxDurationNights() { return approxDurationNights; }
    public BigDecimal getAdvancePaid() { return advancePaid; }
    public Integer getIdentityNumber() { return identityNumber; }
    public BigDecimal getTariffAtCheckin() { return tariffAtCheckin; }

    public void setTokenNumber(int tokenNumber) { this.tokenNumber = tokenNumber; }
    public int getTokenNumber() { return tokenNumber; }
}
