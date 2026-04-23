package com.hotel.controller;

import java.math.BigDecimal;

public class ReservationRequest {
    public String guestName;
    public String bedType;
    public String acType;
    public int durationNights;
    public BigDecimal advancePaid;
    public Integer loyaltyId; // null if guest has no loyalty ID

    public ReservationRequest(String guestName, String bedType, String acType,
                              int durationNights, BigDecimal advancePaid, Integer loyaltyId) {
        this.guestName = guestName;
        this.bedType = bedType;
        this.acType = acType;
        this.durationNights = durationNights;
        this.advancePaid = advancePaid;
        this.loyaltyId = loyaltyId;
    }
}
