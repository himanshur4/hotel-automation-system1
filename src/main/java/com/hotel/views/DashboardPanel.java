package com.hotel.views;

import com.hotel.controllers.DashboardController;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private JLabel occupancyLabel;

    public DashboardPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Occupancy Display
        occupancyLabel = new JLabel("Current Occupancy Rate: --%");
        occupancyLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton refreshButton = new JButton("Refresh Stats");

        // Tariff Adjustment Controls
        JLabel adjustLabel = new JLabel("Revise Room Tariffs (%):");
        JTextField percentField = new JTextField(10);
        percentField.setToolTipText("Use negative numbers for discounts (e.g., -10)");
        JButton applyButton = new JButton("Apply Tariff Change");

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(occupancyLabel, gbc);

        gbc.gridy = 1;
        add(refreshButton, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1;
        add(adjustLabel, gbc);

        gbc.gridx = 1;
        add(percentField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(applyButton, gbc);

        // Actions
        refreshButton.addActionListener(e -> updateOccupancyDisplay());

        applyButton.addActionListener(e -> {
            try {
                double percent = Double.parseDouble(percentField.getText());
                String result = DashboardController.adjustTariff(percent);
                JOptionPane.showMessageDialog(this, result);
                percentField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number (e.g., 15 or -10).");
            }
        });

        // Initial load
        updateOccupancyDisplay();
    }

    private void updateOccupancyDisplay() {
        double rate = DashboardController.getOccupancyRate();
        occupancyLabel.setText(String.format("Current Occupancy Rate: %.1f%%", rate));
    }
}