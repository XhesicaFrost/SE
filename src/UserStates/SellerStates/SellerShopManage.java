package UserStates.SellerStates;

import Graphic.GMDistributor;

public class SellerShopManage extends SellerBasic{
    public SellerShopManage() {
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
        if(gManager.onClickButtonName().equals("Personal")) {
            SellerStatemachine.changeState(new SellerPersonal());
            return;
        }
    }
}
