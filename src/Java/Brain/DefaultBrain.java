package Brain;

import GameEntity.Player;
import Map.Direction;
import Map.Position;
import Vision.Vision;

public class DefaultBrain extends Brain{
    private Player player;
    private Vision vision;

    public DefaultBrain(Player player, Vision vision){
        this.player = player;
        this.vision = vision;
    }

    @Override
    public Direction makeMove(){
        Position currentPos = new Position(player.MapX/player.gp.tileSize, player.MapY/player.gp.tileSize);
        vision.updateScope(currentPos);
        return Direction.EAST;
    }
}
