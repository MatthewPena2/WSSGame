package wss.map;

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

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public boolean hasReachedGoal() {
        return reachedGoal;
    }
}
