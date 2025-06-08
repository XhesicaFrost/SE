package UserStates;

import Graphic.GMDistributor;
import UserStates.ManagerStates.ManagerStatemachine;

public class ManagerState extends UserState {
    public ManagerState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
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
