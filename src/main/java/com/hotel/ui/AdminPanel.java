package com.hotel.ui;

import com.hotel.controller.DispatchTransaction;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {

    private DispatchTransaction dispatcher = new DispatchTransaction();

    public AdminPanel() {
        setLayout(new GridLayout(1, 2, 20, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(createTariffPanel());
        add(createOccupancyPanel());
    }

    private JPanel createTariffPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Revise Room Tariffs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> bedTypeCombo = new JComboBox<>(new String[]{"Single", "Double"});
        JComboBox<String> acTypeCombo = new JComboBox<>(new String[]{"AC", "Non-AC"});
        JTextField pctField = new JTextField("10.0", 5);
        JButton updateBtn = new JButton("Apply % Adjustment");

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Bed Type:"), gbc);
        gbc.gridx = 1; panel.add(bedTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("AC Type:"), gbc);
        gbc.gridx = 1; panel.add(acTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Adjustment % (+/-):"), gbc);
        gbc.gridx = 1; panel.add(pctField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                double pct = Double.parseDouble(pctField.getText().trim());
                String result = dispatcher.handleTariffAdjustment((String)bedTypeCombo.getSelectedItem(), (String)acTypeCombo.getSelectedItem(), pct);
                JOptionPane.showMessageDialog(this, result);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid numeric percentage.");
            }
        });
        return panel;
    }

    private JPanel createOccupancyPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Occupancy Report"));

        JPanel top = new JPanel(new FlowLayout());
        JComboBox<Integer> monthCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        JTextField yearField = new JTextField("2026", 4);
        JButton genBtn = new JButton("Generate");

        top.add(new JLabel("Month:")); top.add(monthCombo);
        top.add(new JLabel("Year:")); top.add(yearField);
        top.add(genBtn);

        JTextArea display = new JTextArea();
        display.setEditable(false);
        display.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(display), BorderLayout.CENTER);

        genBtn.addActionListener(e -> {
            try {
                int y = Integer.parseInt(yearField.getText().trim());
                int m = (Integer) monthCombo.getSelectedItem();
                display.setText(dispatcher.handleOccupancyReport(y, m));
            } catch (Exception ex) {
                display.setText("Invalid Year.");
            }
        });
        return panel;
    }
}