package UserStates;

public class UserStateStatemachine
{
    private static UserState currentState;
    public static UserState getCurrentState()
    {
        return currentState;
    }
    public static void initialize()
    {
        currentState = new LoginState();
        currentState.enter();
    }
    public static void changeState(UserState newState)
    {
        currentState.exit();
        currentState = newState;
        currentState.enter();
    }
}