package UserStates.BuyerStates;

public class BuyerStatemachine {
    private static BuyerBasic currentState;
    public static BuyerBasic getCurrentState()
    {
        return currentState;
    }
    public static void initialize()
    {
        currentState = new BuyerSuggest();
        currentState.enter();
    }
    public static void changeState(BuyerBasic newState)
    {
        currentState.exit();
        currentState = newState;
        currentState.enter();
    }
}
