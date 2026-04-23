package com.hotel.model;

public class Room {
    private int roomNumber;
    private String bedType;
    private String acType;
    private String status;

    public Room(int roomNumber, String bedType, String acType, String status) {
        this.roomNumber = roomNumber;
        this.bedType = bedType;
        this.acType = acType;
        this.status = status;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getBedType() { return bedType; }
    public String getAcType() { return acType; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
