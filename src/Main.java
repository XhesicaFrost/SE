import UserStates.UserStateStatemachine;

public class Main {
    public static void main(String[] args) {
        UserStateStatemachine.initialize();
        while(true) {
            UserStateStatemachine.getCurrentState().update();
        }
    }
}
