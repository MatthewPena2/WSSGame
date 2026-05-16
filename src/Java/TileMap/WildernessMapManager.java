package TileMap;

import GameDisplay.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//THIS WILDERNESS MAP MANAGER SIMPLY LOADS THE MAP FROM THE tileMap.txt FILE AND DRAWS IT IN THE GAME PANEL
//IT DOES NOT KEEP TRACK OF PLAYER MOVEMENT/MAP CONTENTS - THAT IS FOUND IN COLLISION CHECKER AND GAME PANEL

public class WildernessMapManager {
    GamePanel gp;
    public TileManager[] tile;
    public int mapTileNum[][];
    private double mod;

    public WildernessMapManager(GamePanel panel){
        gp = panel;
        tile = new TileManager[5]; //array of different types of tiles (as it pertains to terrain)
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
        loadMap("/MapLayout/tileMap.txt");
        mod = gp.selectedDifficulty.getModifier();
    }

    public void getTileImage(){
        try{
            //create new tile for each specific terrain type
            tile[0] = new TileManager(); //plains
            tile[0].name = "Plains";

            tile[1] = new TileManager(); //desert
            tile[1].name = "Desert";

            tile[2] = new TileManager(); //forest
            tile[2].name = "Forest";

            tile[3] = new TileManager(); //mountain
            tile[3].name = "Mountain";
            tile[3].collision = true; //adds collision with mountain terrain

            tile[4] = new TileManager(); //river water
            tile[4].name = "Water";

            //user tile image for plains as a test
            InputStream inputStream = getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_plains.png");
            if(inputStream == null){ //if not successful, print error message
                System.out.println("Error: could not find tile art path");
            }else{ //else, load the tile image -- if this path works for this tile, it will work for the others
                tile[0].image = ImageIO.read(inputStream);
            }
            //tile image for desert
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_desert.png"));
            //tile image for forest
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_forest.png"));
            //tile image for mountain
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_mountain.png"));
            //tile image for water
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("/TileTerrainPixelArt/terrain_river_water.png"));

            // tile[0] = plains - default penalties (baseFood, baseWater, baseStr, difficulty modifier)
            tile[0].setCosts(0.02, 0.01, 0.01, mod);

            // tile[1] = desert - more aggressive penalties
            tile[1].foodCost = 0.04; //x2 base
            tile[1].waterCost = 0.05; //x5 base
            tile[1].strengthCost = 0.03; //x3 base

            // tile[2] = forest - more food and strength intensive
            tile[2].foodCost = 0.06; //x3 base
            tile[2].waterCost = 0.03; //x3 base
            tile[2].strengthCost = 0.04; //x4 base
            // tile[3] = mountain - impassable terrain with collision

            //tile[4] = water - more food and strength intensive
            tile[4].foodCost = 0.05; //x5
            tile[4].waterCost = 0.01; //x1
            tile[4].strengthCost = 0.05; //x5

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void loadMap(String filePath){
        try{
            InputStream IS = getClass().getResourceAsStream(filePath);
            if (IS == null) {
                throw new IOException("Could not find map resource: " + filePath);
            }
            BufferedReader BR = new BufferedReader(new InputStreamReader(IS));

            int col = 0;
            int row = 0;
            while (col < gp.maxScreenCol && row < gp.maxScreenRow){
                String line = BR.readLine();
                if (line == null) {
                    while (col < gp.maxScreenCol) {
                        mapTileNum[col][row] = 0;
                        col++;
                    }
                } else {
                    String[] numbers = line.trim().split("\\s+");
                    while(col < gp.maxScreenCol){
                        int num = 0;
                        if (col < numbers.length) {
                            num = Integer.parseInt(numbers[col]);
                        }
                        mapTileNum[col][row] = num;
                        col++;
                    }
                }
                if(col == gp.maxScreenCol){
                    col = 0;
                    row++;
                }
            }
            BR.close();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load map layout from " + filePath, e);
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
