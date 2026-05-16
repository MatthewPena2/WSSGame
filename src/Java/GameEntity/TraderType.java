package GameEntity;

public enum TraderType {
    //two types of traders: Normal and Greedy
    //A greedy trader has steeper prices than a normal trader
    NORMAL(10, 8),
    GREEDY(20, 15);

    //Each trader is going to have a minimum food/water price
    //The values of these prices are going to depend on the trader type
    private final int minFoodPrice;
    private final int minWaterPrice;

    //constructor
    TraderType(int foodP, int waterP){
        minFoodPrice = foodP;
        minWaterPrice = waterP;
    }

    //accessor methods for getting minFoodPrice/minWaterPrice
    public int getMinFoodPrice(){return minFoodPrice;}
    public int getMinWaterPrice(){return minWaterPrice;}
}
