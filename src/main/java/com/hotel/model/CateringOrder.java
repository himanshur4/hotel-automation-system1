package com.hotel.model;

import java.math.BigDecimal;

public class CateringOrder {
    private String foodItem;
    private int quantity;
    private BigDecimal unitPrice;

    public CateringOrder(String foodItem, int quantity, BigDecimal unitPrice) {
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getFoodItem() { return foodItem; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}
