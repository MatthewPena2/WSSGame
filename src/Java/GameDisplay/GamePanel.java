package GameDisplay;

import GameEntity.Player;
import GameEntity.PlayerType;
import TileMap.WildernessMapManager;

import javax.swing.*;
import java.awt.*;

//GamePanel class will work as a game screen (displaying/drawing)
public class GamePanel extends JPanel implements Runnable{

    //Display settings
    final int tileSizeInPixels = 16; //each tile, player icon, item, entity, etc. will be 16 by 16 pixels
    final int tileSizeScale = 3; //blows up the tile image to make it bigger on screen

    public final int tileSize = tileSizeInPixels * tileSizeScale; //16 * 3 = 48; 48 by 48
    public final int maxScreenCol = 20; //20 tiles across; 960 pixels across
    public final int maxScreenRow = 14; //14 tiles down; 672 pixels down
    public final int screenWidth = tileSize * maxScreenCol; //width is in pixels displayed on screen
    public final int statUIHeight = tileSize * 2; //space dedicated to stat screen
    public final int screenHeight = (tileSize * maxScreenRow) + statUIHeight; //height is in pixels displayed on screen
    //When it comes to drawing on a screen, the game panel will be using the player's position in pixels

    //FPS Cap = 60 FPS
    //used to tell the game panel how many times per second to update the display window (calling update()/repaint())
    int FPS = 60;

    //Collision Checker (for terrain/trader)
    public CollisionChecker collChecker = new CollisionChecker(this);
    //Map Manager
    WildernessMapManager tileM = new WildernessMapManager(this);
    //KeyHandler to manage user input for player action
    KeyHandler keyH = new KeyHandler();
    //gameThread thread is going to manage the uptime of the game (when the game is active)
    Thread gameThread;
    //Create the player object
    Player player;


    //GamePanel constructor
    public GamePanel(PlayerType selectedType){
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); //creates a window of width pixels by height pixels
        this.setBackground(Color.LIGHT_GRAY); //TEMPORARY game window color
        this.setDoubleBuffered(true); //all drawing/redrawing is done in a separate buffer (improves rendering performance)
        this.addKeyListener(keyH); //listens for user input from keyboard
        this.setFocusable(true); //GamePanel will focus on receiving keyboard input; POTENTIALLY REMOVE LATER

        this.player = new Player(this, keyH, selectedType); //create player object based on the chosen player type
    }

    public void startGameThread(){
        gameThread = new Thread(this); //thread will be running this panel, which has the game loop
        gameThread.start(); //.start() will call run(), which is the game loop
    }
    //automatically included when implementing Runnable class; called to execute the game loop
    @Override
    public void run() {
        //This game loop will accomplish two things:
        //1. Updating player position
        //2. Redrawing the game as it receives new information

        //Drawing Intervals - We only want the game drawing at 60 FPS
        //if we leave it as the system default, the player marker will move too fast
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){ //as long as this game thread exists, execute the loop
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint(); //calls paintComponent
                delta--;
            }
        }
    }

    //used in run()
    public void update(){
        //call player update() to update movement
        player.update();

        //check if player has reached the right-hand side of the screen (wins the game)
        if(player.MapX + tileSize >= screenWidth){
            System.out.println("Player wins! The player has made it to the other side of the screen.");
            gameThread = null; //freeze game upon win
        }

    }

    //used in paintComponent to display player stats at the bottom of the screen
    public void drawStatUI(Graphics2D g2){
        g2.setColor(Color.black);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        int uiY = (tileSize * maxScreenRow) + statUIHeight/2;
        int spacing = 200;

        g2.drawString("Food: " + (int)player.foodAmount, 30, uiY);
        g2.drawString("Water: " + (int)player.waterAmount, 30 + spacing, uiY);
        g2.drawString("Strength: " + (int)player.strength, 30 + (spacing * 2), uiY);
    }

    //used in run()
    //Basically overriding JPanel paintComponent() to redraw the game; called using repaint();
    public void paintComponent(Graphics g){ //uses Graphics
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g; //cast g as a Graphics2D variable (Graphics2D has more components than Graphics)
        //call map manager
        tileM.draw(g2);
        //call player draw() to draw player on the panel
        player.draw(g2);
        //call drawStatUI() to display the player's resources
        drawStatUI(g2);

        g2.dispose(); //when drawing is done, release any resources being used up
    }

}
