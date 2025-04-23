package UserStates.SellerStates;

import Graphic.GMDistributor;

public class SellerPersonal extends SellerBasic {
    public SellerPersonal() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("ItemManage")) {
            SellerStatemachine.changeState(new SellerItemManage());
            return;
        }
        if(gManager.onClickButtonName().equals("OrderManage")) {
            SellerStatemachine.changeState(new SellerOrderManage());
            return;
        }
        if(gManager.onClickButtonName().equals("ShopManage")) {
            SellerStatemachine.changeState(new SellerShopManage());
            return;
        }
    }
}
