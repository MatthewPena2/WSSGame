package GameDisplay;

import javax.swing.*;
import java.awt.*;
import Map.PlayerType;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("WSS - Character Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField nameField = new JTextField("Orobosa", 15);
        JComboBox<PlayerType> typeBox = new JComboBox<>(PlayerType.values());
        JButton startBtn = new JButton("Launch Simulation");

        frame.add(new JLabel("Enter Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Select Type:"));
        frame.add(typeBox);
        frame.add(startBtn);

        startBtn.addActionListener(e -> {
            frame.dispose(); // Close selection window
            // This calls the code your teammate wrote!
            GameWindow.main(null); 
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}