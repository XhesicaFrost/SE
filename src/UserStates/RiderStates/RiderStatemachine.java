package UserStates.RiderStates;

public class RiderStatemachine {
    private static RiderBasic currentState;
    public static RiderBasic getCurrentState()
    {
        return currentState;
    }
    public static void initialize()
    {
        currentState = new RiderOrdersManage();
        currentState.enter();
    }
    public static void changeState(RiderBasic newState)
    {
        currentState.exit();
        currentState = newState;
        currentState.enter();
    }
}
