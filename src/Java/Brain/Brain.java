package Brain;

import Map.Direction;

/**
 * The Brain class acts as the decision-maker for the player in automatic mode
 */
public abstract class Brain {
    /**
     * Analyzes the current state of the player and returns the next direction for the move
     * Based on the direction, the player will move according to player.update()
     * @return The direction the player should move
     */

    public abstract Direction makeMove();
}
