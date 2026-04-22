package com.hotel.views;

import com.hotel.controllers.BookingController;
import javax.swing.*;
import java.awt.*;

public class BookingPanel extends JPanel {

    public BookingPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // UI Components
        JTextField nameField = new JTextField(20);
        JTextField idField = new JTextField(20); // For frequent guest discount [cite: 124]
        JTextField advanceField = new JTextField(20);
        JTextField durationField = new JTextField(20);

        String[] bedTypes = {"Single", "Double"};
        JComboBox<String> bedBox = new JComboBox<>(bedTypes);

        String[] acTypes = {"AC", "Non-AC"};
        JComboBox<String> acBox = new JComboBox<>(acTypes);

        JButton submitButton = new JButton("Book Room");

        // Layout Construction
        int row = 0;
        addFormRow("Guest Name:", nameField, gbc, row++);
        addFormRow("Identity Number (Optional):", idField, gbc, row++);
        addFormRow("Advance Paid (Rs):", advanceField, gbc, row++);
        addFormRow("Expected Duration (Days):", durationField, gbc, row++);
        addFormRow("Bed Type:", bedBox, gbc, row++);
        addFormRow("AC/Non-AC:", acBox, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(submitButton, gbc);

        // Button Click Event
        submitButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String id = idField.getText();
                double advance = Double.parseDouble(advanceField.getText());
                int duration = Integer.parseInt(durationField.getText());
                String bed = (String) bedBox.getSelectedItem();
                String ac = (String) acBox.getSelectedItem();

                // Call Controller
                String result = BookingController.bookGuest(name, id, advance, duration, bed, ac);

                // Show result to user
                JOptionPane.showMessageDialog(this, result);

                // Clear form on success
                if(result.startsWith("Success")) {
                    nameField.setText(""); idField.setText("");
                    advanceField.setText(""); durationField.setText("");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Advance and Duration.");
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