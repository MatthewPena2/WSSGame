package TileMap;

import GameDisplay.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WildernessMapManager {
    GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public WildernessMapManager(GamePanel panel){
        gp = panel;
        tile = new Tile[5]; //array of different types of tiles (as it pertains to terrain)
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap("/MapLayout/tileMap.txt");
    }

    public void getTileImage(){
        try{
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_plains.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_desert.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_forest.png"));

            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_mountain.png"));
            tile[3].collision = true;

            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_river_water.png"));


        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){
        try{
            InputStream IS = getClass().getResourceAsStream(filePath);
            BufferedReader BR = new BufferedReader(new InputStreamReader(IS));

            int col = 0;
            int row = 0;
            while (col < gp.maxScreenCol && row < gp.maxScreenRow){
                String line = BR.readLine();
                while(col < gp.maxScreenCol){
                    String[] numbers = line.split(" ");
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxScreenCol){
                    col = 0;
                    row++;
                }
            }
            BR.close();
        } catch (Exception e) {

        }
    }

    public void draw(Graphics2D g2){
        /*g2.drawImage(tile[0].image, 0,  0, gp.tileSize, gp.tileSize, null);
        g2.drawImage(tile[1].image, 48,  0, gp.tileSize, gp.tileSize, null);
        g2.drawImage(tile[2].image, 96,  0, gp.tileSize, gp.tileSize, null);
        g2.drawImage(tile[3].image, 144,  0, gp.tileSize, gp.tileSize, null);
        g2.drawImage(tile[4].image, 192,  0, gp.tileSize, gp.tileSize, null);
         */

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow){
            int tileNum = mapTileNum[col][row];

            g2.drawImage(tile[tileNum].image, x,  y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;

            if(col == gp.maxScreenCol){
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }


    }
}
