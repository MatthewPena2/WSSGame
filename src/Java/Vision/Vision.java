package Vision;

import Map.Direction;
import Map.Position;
import Map.Tile;
import Map.WildernessMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public abstract class Vision {
    protected Tile[] scope;
    protected Position playerLocation;
    protected final WildernessMap map;

    protected Vision(Position playerLocation, WildernessMap map) {
        this.playerLocation = playerLocation;
        this.map = map;
        this.scope = new Tile[0];
    }
    
    // Closest Items
    public Path closestFood() {
        return runProcedure(Tile::hasFood, false);
    }

    public Path closestWater() {
        return runProcedure(Tile::hasWater, false);
    }

    public Path closestGold() {
        return runProcedure(Tile::hasGold, false);
    }

    public Path closestTrader() {
        return runProcedure(Tile::hasGold, false);
    }


    // Second Closest Items & Easiest Path
    public Path easiestPath() {
        return runEasiestPath();
    }

    public Path secondClosestFood() {
        return runProcedure(Tile::hasFood, true);
    }

    public Path secondClosestWater() {
        return runProcedure(Tile::hasWater, true);
    }

    public Path secondClosestGold() {
        return runProcedure(Tile::hasGold, true);
    }

    public Path secondClosestTrader() {
        return runProcedure(Tile::hasTrader, true);
    }


    // updateScope
    protected abstract void updateScope(Position location);


    // Set/Get Player Location
    public void setPlayerLocation(Position location) {
        this.playerLocation = location;
    }

    public Position getPlayerLocation() {
        return playerLocation;
    }


    // Main runProcedure method
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


    // runEasiestPath Method
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


    // Tie Breaker Method
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


    // Build Path Method
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


    private int manhattanDistance(Position a, Position b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }


    private Direction deltaToDirection(int dx, int dy) {
        for (Direction d : Direction.values()) {
            if (d.getDeltaX() == dx && d.getDeltaY() == dy) return d;
        }
        throw new IllegalArgumentException("No Direction for delta (" + dx + ", " + dy + ")");
    }

    
    protected Tile[] resolveScope(Position location, Direction[] directions, int range) {
        List<Tile> result = new ArrayList<>();
        for (Direction dir : directions) {
            for (int step = 1; step <= range; step++) {
                int nx = location.getX() + dir.getDeltaX() * step;
                int ny = location.getY() + dir.getDeltaY() * step;
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

