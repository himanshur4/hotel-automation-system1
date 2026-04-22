package com.hotel.views;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        // 1. Set up the main window properties
        setTitle("Hotel Automation System - Level 2 DFD Implementation");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window on your screen

        // 2. Create the Tabbed Pane for navigation
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        
        JPanel dashboardPanel = new DashboardPanel();
        JPanel bookingPanel = new BookingPanel();
        JPanel cateringPanel = new CateringPanel();
        JPanel checkoutPanel = new CheckoutPanel();

        // 4. Add the panels to the tabbed pane
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Bookings", bookingPanel);
        tabbedPane.addTab("Catering", cateringPanel);
        tabbedPane.addTab("Checkout", checkoutPanel);

        // 5. Add the tabbed pane to the main frame
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Helper method to generate simple placeholder panels until we build the real ones
    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new GridBagLayout()); // Centers components
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(label);
        return panel;
    }
}