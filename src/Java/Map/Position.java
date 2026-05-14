package Map;

import java.util.Objects;

// Immutable x/y coordinate used for tiles, player location, and generation.
public final class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Returns the x-coordinate on the map grid.
    public int getX() {
        return x;
    }

    // Returns the y-coordinate on the map grid.
    public int getY() {
        return y;
    }

    // Creates a new position moved one step in the given direction.
    public Position translate(Direction direction) {
        return new Position(x + direction.getDeltaX(), y + direction.getDeltaY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
