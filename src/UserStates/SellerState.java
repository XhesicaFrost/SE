package UserStates;

import Graphic.GMDistributor;
import UserStates.SellerStates.SellerStatemachine;

public class SellerState extends UserState {
    public SellerState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void enter() {
        super.enter();
        SellerStatemachine.initialize();
    }
    public void update() {
        super.update();
        SellerStatemachine.getCurrentState().update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
