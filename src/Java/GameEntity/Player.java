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

    GamePanel gp;
    KeyHandler keyH;
    PlayerType type;


    public Player(GamePanel panel, KeyHandler kHandle, PlayerType selectedType){
        gp = panel; //pass the game panel
        keyH = kHandle; //pass the key handler
        this.type = selectedType; //pass the selected player type
        setDefaultPositionAndSpeed(); //set Player default position on the map and their speed
        getPlayerImages(); //get player pixel art
        hitbox = new Rectangle(8, 16, 32, 32);
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

    //Accessor methods for player position
    public int getPlayerXCoord(){return MapX;}
    public int getPlayerYCoord(){return MapY;}

    //Update position
    public void update(){
        //if WASD is pressed (up, left, down, right), then manage player movement
        if(keyH.pressedUp || keyH.pressedDown
            || keyH.pressedLeft || keyH.pressedRight){
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

            }

            spriteCounter++;
            if(spriteCounter > 12){ //update sprite every 12 frames
                if(spriteNumber == 1) spriteNumber = 2;
                else if(spriteNumber == 2) spriteNumber = 1;
                spriteCounter = 0;
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
