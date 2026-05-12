package GameEntity;

public enum PlayerType {
    EXPLORER(100.0, 50.0, 50.0, "Increased Vision range to scout the map."),
    SURVIVOR(100.0, 80.0, 80.0,"Starts with extra Food and Water supplies."),
    WARRIOR(150.0, 50.0, 50.0,"Higher starting Strength for difficult terrain.");

    private final double startStrength;
    private final double startFood;
    private final double startWater;
    private final String description;

    PlayerType(double str, double food, double water, String desc) {
        this.startStrength = str;
        this.startFood = food;
        this.startWater = water;
        this.description = desc;
    }

    public double getStartStrength() { return startStrength; }
    public double getStartFood() { return startFood; }
    public double getStartWater() { return startWater; }
    public String getDescription() { return description; }
}