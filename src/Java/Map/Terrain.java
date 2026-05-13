package Map;

import java.util.Random;

// Defines the terrain types and their gameplay costs.
public enum Terrain {
    PLAINS(1, 0.05),
    FOREST(2, 0.15),
    DESERT(3, 0.10),
    RIVER(4, 0.35),
    MOUNTAIN(5, 0.45);

    private final int movementCost;
    private final double obstacleChance;

    Terrain(int movementCost, double obstacleChance) {
        this.movementCost = movementCost;
        this.obstacleChance = obstacleChance;
    }

    public int getMovementCost() {
        return movementCost;
    }

    // Returns this terrain's chance to block movement with an obstacle.
    public double getObstacleChance() {
        return obstacleChance;
    }

    // Rolls whether the tile blocks movement for this step.
    public boolean hasObstacle(Random random) {
        return random.nextDouble() < obstacleChance;
    }
}
