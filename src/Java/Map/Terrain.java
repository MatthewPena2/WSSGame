package wss.map;

import java.util.Random;

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

    public double getObstacleChance() {
        return obstacleChance;
    }

    public boolean hasObstacle(Random random) {
        return random.nextDouble() < obstacleChance;
    }
}
