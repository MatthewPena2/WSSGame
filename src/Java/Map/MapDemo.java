package Map;

public class MapDemo {
    public static void main(String[] args) {
        WildernessMap map = new WildernessMap(20, 10, Difficulty.MEDIUM);

        System.out.println("Map size: " + map.getWidth() + " x " + map.getHeight() + " tiles");
        System.out.println("Map size: " + map.getWidthInMiles() + " x " + map.getHeightInMiles() + " miles");
        System.out.println("Player starts at: " + map.getPlayerPosition());

        MoveResult move = map.movePlayer(Direction.EAST);
        System.out.println(move.getMessage());
        System.out.println("Move success: " + move.isSuccess());
        System.out.println("New position: " + move.getNewPosition());
        System.out.println("Movement cost: " + move.getMovementCost());
        System.out.println("Reached east edge: " + move.hasReachedGoal());
    }
}
