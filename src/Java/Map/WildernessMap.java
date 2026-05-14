package Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Owns the generated map grid, player position, tile resources, and biome generation logic.
public class WildernessMap {
    public static final int MILES_PER_TILE = 5;

    private final int width;
    private final int height;
    private final Difficulty difficulty;
    private final Tile[][] tiles;
    private final Random random;

    private Position playerPosition;

    // Builds a new map using a default random number generator.
    public WildernessMap(int width, int height, Difficulty difficulty) {
        this(width, height, difficulty, new Random());
    }

    // Builds a new map and immediately generates its terrain and start point.
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

    // Fills the tile array using the biome layout produced by the generator.
    public final void generateMap() {
        Terrain[][] biomeLayout = generateBiomeLayout();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position position = new Position(x, y);
                tiles[y][x] = generateTile(position, biomeLayout[y][x]);
            }
        }
    }

    // Places the player on a random tile along the west edge.
    public final void placePlayer() {
        int startY = random.nextInt(height);
        playerPosition = new Position(0, startY);
    }

    // Attempts to move the player and returns the outcome of that move.
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

    // Checks whether a coordinate falls inside the map bounds.
    public boolean isValidPosition(Position position) {
        return position.getX() >= 0
                && position.getX() < width
                && position.getY() >= 0
                && position.getY() < height;
    }

    // Returns the tile at a position, or throws if the position is invalid.
    public Tile getTile(Position position) {
        if (!isValidPosition(position)) {
            throw new IllegalArgumentException("Position is outside the map: " + position);
        }
        return tiles[position.getY()][position.getX()];
    }

    // Safe tile lookup used by helper systems that may query outside the map.
    public Tile getTileAt(Position position) {
        if (!isValidPosition(position)) {
            return null;
        }
        return tiles[position.getY()][position.getX()];
    }

    // Alias used by other systems that need a simple boundary check.
    public boolean isInBounds(Position position) {
        return isValidPosition(position);
    }

    // Restores repeating food/water sources on tiles that regenerate.
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

    // Exports the generated terrain layout to the text format used by GameWindow.
    public void exportToTileMapFile(Path filePath) throws IOException {
        List<String> lines = new ArrayList<String>(height);

        for (int y = 0; y < height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < width; x++) {
                if (x > 0) {
                    row.append(' ');
                }
                row.append(toGameWindowTileNumber(tiles[y][x].getTerrain()));
            }
            lines.add(row.toString());
        }

        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    // Translates a terrain enum into the tile number expected by the GUI loader.
    private int toGameWindowTileNumber(Terrain terrain) {
        switch (terrain) {
            case PLAINS:
                return 0;
            case DESERT:
                return 1;
            case FOREST:
                return 2;
            case MOUNTAIN:
                return 3;
            case RIVER:
                return 4;
            default:
                throw new IllegalStateException("Unsupported terrain for export: " + terrain);
        }
    }

    // Builds the high-level biome layout before concrete tiles and resources are created.
    private Terrain[][] generateBiomeLayout() {
        Terrain[][] layout = new Terrain[height][width];
        EnumMap<Terrain, Integer> targetCounts = buildTargetCounts();
        int riverTiles = targetCounts.get(Terrain.RIVER);
        targetCounts.put(Terrain.RIVER, 0);
        int mountainTiles = targetCounts.get(Terrain.MOUNTAIN);
        targetCounts.put(Terrain.MOUNTAIN, 0);
        EnumMap<Terrain, Integer> remainingCounts = new EnumMap<Terrain, Integer>(targetCounts);
        EnumMap<Terrain, Set<Position>> frontiers = createFrontiers();

        placeInitialSeeds(layout, remainingCounts, frontiers);

        while (hasUnassignedTiles(layout)) {
            Terrain terrainToGrow = chooseTerrainToGrow(remainingCounts, frontiers);

            if (terrainToGrow == null) {
                break;
            }

            Position next = pollBestFrontierPosition(terrainToGrow, layout, frontiers.get(terrainToGrow));
            if (next == null) {
                next = placeFallbackSeed(layout, terrainToGrow, frontiers.get(terrainToGrow));
            }

            if (next == null) {
                remainingCounts.put(terrainToGrow, 0);
                continue;
            }

            assignTerrain(layout, next, terrainToGrow, remainingCounts, frontiers);
        }

        fillUnassignedTiles(layout);
        smoothBiomeEdges(layout, 1);
        carveRiverPaths(layout, riverTiles);
        scatterMountains(layout, mountainTiles);
        return layout;
    }

    // Calculates how many tiles each terrain should receive for this map size.
    private EnumMap<Terrain, Integer> buildTargetCounts() {
        int totalTiles = width * height;
        double[] weights = biomeWeightsForDifficulty();
        Terrain[] terrains = Terrain.values();
        EnumMap<Terrain, Integer> counts = new EnumMap<Terrain, Integer>(Terrain.class);
        int assigned = 0;

        for (int i = 0; i < terrains.length; i++) {
            int count = (int) Math.floor(totalTiles * weights[i]);
            counts.put(terrains[i], count);
            assigned += count;
        }

        while (assigned < totalTiles) {
            Terrain next = terrainWithLargestFractionalRemainder(weights, counts, totalTiles);
            counts.put(next, counts.get(next) + 1);
            assigned++;
        }

        return counts;
    }

    private double[] biomeWeightsForDifficulty() {
        switch (difficulty) {
            case EASY:
                return new double[] {0.35, 0.28, 0.18, 0.10, 0.09};
            case MEDIUM:
                return new double[] {0.28, 0.24, 0.22, 0.10, 0.16};
            case HARD:
                return new double[] {0.20, 0.20, 0.26, 0.10, 0.24};
            default:
                throw new IllegalStateException("Unsupported difficulty: " + difficulty);
        }
    }

    private Terrain terrainWithLargestFractionalRemainder(
            double[] weights,
            EnumMap<Terrain, Integer> counts,
            int totalTiles) {
        Terrain[] terrains = Terrain.values();
        Terrain bestTerrain = terrains[0];
        double bestRemainder = -1.0;

        for (int i = 0; i < terrains.length; i++) {
            double exactCount = weights[i] * totalTiles;
            double remainder = exactCount - counts.get(terrains[i]);
            if (remainder > bestRemainder) {
                bestRemainder = remainder;
                bestTerrain = terrains[i];
            }
        }

        return bestTerrain;
    }

    private EnumMap<Terrain, Set<Position>> createFrontiers() {
        EnumMap<Terrain, Set<Position>> frontiers = new EnumMap<Terrain, Set<Position>>(Terrain.class);
        for (Terrain terrain : Terrain.values()) {
            frontiers.put(terrain, new HashSet<Position>());
        }
        return frontiers;
    }

    private void placeInitialSeeds(
            Terrain[][] layout,
            EnumMap<Terrain, Integer> remainingCounts,
            EnumMap<Terrain, Set<Position>> frontiers) {
        for (Terrain terrain : Terrain.values()) {
            int seedCount = Math.min(seedCountForTerrain(terrain, remainingCounts.get(terrain)), remainingCounts.get(terrain));
            for (int i = 0; i < seedCount; i++) {
                Position seed = selectSeedPosition(layout);
                if (seed == null) {
                    return;
                }
                assignTerrain(layout, seed, terrain, remainingCounts, frontiers);
            }
        }
    }

    private int seedCountForTerrain(Terrain terrain, int targetCount) {
        if (targetCount <= 0) {
            return 0;
        }

        int base = Math.max(1, targetCount / 25);
        return Math.min(base, 8);
    }

    private Position selectSeedPosition(Terrain[][] layout) {
        List<Position> candidates = new ArrayList<Position>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] == null) {
                    candidates.add(new Position(x, y));
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(candidates, random);
        Collections.sort(candidates, new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                return scoreSeedPosition(layout, b) - scoreSeedPosition(layout, a);
            }
        });

        return candidates.get(0);
    }

    private int scoreSeedPosition(Terrain[][] layout, Position position) {
        int minDistance = Integer.MAX_VALUE;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] != null) {
                    int distance = Math.abs(position.getX() - x) + Math.abs(position.getY() - y);
                    if (distance < minDistance) {
                        minDistance = distance;
                    }
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? width + height : minDistance;
    }

    private void assignTerrain(
            Terrain[][] layout,
            Position position,
            Terrain terrain,
            EnumMap<Terrain, Integer> remainingCounts,
            EnumMap<Terrain, Set<Position>> frontiers) {
        if (!isValidPosition(position) || layout[position.getY()][position.getX()] != null) {
            return;
        }
        if (remainingCounts.get(terrain) <= 0) {
            return;
        }
        if (wouldExceedBiomeFootprint(layout, position, terrain)) {
            return;
        }

        layout[position.getY()][position.getX()] = terrain;
        remainingCounts.put(terrain, remainingCounts.get(terrain) - 1);

        for (Set<Position> frontier : frontiers.values()) {
            frontier.remove(position);
        }

        for (Position neighbor : getNeighbors(position)) {
            if (layout[neighbor.getY()][neighbor.getX()] == null) {
                frontiers.get(terrain).add(neighbor);
            }
        }
    }

    private Terrain chooseTerrainToGrow(
            EnumMap<Terrain, Integer> remainingCounts,
            EnumMap<Terrain, Set<Position>> frontiers) {
        Terrain bestTerrain = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Terrain terrain : Terrain.values()) {
            int remaining = remainingCounts.get(terrain);
            if (remaining <= 0) {
                continue;
            }

            double frontierBoost = frontiers.get(terrain).isEmpty() ? 0.0 : 2.5;
            double score = remaining + frontierBoost + random.nextDouble();
            if (score > bestScore) {
                bestScore = score;
                bestTerrain = terrain;
            }
        }

        return bestTerrain;
    }

    private Position pollBestFrontierPosition(
            Terrain terrain,
            Terrain[][] layout,
            Set<Position> frontier) {
        if (frontier.isEmpty()) {
            return null;
        }

        Position bestPosition = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        List<Position> stalePositions = new ArrayList<Position>();

        for (Position position : frontier) {
            if (layout[position.getY()][position.getX()] != null) {
                stalePositions.add(position);
                continue;
            }

            double score = frontierScore(layout, position, terrain);
            if (score > bestScore) {
                bestScore = score;
                bestPosition = position;
            }
        }

        frontier.removeAll(stalePositions);
        if (bestPosition != null) {
            frontier.remove(bestPosition);
        }
        return bestPosition;
    }

    private double frontierScore(Terrain[][] layout, Position position, Terrain terrain) {
        if (wouldExceedBiomeFootprint(layout, position, terrain)) {
            return Double.NEGATIVE_INFINITY;
        }

        int sameNeighbors = 0;
        int unassignedNeighbors = 0;
        int otherNeighbors = 0;

        for (Position neighbor : getNeighbors(position)) {
            Terrain neighborTerrain = layout[neighbor.getY()][neighbor.getX()];
            if (neighborTerrain == null) {
                unassignedNeighbors++;
            } else if (neighborTerrain == terrain) {
                sameNeighbors++;
            } else {
                otherNeighbors++;
            }
        }

        double score = sameNeighbors * 5.0 + unassignedNeighbors * 1.5 - otherNeighbors * 2.0;
        score += centerBias(position) * 0.15;
        score += random.nextDouble();
        return score;
    }

    private double centerBias(Position position) {
        double centerX = (width - 1) / 2.0;
        double centerY = (height - 1) / 2.0;
        double distance = Math.abs(position.getX() - centerX) + Math.abs(position.getY() - centerY);
        return (width + height) - distance;
    }

    private Position placeFallbackSeed(Terrain[][] layout, Terrain terrain, Set<Position> frontier) {
        Position best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] != null) {
                    continue;
                }
                Position position = new Position(x, y);
                double score = frontierScore(layout, position, terrain);
                if (score > bestScore) {
                    bestScore = score;
                    best = position;
                }
            }
        }

        if (best != null) {
            frontier.remove(best);
        }
        return best;
    }

    private void fillUnassignedTiles(Terrain[][] layout) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] == null) {
                    layout[y][x] = majorityNeighborTerrain(layout, new Position(x, y), Terrain.PLAINS);
                }
            }
        }
    }

    private void smoothBiomeEdges(Terrain[][] layout, int passes) {
        for (int pass = 0; pass < passes; pass++) {
            Terrain[][] snapshot = copyLayout(layout);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Position position = new Position(x, y);
                    Terrain current = snapshot[y][x];
                    Terrain majority = majorityNeighborTerrain(snapshot, position, current);
                    int sameCount = countNeighborTerrain(snapshot, position, current);
                    int majorityCount = countNeighborTerrain(snapshot, position, majority);

                    if (majority != current && majorityCount >= 5 && sameCount <= 2) {
                        layout[y][x] = majority;
                    }
                }
            }
        }
    }

    private void carveRiverPaths(Terrain[][] layout, int riverTiles) {
        if (riverTiles <= 0) {
            return;
        }

        Position start = selectRiverStart(layout);
        if (start == null) {
            return;
        }

        carveSingleRiver(layout, start, riverTiles);
    }

    private void scatterMountains(Terrain[][] layout, int mountainTiles) {
        if (mountainTiles <= 0) {
            return;
        }

        int remaining = mountainTiles;
        while (remaining > 0) {
            Position seed = selectMountainSeed(layout);
            if (seed == null) {
                break;
            }

            int bunchSize = Math.min(remaining, randomRange(1, 2));
            remaining -= placeMountainBunch(layout, seed, bunchSize);
        }
    }

    private Position selectMountainSeed(Terrain[][] layout) {
        List<Position> candidates = new ArrayList<Position>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] == Terrain.RIVER || layout[y][x] == Terrain.MOUNTAIN) {
                    continue;
                }
                candidates.add(new Position(x, y));
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(candidates, random);
        Collections.sort(candidates, new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                return mountainSeedScore(layout, b) - mountainSeedScore(layout, a);
            }
        });

        return candidates.get(0);
    }

    private int mountainSeedScore(Terrain[][] layout, Position position) {
        int score = 0;
        Terrain terrain = layout[position.getY()][position.getX()];

        if (terrain == Terrain.FOREST) {
            score += 4;
        } else if (terrain == Terrain.PLAINS) {
            score += 3;
        } else if (terrain == Terrain.DESERT) {
            score -= 2;
        }

        int nearbyMountains = countTerrainWithinRadius(layout, position, Terrain.MOUNTAIN, 2);
        int nearbyRivers = 0;
        for (Position neighbor : getNeighbors(position)) {
            Terrain neighborTerrain = layout[neighbor.getY()][neighbor.getX()];
            if (neighborTerrain == Terrain.RIVER) {
                nearbyRivers++;
            }
        }

        score -= nearbyMountains * 6;
        score -= nearbyRivers * 3;
        score += (int) Math.round(centerBias(position) * 0.03);
        return score;
    }

    private int placeMountainBunch(Terrain[][] layout, Position seed, int bunchSize) {
        int placed = 0;
        if (canPlaceMountain(layout, seed)) {
            layout[seed.getY()][seed.getX()] = Terrain.MOUNTAIN;
            placed++;
        }

        if (placed < bunchSize) {
            Position neighbor = selectMountainNeighbor(layout, seed);
            if (neighbor != null && canPlaceMountain(layout, neighbor)) {
                layout[neighbor.getY()][neighbor.getX()] = Terrain.MOUNTAIN;
                placed++;
            }
        }

        return placed;
    }

    private boolean canPlaceMountain(Terrain[][] layout, Position position) {
        if (position.getX() == 0) {
            return false;
        }
        Terrain terrain = layout[position.getY()][position.getX()];
        if (terrain == Terrain.RIVER || terrain == Terrain.MOUNTAIN) {
            return false;
        }
        return countTerrainWithinRadius(layout, position, Terrain.MOUNTAIN, 2) <= 1;
    }

    private Position selectMountainNeighbor(Terrain[][] layout, Position seed) {
        Position best = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (Position neighbor : getNeighbors(seed)) {
            if (!canPlaceMountain(layout, neighbor)) {
                continue;
            }
            double score = mountainSeedScore(layout, neighbor) + random.nextDouble();
            if (score > bestScore) {
                bestScore = score;
                best = neighbor;
            }
        }

        return best;
    }

    private Position selectRiverStart(Terrain[][] layout) {
        List<Position> candidates = new ArrayList<Position>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position start = new Position(x, y);
                if (layout[y][x] != Terrain.RIVER && layout[y][x] != Terrain.MOUNTAIN) {
                    candidates.add(start);
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Collections.shuffle(candidates, random);
        Collections.sort(candidates, new Comparator<Position>() {
            @Override
            public int compare(Position a, Position b) {
                return riverStartScore(layout, b) - riverStartScore(layout, a);
            }
        });

        return candidates.get(0);
    }

    private int riverStartScore(Terrain[][] layout, Position start) {
        int score = 0;
        Terrain current = layout[start.getY()][start.getX()];
        if (current != Terrain.MOUNTAIN) {
            score += 4;
        }
        if (current == Terrain.DESERT || current == Terrain.PLAINS) {
            score += 2;
        }

        int centerDistance = Math.abs(start.getX() - (width / 2));
        score -= centerDistance;
        return score;
    }

    private int carveSingleRiver(Terrain[][] layout, Position start, int length) {
        int carved = 0;
        Position current = start;
        Position previous = null;

        while (carved < length && current != null) {
            if (layout[current.getY()][current.getX()] != Terrain.RIVER) {
                layout[current.getY()][current.getX()] = Terrain.RIVER;
                carved++;
            }

            previous = current;
            current = chooseNextRiverStep(layout, current, previous);
        }

        return carved;
    }

    private Position chooseNextRiverStep(Terrain[][] layout, Position current, Position previous) {
        List<Position> candidates = new ArrayList<Position>();
        int[] xSteps = new int[] {-1, 0, 1};

        for (int i = 0; i < xSteps.length; i++) {
            Position next = new Position(current.getX() + xSteps[i], current.getY() + 1);
            if (!isValidPosition(next)) {
                continue;
            }
            if (previous != null && next.equals(previous)) {
                continue;
            }
            if (layout[next.getY()][next.getX()] == Terrain.RIVER) {
                continue;
            }
            candidates.add(next);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        Position best = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Position candidate : candidates) {
            double score = riverContinuationScore(layout, candidate);
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return best;
    }

    private double riverContinuationScore(Terrain[][] layout, Position candidate) {
        double score = 0.0;
        Terrain terrain = layout[candidate.getY()][candidate.getX()];

        if (terrain != Terrain.MOUNTAIN) {
            score += 3.0;
        }
        if (terrain == Terrain.DESERT || terrain == Terrain.PLAINS) {
            score += 1.5;
        }

        for (Position neighbor : getOrthogonalNeighbors(candidate)) {
            if (layout[neighbor.getY()][neighbor.getX()] == Terrain.RIVER) {
                score -= 4.0;
            }
        }

        score -= Math.abs(candidate.getX() - (width / 2)) * 0.4;
        score += random.nextDouble();
        return score;
    }

    private boolean wouldExceedBiomeFootprint(Terrain[][] layout, Position position, Terrain terrain) {
        List<Position> component = collectMergedNeighborComponent(layout, position, terrain);
        if (component.isEmpty()) {
            return false;
        }

        int minX = position.getX();
        int maxX = position.getX();
        int minY = position.getY();
        int maxY = position.getY();

        for (Position member : component) {
            if (member.getX() < minX) {
                minX = member.getX();
            }
            if (member.getX() > maxX) {
                maxX = member.getX();
            }
            if (member.getY() < minY) {
                minY = member.getY();
            }
            if (member.getY() > maxY) {
                maxY = member.getY();
            }
        }

        int componentWidth = maxX - minX + 1;
        int componentHeight = maxY - minY + 1;
        return componentWidth > maxBiomeWidth(terrain) || componentHeight > maxBiomeHeight(terrain);
    }

    private List<Position> collectMergedNeighborComponent(Terrain[][] layout, Position position, Terrain terrain) {
        List<Position> merged = new ArrayList<Position>();
        Set<Position> visited = new HashSet<Position>();
        List<Position> stack = new ArrayList<Position>();

        for (Position neighbor : getNeighbors(position)) {
            if (layout[neighbor.getY()][neighbor.getX()] == terrain && !visited.contains(neighbor)) {
                stack.add(neighbor);
                visited.add(neighbor);
            }
        }

        while (!stack.isEmpty()) {
            Position current = stack.remove(stack.size() - 1);
            merged.add(current);

            for (Position neighbor : getNeighbors(current)) {
                if (layout[neighbor.getY()][neighbor.getX()] == terrain && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return merged;
    }

    private int maxBiomeWidth() {
        return Math.max(2, width / 3);
    }

    private int maxBiomeHeight() {
        return Math.max(2, height / 3);
    }

    private int maxBiomeWidth(Terrain terrain) {
        if (terrain == Terrain.FOREST) {
            return Math.max(2, width / 8);
        }
        return maxBiomeWidth();
    }

    private int maxBiomeHeight(Terrain terrain) {
        if (terrain == Terrain.FOREST) {
            return Math.max(2, height / 8);
        }
        return maxBiomeHeight();
    }

    private int countTerrainWithinRadius(Terrain[][] layout, Position center, Terrain terrain, int radius) {
        int count = 0;

        for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
            for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
                Position candidate = new Position(x, y);
                if (!isValidPosition(candidate) || candidate.equals(center)) {
                    continue;
                }
                if (layout[y][x] == terrain) {
                    count++;
                }
            }
        }

        return count;
    }

    private Terrain[][] copyLayout(Terrain[][] layout) {
        Terrain[][] copy = new Terrain[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(layout[y], 0, copy[y], 0, width);
        }
        return copy;
    }

    private Terrain majorityNeighborTerrain(Terrain[][] layout, Position position, Terrain fallback) {
        EnumMap<Terrain, Integer> counts = new EnumMap<Terrain, Integer>(Terrain.class);
        for (Terrain terrain : Terrain.values()) {
            counts.put(terrain, 0);
        }

        for (Position neighbor : getNeighbors(position)) {
            Terrain terrain = layout[neighbor.getY()][neighbor.getX()];
            if (terrain != null) {
                counts.put(terrain, counts.get(terrain) + 1);
            }
        }

        Terrain best = fallback;
        int bestCount = -1;
        for (Terrain terrain : Terrain.values()) {
            if (counts.get(terrain) > bestCount) {
                best = terrain;
                bestCount = counts.get(terrain);
            }
        }
        return best;
    }

    private int countNeighborTerrain(Terrain[][] layout, Position position, Terrain terrain) {
        int count = 0;
        for (Position neighbor : getNeighbors(position)) {
            if (layout[neighbor.getY()][neighbor.getX()] == terrain) {
                count++;
            }
        }
        return count;
    }

    private List<Position> getOrthogonalNeighbors(Position position) {
        List<Position> neighbors = new ArrayList<Position>(4);
        int[][] deltas = new int[][] {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (int i = 0; i < deltas.length; i++) {
            Position next = new Position(position.getX() + deltas[i][0], position.getY() + deltas[i][1]);
            if (isValidPosition(next)) {
                neighbors.add(next);
            }
        }

        return neighbors;
    }

    private List<Position> getNeighbors(Position position) {
        List<Position> neighbors = new ArrayList<Position>(8);
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                Position next = new Position(position.getX() + dx, position.getY() + dy);
                if (isValidPosition(next)) {
                    neighbors.add(next);
                }
            }
        }
        return neighbors;
    }

    private boolean hasUnassignedTiles(Terrain[][] layout) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (layout[y][x] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    private int randomRange(int minInclusive, int maxInclusive) {
        if (maxInclusive <= minInclusive) {
            return minInclusive;
        }
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
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
