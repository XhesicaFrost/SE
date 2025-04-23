package UserStates;

import Graphic.GMDistributor;
import UserStates.RiderStates.RiderStatemachine;

public class RiderState extends UserState {
    public RiderState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void enter() {
        super.enter();
        RiderStatemachine.initialize();
    }
    public void update() {
        super.update();
        RiderStatemachine.getCurrentState().update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
