package UserStates.ManagerStates;

public class ManagerStatemachine {
    private static ManagerBasic currentState;
    public static ManagerBasic getCurrentState()
    {
        return currentState;
    }
    public static void initialize()
    {
        currentState = new ManagerExamineComments();
        currentState.enter();
    }
    public static void changeState(ManagerBasic newState)
    {
        currentState.exit();
        currentState = newState;
        currentState.enter();
    }
}
