package Map;

public enum Difficulty {
    EASY(0.01),
    MEDIUM(0.05),
    HARD(0.10);

    private final double modifier;

    Difficulty(double modifier){this.modifier = modifier;}

    public double getModifier(){return modifier;}
}
