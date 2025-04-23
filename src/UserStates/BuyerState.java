package UserStates;

import Graphic.GMDistributor;
import UserStates.BuyerStates.BuyerStatemachine;

public class BuyerState extends UserState {
    public BuyerState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
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
