package UserStates.SellerStates;

public class SellerStatemachine {
    private static SellerBasic currentState;
    public static SellerBasic getCurrentState()
    {
        return currentState;
    }
    public static void initialize()
    {
        currentState = new SellerPersonal();
        currentState.enter();
    }
    public static void changeState(SellerBasic newState)
    {
        currentState.exit();
        currentState = newState;
        currentState.enter();
    }
}
