package UserStates.BuyerStates;

import Graphic.GMDistributor;

public class BuyerSearch extends BuyerBasic {
    public BuyerSearch() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("Personal")) {
            BuyerStatemachine.changeState(new BuyerPersonal());
            return;
        }
        if(gManager.onClickButtonName().equals("Suggest")) {
            BuyerStatemachine.changeState(new BuyerSuggest());
            return;
        }
    }
}
