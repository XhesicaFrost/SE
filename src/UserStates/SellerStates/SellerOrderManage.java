package UserStates.SellerStates;

import Graphic.GMDistributor;

public class SellerOrderManage extends SellerBasic {
    public SellerOrderManage() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("ItemManage")) {
            SellerStatemachine.changeState(new SellerItemManage());
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
