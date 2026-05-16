package GameDisplay;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import GameEntity.PlayerType;
import Map.Difficulty;
import Map.WildernessMap;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame frame = new JFrame("WSS - Character Selection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JComboBox<PlayerType> pTypeBox = new JComboBox<>(PlayerType.values());
        JComboBox<Difficulty> dTypeBox = new JComboBox<>(Difficulty.values());
        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Manual", "Automatic"});
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(14, 5, 100, 1));
        JButton startBtn = new JButton("Launch Simulation");

        frame.add(new JLabel("Select Player Type:"));
        frame.add(pTypeBox);
        frame.add(new JLabel("Select Difficulty:"));
        frame.add(dTypeBox);
        frame.add(new JLabel("Map Width:"));
        frame.add(widthSpinner);
        frame.add(new JLabel("Map Height:"));
        frame.add(heightSpinner);
        frame.add(new JLabel("Select Control Mode:"));
        frame.add(modeBox);
        frame.add(startBtn);

        startBtn.addActionListener(e -> {
            PlayerType selected = (PlayerType) pTypeBox.getSelectedItem(); // return selected player type from menu
            Difficulty selectedDifficulty = (Difficulty) dTypeBox.getSelectedItem();
            boolean isAuto = modeBox.getSelectedItem().equals("Automatic");
            int mapWidth = (Integer) widthSpinner.getValue();
            int mapHeight = (Integer) heightSpinner.getValue();

            try {
                Path exportPath = Paths.get("res", "MapLayout", "tileMap.txt");
                WildernessMap generatedMap = new WildernessMap(mapWidth, mapHeight, selectedDifficulty);
                generatedMap.exportToTileMapFile(exportPath);

                frame.dispose(); // Close selection window

                GameWindow gameWindow = new GameWindow();
                gameWindow.startGame(selected, mapWidth, mapHeight, isAuto); //call game window to start game (with selections)
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Could not generate the map file:\n" + ioException.getMessage(),
                        "Map Generation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
