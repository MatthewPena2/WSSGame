package GameDisplay;

import GameEntity.Item;
import GameEntity.Player;
import GameEntity.PlayerType;
import GameEntity.Trader;
import TileMap.WildernessMapManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//GamePanel class will work as a game screen (displaying/drawing)
public class GamePanel extends JPanel implements Runnable{
    private static final int MIN_MAP_COLUMNS = 5;
    private static final int MIN_MAP_ROWS = 5;

    //Display settings
    final int tileSizeInPixels = 16; //each tile, player icon, item, entity, etc. will be 16 by 16 pixels
    final int tileSizeScale = 3; //blows up the tile image to make it bigger on screen

    public final int tileSize = tileSizeInPixels * tileSizeScale; //16 * 3 = 48; 48 by 48
    public final int maxScreenCol; //dynamic map width in tiles
    public final int maxScreenRow; //dynamic map height in tiles
    public final int statUIHeight = tileSize * 2; //space dedicated to stat screen
    public final int screenWidth; //width is in pixels displayed on screen
    public final int screenHeight; //height is in pixels displayed on screen
    public boolean canTrade = true; //used to determine trading cooldowns
    //When it comes to drawing on a screen, the game panel will be using the player's position in pixels

    //FPS Cap = 60 FPS
    //used to tell the game panel how many times per second to update the display window (calling update()/repaint())
    int FPS = 60;

    //Collision Checker (for terrain/trader)
    public final CollisionChecker collChecker;
    //Map Manager
    public final WildernessMapManager tileM;
    //Key Handler Object
    KeyHandler keyH;
    //gameThread thread is going to manage the uptime of the game (when the game is active)
    Thread gameThread;
    //Create the player object
    private final Player player;
    //Create new trader object
    public Trader trader;
    //Create item collection
    public List<Item> items;


    //GamePanel constructor - initializes all variables declared above, and then some
    public GamePanel(PlayerType selectedType, int mapColumns, int mapRows){
        this.maxScreenCol = Math.max(MIN_MAP_COLUMNS, mapColumns);
        this.maxScreenRow = Math.max(MIN_MAP_ROWS, mapRows);
        this.screenWidth = tileSize * maxScreenCol;
        this.screenHeight = (tileSize * maxScreenRow) + statUIHeight;
        this.collChecker = new CollisionChecker(this);
        this.tileM = new WildernessMapManager(this);
        this.keyH = new KeyHandler(); //KeyHandler to manage user input for player action
        this.trader = new Trader(this); //initialize new trader
        this.items = spawnItems(); //initialize map items
        this.player = new Player(this, keyH, selectedType); //create player object based on the chosen player type
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); //creates a window of width pixels by height pixels
        this.setBackground(Color.LIGHT_GRAY); //TEMPORARY game window color
        this.setDoubleBuffered(true); //all drawing/redrawing is done in a separate buffer (improves rendering performance)
        this.addKeyListener(keyH); //listens for user input from keyboard
        this.setFocusable(true); //GamePanel will focus on receiving keyboard input; POTENTIALLY REMOVE LATER
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
        collectItemsAtPlayer();

        tradeInteraction();

        //check if player has reached the right-hand side of the screen (wins the game)
        if(player.MapX + tileSize >= screenWidth){
            System.out.println("Player wins! The player has made it to the other side of the screen.");
            gameThread = null; //freeze game upon win
        }

        //check if player has run out of food/water (loses the game)
        if(player.foodAmount <= 0 || player.waterAmount <= 0){
            System.out.println("Player has died! Remember to eat well and stay hydrated!");
            gameThread = null;
        }

    }

    public void tradeInteraction(){
        int objIndex = collChecker.checkObjectCollision(player, trader);
        if(objIndex != -1 && canTrade){
            canTrade = false; //set trade cooldown
            keyH.resetKeys(); //stop all player movement when trading

            //Define choices for Trade (ask player what they want to buy)
            String[] items = {"Food", "Water", "Leave"};

            int itemChoice = JOptionPane.showOptionDialog(this, "Trader: What do you need?",
                    "Trade", 0, JOptionPane.QUESTION_MESSAGE, null, items, items[0]);

            if(itemChoice == 0 || itemChoice == 1){
                String selectedItem = items[itemChoice];

                //Ask player for gold
                String input = JOptionPane.showInputDialog(this,
                        "What are you offering for " + selectedItem + "?");

                if(input != null){
                    try{
                        //Evaluate player offer
                        int offer = Integer.parseInt(input);
                        if(offer > player.goldAmount) JOptionPane.showMessageDialog(this, "You're broke. Leave!");
                        else if(trader.evaluateOffer(selectedItem, offer)){
                            player.goldAmount -= offer;
                            if(selectedItem.equals("Food")) player.foodAmount += 20;
                            if(selectedItem.equals("Water")) player.waterAmount += 15;

                            JOptionPane.showMessageDialog(this, "Trader: Deal!");
                        }else{
                            JOptionPane.showMessageDialog(this, "Seriously? Get lost.");
                        }

                    }catch(NumberFormatException e){
                        JOptionPane.showMessageDialog(this, "Speak clearly (enter a number)");
                    }
                }
            }
        }

        if(objIndex == -1) canTrade = true; //rest trade cooldown when player leaves trader hitbox
    }

    private List<Item> spawnItems() {
        List<Item> generatedItems = new ArrayList<Item>();
        Random random = new Random();
        int mapArea = maxScreenCol * maxScreenRow;
        int maxItems = Math.max(1, mapArea / 150);

        for (int row = 0; row < maxScreenRow && generatedItems.size() < maxItems; row++) {
            for (int col = 0; col < maxScreenCol && generatedItems.size() < maxItems; col++) {
                int tileNum = tileM.mapTileNum[col][row];
                if (tileM.tile[tileNum].collision) {
                    continue;
                }
                if (trader.MapX / tileSize == col && trader.MapY / tileSize == row) {
                    continue;
                }
                if (random.nextDouble() < 0.1) {
                    generatedItems.add(new Item(col, row, randomItemType(random)));
                }
            }
        }

        return generatedItems;
    }

    private Item.ItemType randomItemType(Random random) {
        double roll = random.nextDouble();
        if (roll < 0.30) {
            return Item.ItemType.FOOD;
        }
        if (roll < 0.60) {
            return Item.ItemType.WATER;
        }
        return Item.ItemType.GOLD;
    }

    private void collectItemsAtPlayer() {
        int centerX = player.MapX + (tileSize / 2);
        int centerY = player.MapY + (tileSize / 2);
        int col = centerX / tileSize;
        int row = centerY / tileSize;

        for (Item item : items) {
            if (!item.isCollected() && item.isOnTile(col, row)) {
                item.collect(player);
            }
        }
    }

    //used in drawStatUI - gets the terrain the player on based on the player's center
    public String getCurrentTerrain(){
        //Calculate the center of the player
        int centerX = player.MapX + (tileSize/2);
        int centerY = player.MapY + (tileSize/2);

        //Convert the pixel coordinates into array indices
        //The map is simply an array of integers corresponding to Tile types
        int col = centerX / tileSize;
        int row = centerY / tileSize;

        //In-bounds safety check
        if(col >= 0 && col < maxScreenCol && row >= 0 && row < maxScreenRow){
            //get the tile number of the tile at [col][row] - tileNumber = type of terrain (0 = Plains, 1 = Desert, etc.)
            int tileNum = tileM.mapTileNum[col][row];
            return tileM.tile[tileNum].name; //return the name of the terrain associated with that tile number
        }
        return "Unknown"; //if above check fails, return "Unknown"
    }

    //used in paintComponent to display player stats at the bottom of the screen
    public void drawStatUI(Graphics2D g2){
        g2.setColor(Color.black);
        g2.setFont(new Font("Arial", Font.BOLD, 20));

        int uiY = (tileSize * maxScreenRow) + statUIHeight/2;
        int spacing = 150;

        //display existing stats
        g2.drawString("Food: " + (int)player.foodAmount, 30, uiY);
        g2.drawString("Water: " + (int)player.waterAmount, 30 + spacing, uiY);
        g2.drawString("Strength: " + (int)player.strength, 30 + (spacing * 2), uiY);
        g2.setColor(Color.YELLOW);
        g2.drawString("Gold: " + player.goldAmount, 40 + (spacing * 3), uiY);

        //Display the current terrain
        g2.setColor(Color.black);
        g2.drawString("Terrain: " + getCurrentTerrain(), 30 + (spacing * 4), uiY);
    }

    //used in run()
    //Basically overriding JPanel paintComponent() to redraw the game; called using repaint();
    public void paintComponent(Graphics g){ //uses Graphics
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g; //cast g as a Graphics2D variable (Graphics2D has more components than Graphics)
        //call map manager
        tileM.draw(g2);
        //draw collectible items as colored tile-corner markers
        for (Item item : items) {
            item.draw(g2, tileSize);
        }
        //call trader drawTrader() to draw trader on the panel
        trader.drawTrader(g2);
        //call player draw() to draw player on the panel
        player.draw(g2);
        //call drawStatUI() to display the player's resources
        drawStatUI(g2);

        g2.dispose(); //when drawing is done, release any resources being used up
    }

}
