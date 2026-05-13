package Vision;

import Map.Direction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private final List<Direction> moves;
    private final int             moveCost;
    private final double          waterCost;
    private final double          foodCost;

    public Path(List<Direction> moves, int moveCost, double waterCost, double foodCost) {
        this.moves     = Collections.unmodifiableList(new ArrayList<>(moves));
        this.moveCost  = moveCost;
        this.waterCost = waterCost;
        this.foodCost  = foodCost;
    }

    public List<Direction> getMoves()    {
        return moves;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public double getWaterCost() {
        return waterCost;
    }

    public double getFoodCost() {
        return foodCost;
    }
    
    public int getLength() {
        return moves.size();
    }

    // Returns the first Direction in the path.
    // Brain uses this to take one step at a time rather than committing
    // to the whole path at once.
    public Direction firstMove() {
        return moves.isEmpty() ? null : moves.get(0);
    }

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

    @Override
    public String toString() { return getSummary(); }
}