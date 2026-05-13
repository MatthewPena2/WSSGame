package Map;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Small console demo for testing map generation and export without the GUI.
public class MapDemo {
    // Creates a sample map, exports it, and prints a basic movement test.
    public static void main(String[] args) throws IOException {
        WildernessMap map = new WildernessMap(20, 10, Difficulty.MEDIUM);
        Path exportPath = Paths.get("..", "..", "WSSGame-GameWindow", "WSSGame-GameWindow", "res", "MapLayout", "tileMap.txt");
        map.exportToTileMapFile(exportPath);

        System.out.println("Map size: " + map.getWidth() + " x " + map.getHeight() + " tiles");
        System.out.println("Map size: " + map.getWidthInMiles() + " x " + map.getHeightInMiles() + " miles");
        System.out.println("Player starts at: " + map.getPlayerPosition());
        System.out.println("Exported tile map to: " + exportPath.normalize());

        MoveResult move = map.movePlayer(Direction.EAST);
        System.out.println(move.getMessage());
        System.out.println("Move success: " + move.isSuccess());
        System.out.println("New position: " + move.getNewPosition());
        System.out.println("Movement cost: " + move.getMovementCost());
        System.out.println("Reached east edge: " + move.hasReachedGoal());
    }
}
