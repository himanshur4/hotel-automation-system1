package com.hotel.logic;

import com.hotel.db.DBAccesslayer;
import java.util.List;

public class RecordCateringOrder {

    private DBAccesslayer db = new DBAccesslayer();

    public List<String> getAvailableMenu() {
        return db.getMenuFoodItems();
    }

    public String processOrder(int tokenNumber, String foodItem, int quantity) {
        if (!db.isTokenActive(tokenNumber)) {
            return "ERROR: Invalid Token. Guest is not checked in or token does not exist.";
        }

        if (quantity <= 0) {
            return "VALIDATION ERROR: Quantity must be at least 1.";
        }

        boolean success = db.createCateringOrder(tokenNumber, foodItem, quantity);

        if (success) {
            return "SUCCESS: Added " + quantity + "x " + foodItem + " to Token " + tokenNumber;
        } else {
            return "ERROR: Failed to save catering order to database.";
        }
    }
}
