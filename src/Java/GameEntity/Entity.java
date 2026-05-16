package GameEntity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    public int MapX, MapY; //position of entity on the map
    protected int entitySize; //used to draw the entity on screen (in terms of pixels)

    //variables used to draw the entity using png pixel art
    //Direction, spriteCounter, and spriteNumber are more geared towards animating the entity movement
    //Animations will mainly apply to the player (e.g., trader won't move);
    public BufferedImage img1, img2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNumber = 1;

    //Collision Detection: Hitbox - will be applied to the player and trader
    //If the player collides with a mountain terrain, they should not be able to pass
    //If the player collides with a trader, trade is initiated
    public Rectangle hitbox;
    public boolean collisionOn = false;
}
