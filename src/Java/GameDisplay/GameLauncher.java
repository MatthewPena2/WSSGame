package GameDisplay;

import javax.swing.*;
import java.awt.*;
import GameEntity.PlayerType;
import Map.Difficulty;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("WSS - Character Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JTextField nameField = new JTextField(10);
        JComboBox<PlayerType> pTypeBox = new JComboBox<>(PlayerType.values());
        JComboBox<Difficulty> dTypeBox = new JComboBox<>(Difficulty.values());
        JButton startBtn = new JButton("Launch Simulation");

        frame.add(new JLabel("Enter Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Select Player Type:"));
        frame.add(pTypeBox);
        frame.add(new JLabel("Select Difficulty:"));
        frame.add(dTypeBox);
        frame.add(startBtn);

        startBtn.addActionListener(e -> {
            PlayerType selected = (PlayerType) pTypeBox.getSelectedItem(); // return selected player type from menu
            frame.dispose(); // Close selection window

            GameWindow gameWindow = new GameWindow();
            gameWindow.startGame(selected); //call game window to start game (with selections)
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}