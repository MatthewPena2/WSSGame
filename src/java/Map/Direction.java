package Map;

// Represents the 8 directions the player or generator can move on the grid.
public enum Direction {
    NORTH(0, -1),
    NORTHEAST(1, -1),
    EAST(1, 0),
    SOUTHEAST(1, 1),
    SOUTH(0, 1),
    SOUTHWEST(-1, 1),
    WEST(-1, 0),
    NORTHWEST(-1, -1);

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    // Returns the horizontal change for this direction.
    public int getDeltaX() {
        return deltaX;
    }

    // Returns the vertical change for this direction.
    public int getDeltaY() {
        return deltaY;
    }
}
