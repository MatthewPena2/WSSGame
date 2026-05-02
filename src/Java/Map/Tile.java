package wss.map;

public class Tile {
    private final Position position;
    private final Terrain terrain;

    public Tile(Position position, Terrain terrain) {
        this.position = position;
        this.terrain = terrain;
    }

    public Position getPosition() {
        return position;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public int getMovementCost() {
        return terrain.getMovementCost();
    }
}
