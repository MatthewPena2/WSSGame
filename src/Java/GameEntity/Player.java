package GameEntity;

import Brain.DefaultBrain;
import GameDisplay.GamePanel;
import GameDisplay.KeyHandler;
import Map.Direction;
import Map.Position;
import Vision.VisionManager;
import Vision.Vision;
import Brain.Brain;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends Entity{
    //Use the GamePanel and KeyHandler classes
    //Instead of drawing everything in the Game Panel class, the Player class will have
    //its own update() and draw() methods, which will be used to tell the Game Panel
    //to update its position in the window

    //DECLARE VARIABLES
    public GamePanel gp;
    KeyHandler keyH;
    PlayerType type;
    private Brain brain;
    private Vision playerVision;
    public boolean automaticMode;
    public double maxStrength,maxFood, maxWater; //max stats based on player type
    public double currentFood, currentWater, currentStrength;
    public int goldAmount = 0; //player starts with 0 gold
    public int speed = 0; //default speed used to traverse the map
    //The player moves in pixels
    //A speed of 3 means the player will move 3 pixels everytime an appropriate movement key is pressed

    public Player(GamePanel panel, KeyHandler kHandle, PlayerType selectedType, boolean isAuto){
        gp = panel; //pass the game panel
        keyH = kHandle; //pass the key handler
        this.type = selectedType; //pass the selected player type
        this.automaticMode = isAuto;
        setStartingSuppliesAndStrength(); //initiate the player supplies
        setDefaultPositionAndSpeed(); //set Player default position on the map and their speed
        getPlayerImages(); //get player pixel art

        Position gridPos = new Position(MapX/gp.tileSize, MapY/ gp.tileSize);
        this.playerVision = VisionManager.create(type.getVision(), gridPos, gp.worldMap);
        if(this.automaticMode){
            this.brain = new DefaultBrain(this, this.playerVision);
        }

        hitbox = new Rectangle(8, 16, 28, 28);
    }

    //Mutator method for initializing the player supplies and strength
    public void setStartingSuppliesAndStrength(){
        maxFood = type.getStartFood();
        maxWater = type.getStartWater();
        maxStrength = type.getStartStrength();
        
        currentFood = maxFood;
        currentWater = maxWater;
        currentStrength = maxStrength;
    }

    //Mutator method for position x, y and speed
    public void setDefaultPositionAndSpeed(){
        MapX = 0; //x coordinate in the window
        MapY = 336; //y coordinate in the window
        entitySize = 48;
        direction = "down"; //spawned-in direction

        switch (type) {
            case EXPLORER:
                speed = 5;  // Fast movement for scouting
                break;
            case SURVIVOR:
                speed = 3;  // Standard speed, but maybe they'll have more food later
                break;
            case WARRIOR:
                speed = 2;  // Slower because they are "heavy" or high strength
                break;
            case ADVENTURER:
                speed = 4;  // Balanced speed for versatility
                break;
            default:
                speed = 3;
                break;
        }
    }

    //Help Function - get the current tile index
    public int getCurrentTileIndex(){
        int col = (MapX + hitbox.x + hitbox.width / 2)/ gp.tileSize;
        int row = (MapY + hitbox.y + hitbox.height / 2)/ gp.tileSize;

        //check map safety bounds
        if (col >= 0 && col < gp.maxScreenCol && row >= 0 && row < gp.maxScreenRow)
            return gp.tileM.mapTileNum[col][row];

        return 0; //default to Plains
    }

    //Update position
    public void update(){

        if(automaticMode && brain != null){
            Direction move = brain.makeMove();

            if (move == Direction.NORTH) direction = "up";
            if (move == Direction.SOUTH) direction = "down";
            if (move == Direction.WEST)  direction = "left";
            if (move == Direction.EAST)  direction = "right";
        }else{
            if(keyH.pressedUp){
                direction = "up";
            }else if(keyH.pressedDown){
                direction = "down";
            }else if(keyH.pressedLeft){
                direction = "left";
            }else if(keyH.pressedRight){
                direction = "right";
            }else{
                return;
            }
        }

        //Check the tile collision
        collisionOn = false;
        gp.collChecker.checkTileCollision(this);

        //if collision is false, the player can move; if on, the player cannot move
        if(!collisionOn){
            switch(direction){ //only allow the player to move when collision is off
                case "up": MapY -= speed; break;
                case "down": MapY += speed; break;
                case "right": MapX += speed; break;
                case "left": MapX -= speed; break;
            }

            //based on the tile index (terrain), lower stats accordingly
            int tileIndex = getCurrentTileIndex();
            currentFood -= gp.tileM.tile[tileIndex].foodCost;
            currentWater -= gp.tileM.tile[tileIndex].waterCost;
            currentStrength -= gp.tileM.tile[tileIndex].strengthCost;

            //prevent stats from going below zero
            if(currentFood < 0) currentFood = 0;
            if(currentWater < 0) currentWater = 0;
            if(currentStrength < 0) currentStrength = 0;

        }

        spriteCounter++;
        if(spriteCounter > 12){ //update sprite every 12 frames
            if(spriteNumber == 1) spriteNumber = 2;
            else if(spriteNumber == 2) spriteNumber = 1;
            spriteCounter = 0;
        }

        //if WASD is pressed (up, left, down, right), then manage player movement
        /*if(keyH.pressedUp || keyH.pressedDown
            || keyH.pressedLeft || keyH.pressedRight){

            //strength check - player should not be able to move if strength is 0
            //Player is forced to stop moving and rest
            if(currentStrength <= 0){
                System.out.println("You are too tired to move. Rest up!");
                return;
            }

            //manage player movement
            if(keyH.pressedUp){
                direction = "up";
            }else if(keyH.pressedDown){
                direction = "down";
            }else if(keyH.pressedLeft){
                direction = "left";
            }else {
                direction = "right";
            }

            //Check the tile collision
            collisionOn = false;
            gp.collChecker.checkTileCollision(this);

            //if collision is false, the player can move; if on, the player cannot move
            if(!collisionOn){
                switch(direction){ //only allow the player to move when collision is off
                    case "up": MapY -= speed; break;
                    case "down": MapY += speed; break;
                    case "right": MapX += speed; break;
                    case "left": MapX -= speed; break;
                }

                //based on the tile index (terrain), lower stats accordingly
                int tileIndex = getCurrentTileIndex();
                currentFood -= gp.tileM.tile[tileIndex].foodCost;
                currentWater -= gp.tileM.tile[tileIndex].waterCost;
                currentStrength -= gp.tileM.tile[tileIndex].strengthCost;

                //prevent stats from going below zero
                if(currentFood < 0) currentFood = 0;
                if(currentWater < 0) currentWater = 0;
                if(currentStrength < 0) currentStrength = 0;

            }

            spriteCounter++;
            if(spriteCounter > 12){ //update sprite every 12 frames
                if(spriteNumber == 1) spriteNumber = 2;
                else if(spriteNumber == 2) spriteNumber = 1;
                spriteCounter = 0;
            }

        }

         */

    }

    //Get the pixel art for the player
    public void getPlayerImages(){
        try{
            img2 = ImageIO.read(getClass().getResourceAsStream("/PlayerPixelArt/player_down_1.png"));
            img1 = ImageIO.read(getClass().getResourceAsStream("/PlayerPixelArt/player_down_2.png"));

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Draw player based on new position
    public void draw(Graphics2D g2){
        //g2.setColor(Color.white);

        //DRAWS PLAYER USING X Cord, Y Cord, width, and height (also based on pixels)
        //X and Y coordinates are crucial to placing the player on the window
        //g2.fillRect(x, y, 36, 36);

        BufferedImage image = null;

        //right now, only two images facing down are used, but if time permits, we can
        // add more images facing up, left, and right
        switch(direction){
            case "up":
                if(spriteNumber == 1) image = img1;
                if(spriteNumber == 2) image = img2;
                break;
            case "down":
                if(spriteNumber == 1) image = img1;
                if(spriteNumber == 2) image = img2;
                break;
            case "left":
                if(spriteNumber == 1) image = img1;
                if(spriteNumber == 2) image = img2;
                break;
            case "right":
                if(spriteNumber == 1) image = img1;
                if(spriteNumber == 2) image = img2;
                break;
        }

        g2.drawImage(image, MapX, MapY, entitySize, entitySize, null);
    }
}
