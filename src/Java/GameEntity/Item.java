package GameEntity;

import java.awt.*;

// Represents a rare collectible that appears on a tile and boosts one player resource.
public class Item {
    public enum ItemType {
        FOOD,
        WATER,
        GOLD
    }

    private final int mapCol;
    private final int mapRow;
    private final ItemType type;
    private boolean collected;

    public Item(int mapCol, int mapRow, ItemType type) {
        this.mapCol = mapCol;
        this.mapRow = mapRow;
        this.type = type;
        this.collected = false;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isOnTile(int col, int row) {
        return mapCol == col && mapRow == row;
    }

    public void collect(Player player) {
        if (collected) {
            return;
        }

        switch (type) {
            case FOOD:
                player.foodAmount += 10;
                break;
            case WATER:
                player.waterAmount += 15;
                break;
            case GOLD:
                player.goldAmount += 5;
                break;
        }

        collected = true;
    }

    public void draw(Graphics2D g2, int tileSize) {
        if (collected) {
            return;
        }

        int pixelX = mapCol * tileSize;
        int pixelY = mapRow * tileSize;
        int markerSize = Math.max(8, tileSize / 5);
        int padding = Math.max(3, tileSize / 12);

        g2.setColor(getDisplayColor());
        g2.fillRect(pixelX + padding, pixelY + padding, markerSize, markerSize);
        g2.setColor(Color.BLACK);
        g2.drawRect(pixelX + padding, pixelY + padding, markerSize, markerSize);
    }

    private Color getDisplayColor() {
        switch (type) {
            case FOOD:
                return Color.RED;
            case WATER:
                return Color.BLUE;
            case GOLD:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }
}
