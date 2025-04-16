package UserStates;

import Graphic.GraphicalManager;
import UserStates.RiderStates.RiderStatemachine;

public class RiderState extends UserState {
    public RiderState() {
        super(new GraphicalManager(){});
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
