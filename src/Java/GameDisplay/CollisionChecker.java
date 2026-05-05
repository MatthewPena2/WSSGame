package GameDisplay;

import GameEntity.Entity;

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
        switch (entity.direction){
            case "up":
                entityTopRow = (entityTopMapY - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "down":
                entityBottomRow = (entityBottomMapY + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "left":
                entityLeftCol = (entityLeftMapX - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;
            case "right":
                entityRightCol = (entityRightMapX + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;
        }

    }
}
