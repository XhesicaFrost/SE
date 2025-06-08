package UserStates;

import Graphic.GMDistributor;

public class RegistrationState extends UserState {
    public RegistrationState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
