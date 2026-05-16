package TileMap;

import java.awt.image.BufferedImage;

public class TileManager {
    public BufferedImage image; //used for image generation
    public boolean collision = false; //check if tile has collision
    public String name; // used to get the name of the terrain player is on

    //Movement costs per terrain step
    public double foodCost;
    public double waterCost;
    public double strengthCost;

    //sets the costs for each tile (base food cost defined in WildernessMapManger + Difficulty Modifier)
    public void setCosts(double baseFood, double baseWater, double baseStr, double mod){
        this.foodCost = baseFood + mod;
        this.waterCost = baseWater + mod;
        this.strengthCost = baseStr + mod;
    }
}
