package GameDisplay;

import GameEntity.Entity;

//TRACKS PLAYER MOVEMENT - ENSURES PLAYER COLLIDES WITH MOUNTAIN OBJECT AND STAYS IN BOUNDS

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel panel){
        gp = panel;
    }

    public void checkTileCollision(Entity entity){
        int entityLeftMapX = entity.MapX + entity.hitbox.x;
        int entityRightMapX = entity.MapX + entity.hitbox.x + entity.hitbox.width;
        int entityTopMapY = entity.MapY + entity.hitbox.y;
        int entityBottomMapY = entity.MapY + entity.hitbox.y + entity.hitbox.height;

        //get the position of the player (hitbox) in terms of map row and col
        int entityLeftCol = entityLeftMapX/gp.tileSize;
        int entityRightCol = entityRightMapX/gp.tileSize;
        int entityTopRow = entityTopMapY/gp.tileSize;
        int entityBottomRow = entityBottomMapY/gp.tileSize;

        //only need to check for two tiles in front, behind, or next to the player for collision
        int tileNum1, tileNum2;

        try{
            switch (entity.direction){ //based on the players direction (W = "up", S = "down", etc.)
                case "up": //if player is pressing W
                    entityTopRow = (entityTopMapY - entity.speed)/gp.tileSize;
                    if(entityTopRow < 0){ //check for out of bounds
                        entity.collisionOn = true;
                        System.out.println("Cannot go out of bounds!");
                    }else{
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                        if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){ //check for tile collision
                            entity.collisionOn = true;
                        }
                    }
                    break;
                case "down": //if player is pressing S
                    entityBottomRow = (entityBottomMapY + entity.speed)/gp.tileSize;
                    if(entityBottomRow < 0){ //check for out of bounds
                        entity.collisionOn = true;
                        System.out.println("Cannot go out of bounds!");
                    }else{
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                        if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                            entity.collisionOn = true;
                        }
                    }
                    break;
                case "left": //if player is pressing A
                    entityLeftCol = (entityLeftMapX - entity.speed)/gp.tileSize;
                    if(entityLeftCol < 0){ //check for out of bounds
                        entity.collisionOn = true;
                        System.out.println("Cannot go out of bounds!");
                    }else{
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                        if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                            entity.collisionOn = true;
                        }
                    }
                    break;
                case "right": //if player is pressing D
                    entityRightCol = (entityRightMapX + entity.speed)/gp.tileSize;
                    if(entityRightCol < 0){ //check for out of bounds
                        entity.collisionOn = true;
                        System.out.println("Cannot go out of bounds!");
                    }else{
                        tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                        if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                            entity.collisionOn = true;
                        }
                    }
                    break;
            }
        }catch(ArrayIndexOutOfBoundsException e){
            entity.collisionOn = true;
        }

    }
}
