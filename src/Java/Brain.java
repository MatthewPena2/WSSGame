import Vision.Vision;

public abstract class Brain {
    protected Player player;
    protected Vision vision;
    protected boolean isTrading;

    public abstract void makeMove();
}
