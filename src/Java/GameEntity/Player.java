package GameEntity;

import GameDisplay.GamePanel;
import GameDisplay.KeyHandler;


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
    GamePanel gp;
    KeyHandler keyH;
    PlayerType type;
    public double foodAmount, waterAmount, strength;
    public int goldAmount = 30;

    public Player(GamePanel panel, KeyHandler kHandle, PlayerType selectedType){
        gp = panel; //pass the game panel
        keyH = kHandle; //pass the key handler
        this.type = selectedType; //pass the selected player type
        setStartingSuppliesAndStrength(); //initiate the player supplies
        setDefaultPositionAndSpeed(); //set Player default position on the map and their speed
        getPlayerImages(); //get player pixel art
        hitbox = new Rectangle(8, 16, 32, 32);
    }

    //Mutator method for initializing the player supplies and strength
    public void setStartingSuppliesAndStrength(){
        foodAmount = type.getStartFood();
        waterAmount = type.getStartWater();
        strength = type.getStartStrength();
    }

    //Mutator method for position x, y and speed
    public void setDefaultPositionAndSpeed(){
        MapX = 0; //x coordinate in the window
        MapY = 336; //y coordinate in the window
        speed = 0; //default speed
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
        //if WASD is pressed (up, left, down, right), then manage player movement
        if(keyH.pressedUp || keyH.pressedDown
            || keyH.pressedLeft || keyH.pressedRight){

            //strength check - player should not be able to move if strength is 0
            //Player is forced to stop moving and rest
            if(strength <= 0){
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
                foodAmount -= gp.tileM.tile[tileIndex].foodCost;
                waterAmount -= gp.tileM.tile[tileIndex].waterCost;
                strength -= gp.tileM.tile[tileIndex].strengthCost;

                //prevent stats from going below zero
                if(foodAmount < 0) foodAmount = 0;
                if(waterAmount < 0) waterAmount = 0;
                if(strength < 0) strength = 0;

            }

            spriteCounter++;
            if(spriteCounter > 12){ //update sprite every 12 frames
                if(spriteNumber == 1) spriteNumber = 2;
                else if(spriteNumber == 2) spriteNumber = 1;
                spriteCounter = 0;
            }

        } else{ //the player will regain strength when they are not moving
            if(strength < type.getStartStrength()){ //less than max
                strength += 0.07; //slowly regain strength

                //keep strength at starting cap
                if(strength > type.getStartStrength()) strength = type.getStartStrength();
            }
        }

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
