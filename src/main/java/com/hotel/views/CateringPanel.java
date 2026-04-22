package com.hotel.views;

import com.hotel.controllers.CateringController;
import javax.swing.*;
import java.awt.*;

public class CateringPanel extends JPanel {

    public CateringPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // UI Components
        JTextField tokenField = new JTextField(15);
        JTextField foodItemField = new JTextField(15);
        JTextField quantityField = new JTextField(15);
        JButton submitOrderButton = new JButton("Add Order to Tab");

        // Layout Construction
        int row = 0;
        addFormRow("Guest Token Number:", tokenField, gbc, row++);
        addFormRow("Food Item Name:", foodItemField, gbc, row++);
        addFormRow("Quantity:", quantityField, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(submitOrderButton, gbc);

        // Button Click Event
        submitOrderButton.addActionListener(e -> {
            try {
                int tokenNo = Integer.parseInt(tokenField.getText());
                String foodItem = foodItemField.getText();
                int quantity = Integer.parseInt(quantityField.getText());

                if (foodItem.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a food item.");
                    return;
                }

                // Call Controller
                String result = CateringController.addFoodOrder(tokenNo, foodItem, quantity);

                // Show result to user
                JOptionPane.showMessageDialog(this, result);

                // Clear form on success
                if(result.startsWith("Success")) {
                    tokenField.setText("");
                    foodItemField.setText("");
                    quantityField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Token and Quantity.");
            }
        });
    }

    private void addFormRow(String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        add(component, gbc);
    }
}