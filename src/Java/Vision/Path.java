package Vision;

import Map.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Path
 * 
 * The Path class represents the computed movement path from the player's current location
 * to a target tile on the map
 * 
 * A Path contains
 *  - An ordered list of movement directions
 *  - Total movement cost
 *  - Total water cost
 *  - Total food cost
 */
public class Path {

    // Ordered sequence of movement directions
    private final List<Direction> moves;

    // Total movement cost of following this path
    private final int moveCost;

    // Total water consumption cost along the path
    private final double waterCost;

    // Total food consumption cost along the path
    private final double foodCost;

    /**
     * Constructs an immutable Path object.
     *
     * @param moves         The ordered list of movement directions
     * @param moveCost      The total movement cost of the path
     * @param waterCost     The total water cost of the path
     * @param foodCost      The total food cost of the path
     */
    public Path(List<Direction> moves, int moveCost, double waterCost, double foodCost) {
        this.moves     = Collections.unmodifiableList(new ArrayList<>(moves));
        this.moveCost  = moveCost;
        this.waterCost = waterCost;
        this.foodCost  = foodCost;
    }

    /**
     * Returns the full list of movement directions in this path.
     *
     * @return  The unmodifiable list of directions
     */
    public List<Direction> getMoves()    {
        return moves;
    }

    /**
     * Returns the total movement cost of this path.
     *
     * @return  The movement cost
     */
    public int getMoveCost() {
        return moveCost;
    }

    /**
     * Returns the total water cost of this path.
     *
     * @return  The water cost
     */
    public double getWaterCost() {
        return waterCost;
    }

    /**
     * Returns the total food cost of this path.
     *
     * @return  The food cost
     */
    public double getFoodCost() {
        return foodCost;
    }

    /**
     * Returns the number of steps (moves) in this path.
     *
     * @return  The path length
     */
    public int getLength() {
        return moves.size();
    }

    /**
     * Returns the first movement direction in the path.
     *
     * This is used by the Brain system to execute movement step-by-step
     * rather than committing to the full path at once.
     *
     * @return  The first direction in the path, or null if the path is empty
     */
    public Direction firstMove() {
        return moves.isEmpty() ? null : moves.get(0);
    }

    /**
     * Returns a human-readable summary of this path.
     *
     * @return  The formatted path summary string
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path[")
          .append(moves.size()).append(" move").append(moves.size() != 1 ? "s" : "")
          .append(" | move cost: ").append(moveCost)
          .append(" | water cost: ").append(waterCost)
          .append(" | food cost: ").append(foodCost)
          .append("] → ");
        for (int i = 0; i < moves.size(); i++) {
            sb.append(moves.get(i));
            if (i < moves.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * Returns the string representation of this Path.
     *
     * @return  The summary string of the path
     */
    @Override
    public String toString() { return getSummary(); }
}