package UserStates.RiderStates;

import Graphic.GMDistributor;

public class RiderOrdersManage extends RiderBasic {
    public RiderOrdersManage() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Personal")) {
            RiderStatemachine.changeState(new RiderPersonal());
            return;
        }
    }
}
