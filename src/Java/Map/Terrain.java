package Map;

import java.util.Random;

// Defines terrain types along with movement, resource, and obstacle values.
public enum Terrain {
    PLAINS(1, 0.5, 0.5, 0.05),
    FOREST(2, 0.5, 1.0, 0.15),
    DESERT(3, 2.0, 0.5, 0.10),
    RIVER(4, 0.0, 1.0, 0.35),
    MOUNTAIN(5, 1.0, 2.0, 0.45);

    private final int movementCost;
    private final double obstacleChance;
    private final double waterCost;
    private final double foodCost;

    Terrain(int movementCost, double waterCost, double foodCost, double obstacleChance) {
        this.movementCost = movementCost;
        this.obstacleChance = obstacleChance;
        this.foodCost = foodCost;
        this.waterCost = waterCost;
    }

    public int getMovementCost() {
        return movementCost;
    }

    // Returns the obstacle chance associated with this terrain.
    public double getObstacleChance() {
        return obstacleChance;
    }
    
    public double getWaterCost() {
        return waterCost;
    }

    // Returns how much food is consumed when traveling through this terrain.
    public double getFoodCost() {
        return foodCost;
    }

    // Rolls whether movement is blocked by an obstacle on this terrain.
    public boolean hasObstacle(Random random) {
        return random.nextDouble() < obstacleChance;
    }
}
