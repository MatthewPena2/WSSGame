package TileMap;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage image; //used for image generation
    public boolean collision = false; //check if tile has collision
    public String name; // used to get the name of the terrain player is on

    //Movement costs per terrain step
    public double foodCost;
    public double waterCost;
    public double strengthCost;
}
