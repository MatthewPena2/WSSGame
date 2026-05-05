import Vision.Vision;

public class Player {
    private int id;
    private String name;
    private double maxStrength = 100, currentStrength = 100;
    private double maxFood = 50, maxWater = 50, currentWater = 50, currentFood = 50;
    private int gold=0;
    private boolean isAlive = true;
    private Vision vision;
    private Brain brain;
    
    //constructor for the player class
    public Player(int id, String name){
        this.id = id;
        this.name = name;
    }

    //setters and getters for the player attributes
    public void setVision(Vision vision){this.vision = vision;}

    public void setBrain(Brain brain){this.brain = brain;}

    public Vision getVision() { return vision; }

    public Brain getBrain() { return brain; }

    public int getId(){return id;}

    public String getName(){return name;}

    public double getCurrentStrength(){return currentStrength;}
    
    public double getCurrentFood(){return currentFood;}
    
    public double getCurrentWater(){return currentWater;}
    
    public int getGold(){return gold;}

    //As the player moves, they lose strength. If strength reaches PAST 0, the player dies.
    public void decreaseStrength(){
        currentStrength -= 10;
        if(currentStrength < 0){
            currentStrength = 0;
            isAlive = false;
        }
    }

    //The current water and current food values also go down 
    // by the terrain costs. If either of these reaches PAST 0, the player dies.
    public void decreaseFoodandWater(double foodCost, double waterCost){
        currentFood -= foodCost;
        currentWater -= waterCost;
        if(currentFood < 0 || currentWater < 0){
            isAlive = false;
        }
    }

    // the person collects bonuses or trades, the current values 
    // go up. However, the current values can never exceed the maximum values.
    public void updateStrength(double amount){
       currentStrength = Math.min(maxStrength, currentStrength + amount);
    
    }
    public void updateFood(double amount){
        currentFood = Math.min(maxFood, currentFood + amount);
    }
    public void updateWater(double amount){
        currentWater = Math.min(maxWater, currentWater + amount);
    }
    public void updateGold(int amount){
        gold += amount;
    }

    //check if the player is alive
    public boolean isAlive(){return isAlive;}



}