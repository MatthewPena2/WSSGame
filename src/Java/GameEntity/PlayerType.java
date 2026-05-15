package GameEntity;
import Vision.VisionManager;
import Vision.VisionManager.Type;

public enum PlayerType {
    EXPLORER(100.0, 50.0, 50.0, "Increased Vision range to scout the map.", VisionManager.Type.HIND_SIGHT),
    SURVIVOR(100.0, 80.0, 80.0,"Starts with extra Food and Water supplies.", VisionManager.Type.CAUTIOUS),
    WARRIOR(150.0, 50.0, 50.0,"Higher starting Strength for difficult terrain.", VisionManager.Type.FOCUSED),
    ADVENTURER(120.0, 60.0, 60.0,"Balanced stats for versatile gameplay.", VisionManager.Type.KEEN_EYED);

    private final double startStrength;
    private final double startFood;
    private final double startWater;
    private final String description;
    private final Type vision;
    

    PlayerType(double str, double food, double water, String desc, VisionManager.Type visionManager) {
        this.startStrength = str;
        this.startFood = food;
        this.startWater = water;
        this.description = desc;
        this.vision = visionManager;
    }

    public double getStartStrength() { return startStrength; }
    public double getStartFood() { return startFood; }
    public double getStartWater() { return startWater; }
    public String getDescription() { return description; }
    public VisionManager.Type getVision() { return vision; }

}