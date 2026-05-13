package Vision;

import Map.Direction;
import Map.Position;
import Map.Tile;
import Map.WildernessMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Vision (Abstract Base Class)
 * 
 * The Vision system is responsible for:
 * - Scanning tiles around the player based on a defined scope</li>
 * - Filtering tiles based on target conditions (food, water, gold, etc.)</li>
 * - Determining optimal movement paths toward targets</li>
 */
public abstract class Vision {

    // Tiles currently visible based on vision strategy
    protected Tile[] scope;

    // Current player position used for calculations
    protected Position playerLocation;

    // Reference to the game map for tile queries and path building
    protected final WildernessMap map;

    /**
     * Constructs a Vision system.
     *
     * @param playerLocation    The current location of the player
     * @param map               Reference to the map
     */
    protected Vision(Position playerLocation, WildernessMap map) {
        this.playerLocation = playerLocation;
        this.map = map;
        this.scope = new Tile[0];
    }
    

    // --------------------- CLOSEST SEARCH METHODS ---------------------

    /**
     * Finds the closest food tile within vision scope.
     *
     * @return  Path to closest food, or null if none found
     */
    public Path closestFood() {
        return runProcedure(Tile::hasFood, false);
    }

    /**
     * Finds the closest water tile within vision scope.
     *
     * @return  Path to closest water, or null if none found
     */
    public Path closestWater() {
        return runProcedure(Tile::hasWater, false);
    }

    /**
     * Finds the closest gold tile within vision scope.
     *
     * @return  Path to closest gold, or null if none found
     */
    public Path closestGold() {
        return runProcedure(Tile::hasGold, false);
    }

    /**
     * Finds the closest trader tile within vision scope.
     *
     * @return  Path to closest trader, or null if none found
     */
    public Path closestTrader() {
        return runProcedure(Tile::hasGold, false);
    }


    // --------------------- SECOND CLOSEST SEARCH METHODS & EASIEST PATH --------------------- 
    
    /**
     * Computes the easiest path based on movement cost.
     *
     * @return  Path with lowest movement cost, or null if none available
     */
    public Path easiestPath() {
        return runEasiestPath();
    }

    /**
     * Finds the second closest food tile.
     *
     * @return  Path to second closest food, or null if unavailable
     */
    public Path secondClosestFood() {
        return runProcedure(Tile::hasFood, true);
    }

    /**
     * Finds the second closest water tile.
     *
     * @return  Path to second closest water, or null if unavailable
     */
    public Path secondClosestWater() {
        return runProcedure(Tile::hasWater, true);
    }

    /**
     * Finds the second closest gold tile.
     *
     * @return  Path to second closest gold, or null if unavailable
     */
    public Path secondClosestGold() {
        return runProcedure(Tile::hasGold, true);
    }

    /**
     * Finds the second closest trader tile.
     *
     * @return  Path to second closest trader, or null if unavailable
     */
    public Path secondClosestTrader() {
        return runProcedure(Tile::hasTrader, true);
    }


    // --------------------- ABSTRACT VISION BEHAVIOR --------------------- 

    /**
     * Updates the visible scope of tiles based on the vision strategy.
     *
     * @param playerLocation    The current player location
     */
    protected abstract void updateScope(Position playerLocation);


    // --------------------- PLAYER STATE ACCESS ---------------------

    /**
     * Updates the player position used for vision calculations.
     *
     * @param playerLocation    The new current player location
     */
    public void setPlayerLocation(Position playerLocation) {
        this.playerLocation = playerLocation;
    }

    /**
     * Gets the current player position.
     *
     * @return  The current player location
     */
    public Position getPlayerLocation() {
        return playerLocation;
    }


    // --------------------- CORE SEARCH LOGIC --------------------- 
    
    /**
     * Main search procedure used for finding target tiles.
     *
     * Steps:
     * - Update vision scope
     * - Filter tiles by predicate
     * - Sort by Manhattan distance
     * - Optionally select second-best candidate
     * - Resolve ties using movement cost and position priority
     *
     * @param target        Condition used to filter tiles
     * @param secondBest    Whether to select the second closest tile
     * @return Path         Path to selected tile, or null if none found
     */
    private Path runProcedure(Predicate<Tile> target, boolean secondBest) {
        updateScope(playerLocation);
        List<Tile> candidates = new ArrayList<>();

        for (Tile t : scope) {
            if (t != null && target.test(t)) {
                candidates.add(t);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        candidates.sort(Comparator.comparingInt(t -> manhattanDistance(t.getPosition(), playerLocation)));

        if (secondBest) {
            if (candidates.size() < 2) {
                return null;
            }
            candidates.remove(0);
        }

        int closestDist = manhattanDistance(candidates.get(0).getPosition(), playerLocation);
        List<Tile> tied = new ArrayList<>();
        for (Tile t : candidates) {
            if (manhattanDistance(t.getPosition(), playerLocation) == closestDist) {
                tied.add(t);
            }
        }

        Tile chosen;
        if (tied.size() > 1) {
            chosen = tieBreak(tied);
        } else {
            chosen = tied.get(0);
        }

        return buildPath(playerLocation, chosen);
    }


    /**
     * Finds the easiest (lowest movement cost) path within vision scope.
     *
     * @return  The optimal path based on movement cost
     */
    private Path runEasiestPath() {
        updateScope(playerLocation);

        List<Tile> candidates = new ArrayList<>();
        for (Tile t : scope) {
            if (t != null) {
                candidates.add(t);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        candidates.sort(Comparator.comparingInt(Tile::getMovementCost));

        int lowestCost = candidates.get(0).getMovementCost();
        List<Tile> tied = new ArrayList<>();
        for (Tile t : candidates) {
            if (t.getMovementCost() == lowestCost) {
                tied.add(t);
            }
        }

        Tile chosen;
        if(tied.size() > 1) {
            chosen = tieBreak(tied);
        } else {
            chosen = tied.get(0);
        }

        return buildPath(playerLocation, chosen);
    }


    // --------------------- TIE BREAKING LOGIC --------------------- 
    
    /**
     * Resolves ties between tiles using movement cost and X-position priority.
     *
     * @param tied  The list of tied candidate tiles
     * @return      Selected best tile
     */
    private Tile tieBreak(List<Tile> tied) {
        int minCost = tied.stream().mapToInt(Tile::getMovementCost).min().orElse(0);
        List<Tile> afterCostFilter = new ArrayList<>();
        
        for (Tile t : tied) {
            if (t.getMovementCost() == minCost) {
                afterCostFilter.add(t);
            }
        }

        Tile best = afterCostFilter.get(0);

        for (Tile t : afterCostFilter) {
            if (t.getPosition().getX() > best.getPosition().getX()) {
                best = t;
            }
        }

        return best;
    }


    // --------------------- PATH CONSTRUCTION --------------------- 
    
    /**
     * Builds a step-by-step path from a start position to a target tile.
     *
     * @param from  The starting position
     * @param to    The target tile
     * @return      The constructed Path object
     */
    private Path buildPath(Position from, Tile to) {
        List<Direction> moves = new ArrayList<>();
        int totalMoveCost = 0;
        double totalWaterCost = 0.0;
        double totalFoodCost = 0.0;
 
        int curX = from.getX();
        int curY = from.getY();
        int toX = to.getPosition().getX();
        int toY = to.getPosition().getY();
 
        while (curX != toX || curY != toY) {
            int dx = Integer.compare(toX, curX);
            int dy = Integer.compare(toY, curY);
 
            moves.add(deltaToDirection(dx, dy));
            curX += dx;
            curY += dy;
 
            Tile stepTile = map.getTileAt(new Position(curX, curY));
            if (stepTile != null) {
                totalMoveCost  += stepTile.getMovementCost();
                totalWaterCost += stepTile.getWaterCost();
                totalFoodCost  += stepTile.getFoodCost();
            }
        }
 
        return new Path(moves, totalMoveCost, totalWaterCost, totalFoodCost);
    }


    // --------------------- UTILITY METHODS --------------------- 

    /**
     * Computes Manhattan distance between two positions
     * 
     * @param a     The current position
     * @param b     The new position
     */
    private int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }


    /**
     * Converts a movement delta into a Direction enum.
     *
     * @param dx                        Change in x-direction
     * @param dy                        Change in y-direction
     * @throws IllegalArgumentException if no valid direction exists
     */
    private Direction deltaToDirection(int dx, int dy) {
        for (Direction d : Direction.values()) {
            if (d.getDeltaX() == dx && d.getDeltaY() == dy) return d;
        }
        throw new IllegalArgumentException("No Direction for delta (" + dx + ", " + dy + ")");
    }

    /**
     * Computes visible tiles based on direction set and range.
     *
     * @param playerLocation    The current location of the player
     * @param directions        The directions to scan
     * @param range             The scan distance
     * @return                  The array of visible tiles
     */
    protected Tile[] resolveScope(Position playerLocation, Direction[] directions, int range) {
        List<Tile> result = new ArrayList<>();
        for (Direction dir : directions) {
            for (int step = 1; step <= range; step++) {
                int nx = playerLocation.getX() + dir.getDeltaX() * step;
                int ny = playerLocation.getY() + dir.getDeltaY() * step;
                Position pos = new Position(nx, ny);
                if (map.isInBounds(pos)) {
                    Tile tile = map.getTileAt(pos);
                    if (tile != null) result.add(tile);
                }
            }
        }
        return result.toArray(new Tile[0]);
    }
}

