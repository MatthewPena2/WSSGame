package GameEntity;

import GameDisplay.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class Trader extends Entity{
    GamePanel gp;

    public Trader(GamePanel panel){
        gp = panel;

        //define hitbox - when player and trader hitbox collide, an interaction will occur
        hitbox = new Rectangle(0, 0, 48, 48);

        //spawn randomly within map bounds
        spawnRandomly();
        //load trader sprite image
        getTraderImage();
    }

    //choose random map coordinates (from tiles to pixels) to spawn the Trader
    public void spawnRandomly(){
        Random random = new Random();
        int col, row, tileNum;
        boolean invalidSpawn = true;

        do{
            //get a random column and row in the map
            col = random.nextInt(gp.maxScreenCol);
            row = random.nextInt(gp.maxScreenRow);

            //Look at tileNum, ensure trader does not spawn on tile with collision (mountain)
            tileNum = gp.tileM.mapTileNum[col][row];
            if(!gp.tileM.tile[tileNum].collision) invalidSpawn = false;

        }while(invalidSpawn);

        //convert tile indices to pixel coordinates once valid tile is found
        this.MapX = col * gp.tileSize;
        this.MapY = row * gp.tileSize;
    }

    //trade negotiation rational - if the player does not offer a min price, trade is rejected
    public boolean evaluateOffer(String item, int offerAmount){
        int minPrice = 0;

        //minimum prices trader is willing to accept
        if(item.equals("Food") || item.equals("food")) minPrice = 15; //wants 15 gold for food
        if(item.equals("Water") || item.equals("water")) minPrice = 20; //wants 20 gold for water
        //"rationality": if player's offer exceeds or matches asked price, trade is accepted
        return offerAmount >= minPrice;
    }

    //get the trader sprite image
    public void getTraderImage(){
        try{
            img1 = ImageIO.read(getClass().getResourceAsStream("/TraderPixelArt/trader.png"));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    //draw trader on the game panel
    public void drawTrader(Graphics2D g2){
        g2.drawImage(img1, MapX, MapY, gp.tileSize, gp.tileSize, null);
    }

}
