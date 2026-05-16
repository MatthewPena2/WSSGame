package GameDisplay;

import GameEntity.PlayerType;

import javax.swing.*;

//This class will launch the game window
public class GameWindow {
    public void startGame(PlayerType selectedPlayer, int mapColumns, int mapRows, boolean isAuto){
        JFrame window = new JFrame(); //create a new window to display the game
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits the program when clicking the little 'x'
        window.setResizable(false); //can't resize window
        window.setTitle("Wilderness Survival Game"); // game title

        GamePanel gamePanel = new GamePanel(selectedPlayer, mapColumns, mapRows, isAuto); //creates new game panel with selected player type
        window.add(gamePanel); //add the panel to the window

        window.pack(); //packs the contents of GamePanel to their optimal size in the generated window

        window.setLocationRelativeTo(null); //window is set at the center of the screen
        window.setVisible(true); //can see the window

        gamePanel.startGameThread(); //Call the game loop
    }
}
