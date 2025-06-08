package UserStates.BuyerStates;

import Graphic.GMDistributor;

public class BuyerSuggest extends BuyerBasic {
    public BuyerSuggest() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Search")) {
            BuyerStatemachine.changeState(new BuyerSearch());
            return;
        }
        if(gManager.onClickButtonName().equals("Personal")) {
            BuyerStatemachine.changeState(new BuyerPersonal());
            return;
        }
    }
}
