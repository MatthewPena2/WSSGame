package Vision;

import Map.Direction;
import Map.Position;
import Map.WildernessMap;
import Map.Tile;


/**
 * FocusedVision
 * 
 * Range: 1
 * Directions: NE, E, SE
 * 
 * A Vision type that only scans the three tiles directly ahead on the east arc
 *
 */
class FocusedVision extends Vision {

    private static final int RANGE = 1;
    private static final Direction[] DIRECTIONS = {
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST
    };

    /**
     * Constructs a FocusedVision centered on the player's position
     * 
     * @param playerLocation    The current location of the player
     * @param map               Map used for Vision calculations
     */
    public FocusedVision(Position playerLocation, WildernessMap map) {
        super(playerLocation, map);
    }

    /**
     * Updates the visible scope based on a narrow east-focused direction set.
     * 
     * @param playerLocation    The current location of the player
     */
    @Override
    public void updateScope(Position playerLocation) {
        scope = resolveScope(playerLocation, DIRECTIONS, RANGE);
    }
}


/**
 * CautiousVision
 * 
 * Range: 1
 * Directions: N, E, S
 * 
 * A Vision type designed for conservative exploration and prevents backtracking
 *
 */
class CautiousVision extends Vision {

    private static final int RANGE = 1;
    private static final Direction[] DIRECTIONS = {
        Direction.NORTH,
        Direction.EAST,
        Direction.SOUTH
    };

    /**
     * Constructs a CautiousVision centered on the player's position
     * 
     * @param playerLocation    The current location of the player
     * @param map               The map used for Vision calculations
     */
    public CautiousVision(Position playerLocation, WildernessMap map) {
        super(playerLocation, map);
    }

    /**
     * Updates visible tiles using a forward-biased directional set
     * 
     * @param playerLocation    The current location of the player
     */
    @Override
    public void updateScope(Position playerLocation) {
        scope = resolveScope(playerLocation, DIRECTIONS, RANGE);
    }
}


/**
 * KeenEyedVision
 * 
 * Range: 1
 * Directions: N, NE, E, SE, S
 * 
 * A Vision type designed to provide broad forward awareness while maintaining directional focus.
 */
class KeenEyedVision extends Vision {

    private static final int RANGE = 1;
    private static final Direction[] DIRECTIONS = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH
    };

    /**
     * Constructs a KeenEyedVision centered on the player's location
     * 
     * @param playerLocation    The current location of the player
     * @param map               The map used for Vision calculations
     */
    public KeenEyedVision(Position playerLocation, WildernessMap map) {
        super(playerLocation, map);
    }

    /**
     * Updates the visible scope using a balanced directional arc
     * 
     * @params playerLocation   The current location of the player
     */
    @Override
    public void updateScope(Position playerLocation) {
        scope = resolveScope(playerLocation, DIRECTIONS, RANGE);
    }
}


/**
 * HindSightVision
 * 
 * Behavior:
 *  - W, NW, SW (Range 2)
 *  - E (Range 1)
 * 
 * A Vision type designed to support backtracking behavior
 */
class HindSightVision extends Vision {

    private static final Direction[] BEHIND   = {
        Direction.WEST,
        Direction.NORTHWEST,
        Direction.SOUTHWEST
    };

    private static final Direction[] AHEAD    = {
        Direction.EAST
    };

    /**
     * Constructs a HindSightVision centered on the player's location
     * 
     * @param playerLocation    The current location of the player
     * @param map               The map used for Vision calculations
     */
    public HindSightVision(Position playerLocation, WildernessMap map) {
        super(playerLocation, map);
    }

    /**
     * Updates the visible scope by combining backward and forward scans
     * 
     * The results are merged into a single visible scope array.
     * 
     * @param playerLocation    The current location of the player
     */
    @Override
    public void updateScope(Position playerLocation) {
        Tile[] behind = resolveScope(playerLocation, BEHIND, 2);  // 2 tiles west arc
        Tile[] ahead  = resolveScope(playerLocation, AHEAD,  1);  // 1 tile east

        // Merge both arrays into scope
        Tile[] merged = new Tile[behind.length + ahead.length];
        System.arraycopy(behind, 0, merged, 0,              behind.length);
        System.arraycopy(ahead,  0, merged, behind.length,  ahead.length);
        scope = merged;
    }
}