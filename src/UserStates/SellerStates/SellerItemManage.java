package UserStates.SellerStates;

import Graphic.GMDistributor;

public class SellerItemManage extends SellerBasic {
    public SellerItemManage() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("OrderManage")) {
            SellerStatemachine.changeState(new SellerOrderManage());
            return;
        }
        if(gManager.onClickButtonName().equals("Personal")) {
            SellerStatemachine.changeState(new SellerPersonal());
            return;
        }
        if(gManager.onClickButtonName().equals("ShopManage")) {
            SellerStatemachine.changeState(new SellerShopManage());
            return;
        }
    }
}
