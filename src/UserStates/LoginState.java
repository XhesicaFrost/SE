package UserStates;

import DataBase.DataBaseManager;
import Graphic.GMDistributor;
import Users.User;

public class LoginState extends UserState {
    public LoginState() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Register")) {
            UserStateStatemachine.changeState(new RegistrationState());
            return;
        }
        if(gManager.onClickButtonName().equals("Login")) {
            Object[] tmp = DataBaseManager.query("user-type");
            if(tmp == null) return;//todo: actually we should throw exception here.
            int nowUsertype = (Integer)tmp[0];
            switch (nowUsertype)
            {
                case User.NOT_USER: {
                    // ?
                    break;
                }
                case User.MANAGER: {
                    UserStateStatemachine.changeState(new ManagerState());
                    break;
                }
                case User.BUYER: {
                    UserStateStatemachine.changeState(new BuyerState());
                    break;
                }
                case User.RIDER: {
                    UserStateStatemachine.changeState(new RiderState());
                    break;
                }
                case User.SELLER: {
                    UserStateStatemachine.changeState(new SellerState());
                    break;
                }
                default: {
                    break;
                }
            }
            return;
        }
    }
}
