package Map;

// Packages the result of trying to move the player onto another tile.
public class MoveResult {
    private final boolean success;
    private final String message;
    private final int movementCost;
    private final Position newPosition;
    private final boolean reachedGoal;

    public MoveResult(
            boolean success,
            String message,
            int movementCost,
            Position newPosition,
            boolean reachedGoal) {
        this.success = success;
        this.message = message;
        this.movementCost = movementCost;
        this.newPosition = newPosition;
        this.reachedGoal = reachedGoal;
    }

    // Indicates whether the move actually succeeded.
    public boolean isSuccess() {
        return success;
    }

    // Returns the user-facing message describing the move outcome.
    public String getMessage() {
        return message;
    }

    // Returns the resource cost of the move that was attempted.
    public int getMovementCost() {
        return movementCost;
    }

    // Returns the player's position after the move attempt.
    public Position getNewPosition() {
        return newPosition;
    }

    // Indicates whether the move reached the east edge win condition.
    public boolean hasReachedGoal() {
        return reachedGoal;
    }
}
