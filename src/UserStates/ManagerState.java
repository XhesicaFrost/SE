package UserStates;

import Graphic.GraphicalManager;
import UserStates.ManagerStates.ManagerStatemachine;

public class ManagerState extends UserState {
    public ManagerState() {
        super(new GraphicalManager(){});
    }
    public void enter() {
        super.enter();
        ManagerStatemachine.initialize();
    }
    public void update() {
        super.update();
        ManagerStatemachine.getCurrentState().update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
