package Map;

public class Tile {
    private final Position position;
    private final Terrain terrain;

    private boolean hasFood;
    private boolean hasWater;
    private boolean hasGold;
    private boolean hasTrader;

    private final boolean foodRepeating;
    private final boolean waterRepeating;
    private final boolean goldRepeating = false;

    public Tile(Position position, Terrain terrain) {
        this.position = position;
        this.terrain = terrain;
        this.foodRepeating = false;
        this.waterRepeating = false;
    }

    public Tile(Position position, Terrain terrain, boolean hasFood, boolean foodRepeating, boolean hasWater, boolean waterRepeating, boolean hasGold, boolean hasTrader) {
        this.position = position;
        this.terrain = terrain;
        this.hasFood = hasFood;
        this.foodRepeating = foodRepeating;
        this.hasWater = hasWater;
        this.waterRepeating = waterRepeating;
        this.hasGold = hasGold;
        this.hasTrader = hasTrader;
    }


    // Getters
    public Position getPosition() {
        return position;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public int getMovementCost() {
        return terrain.getMovementCost();
    }

    public boolean hasFood() {
        return hasFood;
    }

    public boolean hasWater() {
        return hasWater;
    }

    public boolean hasGold() {
        return hasGold;
    }

    public boolean hasTrader() {
        return hasTrader;
    }

    public boolean isFoodRepeating() {
        return foodRepeating;
    }

    public boolean isWaterRepeating() {
        return waterRepeating;
    }

    public boolean isGoldRepeating() {
        return goldRepeating;
    }


    public void collectFood() {
        if (!foodRepeating) {
            hasFood = false;
        }
    }
 
    public void collectWater() {
        if (!waterRepeating) {
            hasWater = false;
        }
    }
 
    public void collectGold() {
        hasGold = false;
    }
    
    public void resetRepeating() {
        if (foodRepeating)  hasFood  = true;
        if (waterRepeating) hasWater = true;
        // hasTrader is always true — traders never leave
    }


    public double getWaterCost() {
        return terrain.getWaterCost();
    }

    public double getFoodCost() {
        return terrain.getFoodCost();
    }
}
