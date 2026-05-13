package Vision;

import Map.Position;
import Map.WildernessMap;


/**
 * The VisionManager class is responsible for creating the different vision types. 
 * It is the ONLY entry point into the Vision package.
 */
public class VisionManager {

    public enum Type {
        FOCUSED,     // NE, E, SE               range 1 — aggressive east push
        CAUTIOUS,    // N, E, S                 range 1 — no backtracking
        KEEN_EYED,   // N, NE, E, SE, S         range 1 — balanced forward arc
        HIND_SIGHT   // W, NW, SW (range 2) + E (range 1) — backtracker
    }

    /**
     * This creates a new Vision object based on the Vision type
     * 
     * @param type              The type of Vision to create
     * @param playerLocation    The player's current position
     * @param map               The map that the Vision will analyze
     * @return                  The Vision implementation based on the selected type
     */
    public static Vision create(Type type, Position playerLocation, WildernessMap map) {
        switch (type) {

            case FOCUSED:    
                return new FocusedVision(playerLocation, map);

            case CAUTIOUS:
                return new CautiousVision(playerLocation, map);

            case KEEN_EYED:
                return new KeenEyedVision(playerLocation, map);

            case HIND_SIGHT:
                return new HindSightVision(playerLocation, map);

            default:
                throw new IllegalArgumentException("Unknown Vision type: " + type);
        }
    }

    // Prevents instantiation
    private VisionManager() {}
}