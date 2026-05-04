package Map;

import java.util.Random;

public class WildernessMap {
    public static final int MILES_PER_TILE = 5;

    private final int width;
    private final int height;
    private final Difficulty difficulty;
    private final Tile[][] tiles;
    private final Random random;

    private Position playerPosition;

    public WildernessMap(int width, int height, Difficulty difficulty) {
        this(width, height, difficulty, new Random());
    }

    public WildernessMap(int width, int height, Difficulty difficulty, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map width and height must be positive.");
        }

        this.width = width;
        this.height = height;
        this.difficulty = difficulty;
        this.random = random;
        this.tiles = new Tile[height][width];

        generateMap();
        placePlayer();
    }

    public final void generateMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position position = new Position(x, y);
                Terrain terrain = randomTerrain();
                tiles[y][x] = new Tile(position, terrain);
            }
        }
    }

    public final void placePlayer() {
        int startY = random.nextInt(height);
        playerPosition = new Position(0, startY);
    }

    public MoveResult movePlayer(Direction direction) {
        Position destination = playerPosition.translate(direction);

        if (!isValidPosition(destination)) {
            return new MoveResult(
                    false,
                    "That move would go outside the map.",
                    0,
                    playerPosition,
                    false);
        }

        Tile destinationTile = getTile(destination);
        Terrain terrain = destinationTile.getTerrain();

        if (terrain.hasObstacle(random)) {
            return new MoveResult(
                    false,
                    "An obstacle blocked the path through " + terrain.name().toLowerCase() + ".",
                    0,
                    playerPosition,
                    false);
        }

        playerPosition = destination;
        boolean reachedGoal = hasReachedEastEdge();

        return new MoveResult(
                true,
                "Moved into " + terrain.name().toLowerCase() + ".",
                destinationTile.getMovementCost(),
                playerPosition,
                reachedGoal);
    }

    public boolean isValidPosition(Position position) {
        return position.getX() >= 0
                && position.getX() < width
                && position.getY() >= 0
                && position.getY() < height;
    }

    public Tile getTile(Position position) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Position is outside the map: " + position);
        }
        return tiles[position.getY()][position.getX()];
    }

    public Tile getTileAt(Position position) {
        if (!isValidPosition(position)) {
            return null;
        }
        return tiles[position.getY()][position.getX()];
    }

    public boolean isInBounds(Position position) {
        return isValidPosition(position);
    }

    public void resetRepeatingBonuses() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x].resetRepeating();
            }
        }
    }

    public boolean hasReachedEastEdge() {
        return playerPosition.getX() == width - 1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidthInMiles() {
        return width * MILES_PER_TILE;
    }

    public int getHeightInMiles() {
        return height * MILES_PER_TILE;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    private Terrain randomTerrain() {
        double roll = random.nextDouble();

        switch (difficulty) {
            case EASY:
                return selectTerrain(roll, 0.40, 0.70, 0.85, 0.95);
            case MEDIUM:
                return selectTerrain(roll, 0.25, 0.50, 0.70, 0.85);
            case HARD:
                return selectTerrain(roll, 0.15, 0.35, 0.55, 0.75);
            default:
                throw new IllegalStateException("Unsupported difficulty: " + difficulty);
        }
    }

    private Terrain selectTerrain(
            double roll,
            double plainsThreshold,
            double forestThreshold,
            double desertThreshold,
            double riverThreshold) {
        if (roll < plainsThreshold) {
            return Terrain.PLAINS;
        }
        if (roll < forestThreshold) {
            return Terrain.FOREST;
        }
        if (roll < desertThreshold) {
            return Terrain.DESERT;
        }
        if (roll < riverThreshold) {
            return Terrain.RIVER;
        }
        return Terrain.MOUNTAIN;
    }

    private Tile generateTile(Position position, Terrain terrain) {
        double scale = difficultyScale();
 
        // Food
        boolean hasFood = random.nextDouble() < baseFoodChance(terrain) * scale;
        boolean foodRep = hasFood && terrain == Terrain.PLAINS && random.nextBoolean();
 
        // Water — rivers always give repeating water
        boolean waterRep = (terrain == Terrain.RIVER);
        boolean hasWater = waterRep || random.nextDouble() < baseWaterChance(terrain) * scale;
 
        // Gold — rare, never repeating
        boolean hasGold = random.nextDouble() < 0.04 * scale;
 
        // Trader — rare, always repeating (stays in their square)
        boolean hasTrader = random.nextDouble() < 0.03 * scale;
 
        return new Tile(position, terrain,
                        hasFood,  foodRep,
                        hasWater, waterRep,
                        hasGold,
                        hasTrader);
    }
 
    private double baseFoodChance(Terrain terrain) {
        switch (terrain) {
            case PLAINS:   return 0.20;
            case FOREST:   return 0.25;
            case DESERT:   return 0.05;
            case RIVER:    return 0.15;
            case MOUNTAIN: return 0.08;
            default:       return 0.10;
        }
    }
 
    private double baseWaterChance(Terrain terrain) {
        switch (terrain) {
            case PLAINS:   return 0.08;
            case FOREST:   return 0.12;
            case DESERT:   return 0.03;
            case MOUNTAIN: return 0.10;
            default:       return 0.05;
        }
    }
 
    private double difficultyScale() {
        switch (difficulty) {
            case EASY:   return 1.0;
            case MEDIUM: return 0.7;
            case HARD:   return 0.4;
            default:     return 1.0;
        }
    }
}
