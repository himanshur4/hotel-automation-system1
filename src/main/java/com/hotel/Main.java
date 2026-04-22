package com.hotel;

import javax.swing.SwingUtilities;
import com.hotel.views.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Hotel Automation System initializing...");

            // Launching the GUI
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}