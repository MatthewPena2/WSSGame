package Map;

public enum PlayerType {
    EXPLORER(100.0, 50.0, "Increased Vision range to scout the map."),
    SURVIVOR(100.0, 80.0, "Starts with extra Food and Water supplies."),
    WARRIOR(150.0, 40.0, "Higher starting Strength for difficult terrain.");

    private final double startStrength;
    private final double startSupplies;
    private final String description;

    PlayerType(double str, double sup, String desc) {
        this.startStrength = str;
        this.startSupplies = sup;
        this.description = desc;
    }

    public double getStartStrength() { return startStrength; }
    public double getStartSupplies() { return startSupplies; }
    public String getDescription() { return description; }
}