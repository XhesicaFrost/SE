package UserStates;

import Graphic.GraphicalManager;

public class RegistrationState extends UserState {
    public RegistrationState() {
        super(new GraphicalManager(){});
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Logout")) {
            UserStateStatemachine.changeState(new LoginState());
            return;
        }
    }
}
