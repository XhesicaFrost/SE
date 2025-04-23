package UserStates.BuyerStates;

import Graphic.GMDistributor;

public class BuyerPersonal extends BuyerBasic {
    public BuyerPersonal() {
        gManager=GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Search")) {
            BuyerStatemachine.changeState(new BuyerSearch());
            return;
        }
        if(gManager.onClickButtonName().equals("Suggest")) {
            BuyerStatemachine.changeState(new BuyerSuggest());
            return;
        }
    }
}
