package UserStates.BuyerStates;

import Graphic.GraphicalManager;

public class BuyerSearch extends BuyerBasic {
    public BuyerSearch() {
        super(new GraphicalManager(){});
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
