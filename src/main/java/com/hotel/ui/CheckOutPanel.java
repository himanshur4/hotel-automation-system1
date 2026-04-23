package com.hotel.ui;

import com.hotel.controller.DispatchTransaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CheckOutPanel extends JPanel {

    private DispatchTransaction dispatcher = new DispatchTransaction();

    private JTextField tokenField;
    private JTextArea billDisplayArea;
    private JButton checkOutButton;

    public CheckOutPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));

        JLabel tokenLabel = new JLabel("Enter Guest Token Number:");
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 14));

        tokenField = new JTextField(10);
        tokenField.setFont(new Font("Arial", Font.PLAIN, 14));

        checkOutButton = new JButton("Process Check-Out & Generate Bill");

        inputPanel.add(tokenLabel);
        inputPanel.add(tokenField);
        inputPanel.add(checkOutButton);

        billDisplayArea = new JTextArea();
        billDisplayArea.setEditable(false);
        billDisplayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        billDisplayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(billDisplayArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Generated Bill"));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCheckOut();
            }
        });
    }

    private void processCheckOut() {
        try {
            int token = Integer.parseInt(tokenField.getText().trim());
            String result = dispatcher.handleCheckOut(token);
            billDisplayArea.setText(result);

            if (result.startsWith("ERROR") || result.startsWith("VALIDATION ERROR")) {
                JOptionPane.showMessageDialog(this, "Failed to generate bill. Check the display for details.",
                        "Check-Out Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Check-Out Successful! Room is now available.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                tokenField.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric Token Number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
