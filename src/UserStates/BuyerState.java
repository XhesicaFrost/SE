package UserStates;

import Graphic.GraphicalManager;
import UserStates.BuyerStates.BuyerStatemachine;

public class BuyerState extends UserState {
    public BuyerState() {
        super(new GraphicalManager(){});
    }

    public void enter() {
        super.enter();
        BuyerStatemachine.initialize();
    }
    public void update() {
        super.update();
        BuyerStatemachine.getCurrentState().update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
