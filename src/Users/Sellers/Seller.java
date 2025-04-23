package Users.Sellers;

import Entity.Shops.Shop;
import Users.User;

import java.util.ArrayList;

public class Seller extends User {
    private final ArrayList<Shop> shopList;
    public Seller() {
        super();
        shopList = new ArrayList<Shop>();
    }
    public ArrayList<Shop> getShopList() {
        return shopList;
    }
    public void addShop(Shop shop) {
        shopList.add(shop);
    }
    public void removeShop(Shop shop) {
        shopList.remove(shop);
    }
}
