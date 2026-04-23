package com.hotel.ui;

import com.hotel.controller.DispatchTransaction;
import com.hotel.controller.ReservationRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class ReceptionPanel extends JPanel {

    private DispatchTransaction dispatcher = new DispatchTransaction();

    private JTextField nameField;
    private JComboBox<String> bedTypeCombo;
    private JComboBox<String> acTypeCombo;
    private JTextField durationField;
    private JTextField advanceField;
    private JTextField loyaltyField;

    public ReceptionPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Guest Room Reservation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        nameField = new JTextField(15);
        bedTypeCombo = new JComboBox<>(new String[]{"Single", "Double"});
        acTypeCombo = new JComboBox<>(new String[]{"AC", "Non-AC"});
        durationField = new JTextField(5);
        advanceField = new JTextField("0.00", 10);
        loyaltyField = new JTextField(10);
        loyaltyField.setToolTipText("Leave blank if not a frequent guest");

        JButton bookButton = new JButton("Book Room");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 1; add(new JLabel("Guest Name:"), gbc);
        gbc.gridx = 1; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Bed Type:"), gbc);
        gbc.gridx = 1; add(bedTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("AC Type:"), gbc);
        gbc.gridx = 1; add(acTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Duration (Nights):"), gbc);
        gbc.gridx = 1; add(durationField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(new JLabel("Advance Paid ($):"), gbc);
        gbc.gridx = 1; add(advanceField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; add(new JLabel("Loyalty ID (Optional):"), gbc);
        gbc.gridx = 1; add(loyaltyField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        bookButton.setPreferredSize(new Dimension(200, 40));
        add(bookButton, gbc);

        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBooking();
            }
        });
    }

    private void handleBooking() {
        try {
            String name = nameField.getText();
            String bed = (String) bedTypeCombo.getSelectedItem();
            String ac = (String) acTypeCombo.getSelectedItem();
            int duration = Integer.parseInt(durationField.getText());
            BigDecimal advance = new BigDecimal(advanceField.getText());

            Integer loyaltyId = null;
            if (!loyaltyField.getText().trim().isEmpty()) {
                loyaltyId = Integer.parseInt(loyaltyField.getText().trim());
            }

            ReservationRequest req = new ReservationRequest(name, bed, ac, duration, advance, loyaltyId);
            String result = dispatcher.handleReservation(req);

            if (result.startsWith("SUCCESS")) {
                JOptionPane.showMessageDialog(this, result, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else if (result.startsWith("APOLOGY")) {
                JOptionPane.showMessageDialog(this, result, "Apology - No Rooms", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Duration, Advance, and Loyalty ID.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        durationField.setText("");
        advanceField.setText("0.00");
        loyaltyField.setText("");
    }
}
