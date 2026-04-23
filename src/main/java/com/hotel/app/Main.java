package com.hotel.app;

import com.hotel.ui.AdminPanel;
import com.hotel.ui.CateringPanel;
import com.hotel.ui.CheckOutPanel;
import com.hotel.ui.ReceptionPanel;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hotel Automation Software");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Reception (Booking)", new ReceptionPanel());
            tabbedPane.addTab("Catering", new CateringPanel());
            tabbedPane.addTab("Check-Out", new CheckOutPanel());
            tabbedPane.addTab("Manager (Admin)", new AdminPanel());
            frame.add(tabbedPane);
            frame.setVisible(true);
        });
    }
}
