package com.hotel.views;

import com.hotel.controllers.CheckoutController;
import javax.swing.*;
import java.awt.*;

public class CheckoutPanel extends JPanel {

    public CheckoutPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        JLabel tokenLabel = new JLabel("Enter Guest Token No:");
        tokenLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField tokenField = new JTextField(10);
        JButton checkoutButton = new JButton("Generate Bill & Checkout");

        controlPanel.add(tokenLabel);
        controlPanel.add(tokenField);
        controlPanel.add(checkoutButton);

        // Receipt Display Area
        JTextArea receiptArea = new JTextArea();
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Monospaced keeps columns aligned
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button Click Event
        checkoutButton.addActionListener(e -> {
            try {
                int tokenNo = Integer.parseInt(tokenField.getText());

                // Call Controller
                String billReceipt = CheckoutController.processCheckout(tokenNo);

                // Display Bill
                receiptArea.setText(billReceipt);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Token Number.");
            }
        });
    }
}