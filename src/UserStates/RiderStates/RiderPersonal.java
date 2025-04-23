package UserStates.RiderStates;

import Graphic.GMDistributor;

public class RiderPersonal extends RiderBasic {
    public RiderPersonal() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("OrdersManage")) {
            RiderStatemachine.changeState(new RiderOrdersManage());
            return;
        }
    }
}
