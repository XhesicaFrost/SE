package UserStates.BuyerStates;

import Graphic.GraphicalManager;

public class BuyerSuggest extends BuyerBasic {
    public BuyerSuggest() {
        super(new GraphicalManager(){});
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
