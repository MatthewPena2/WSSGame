package GameEntity;

import GameDisplay.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Random;

public class Trader extends Entity{
    GamePanel gp;
    public TraderType type; //used to determine type of trader

    public Trader(GamePanel panel){
        gp = panel;
        //define hitbox - when player and trader hitbox collide, an interaction will occur
        hitbox = new Rectangle(0, 0, 48, 48);
        //randomly assign a trader type
        this.type = TraderType.values()[new Random().nextInt(TraderType.values().length)];
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
        if(item.equals("Food")) minPrice = type.getMinFoodPrice(); //wants 15 gold for food
        if(item.equals("Water")) minPrice = type.getMinWaterPrice(); //wants 20 gold for water
        //"rationality": if player's offer exceeds or matches asked price, trade is accepted
        return offerAmount >= minPrice;
    }

    //trade negotiation rational - counteroffer based on minimum currency requirements in evaluateOffer()
    public int getCounterOffer(String item){
        int baseMin = 0; //base min price
        int premium = 2; //additional price

        if(item.equals("Food")) baseMin = type.getMinFoodPrice(); //wants 15 gold for food
        if(item.equals("Water")) baseMin = type.getMinWaterPrice(); //wants 20 gold for water

        if(type == TraderType.GREEDY) premium = 10; //increase premium on greedy traders

        return baseMin + premium; //returns highest asking price
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
