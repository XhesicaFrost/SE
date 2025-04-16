package UserStates.BuyerStates;

import Graphic.GraphicalManager;

public class BuyerPersonal extends BuyerBasic {
    public BuyerPersonal() {
        super(new GraphicalManager(){});
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
