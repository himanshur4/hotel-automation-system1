package com.hotel.ui;

import com.hotel.controller.DispatchTransaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CateringPanel extends JPanel {

    private DispatchTransaction dispatcher = new DispatchTransaction();

    private JTextField tokenField;
    private JComboBox<String> foodItemCombo;
    private JTextField quantityField;

    public CateringPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Room Service & Catering");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        tokenField = new JTextField(10);
        quantityField = new JTextField("1", 5);

        // Food items are loaded live from the database so the menu stays up to date
        foodItemCombo = new JComboBox<>();
        List<String> menuItems = dispatcher.getMenu();
        if (menuItems.isEmpty()) {
            foodItemCombo.addItem("No menu items configured");
        } else {
            for (String item : menuItems) {
                foodItemCombo.addItem(item);
            }
        }

        JButton orderButton = new JButton("Add to Guest Bill");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; add(new JLabel("Guest Token Number:"), gbc);
        gbc.gridx = 1; add(tokenField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Select Food Item:"), gbc);
        gbc.gridx = 1; add(foodItemCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        orderButton.setPreferredSize(new Dimension(200, 40));
        add(orderButton, gbc);

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitOrder();
            }
        });
    }

    private void submitOrder() {
        try {
            int token = Integer.parseInt(tokenField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            String foodItem = (String) foodItemCombo.getSelectedItem();

            if (foodItem == null || foodItem.equals("No menu items configured")) {
                JOptionPane.showMessageDialog(this, "Menu is empty. Cannot place order.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = dispatcher.handleCateringOrder(token, foodItem, quantity);

            if (result.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, result, "Order Placed", JOptionPane.INFORMATION_MESSAGE);
                quantityField.setText("1");
            } else {
                JOptionPane.showMessageDialog(this, result, "Order Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Token and Quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
