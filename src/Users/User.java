package Users;

public class User {
    private int userType;
    public final static int NOT_USER = 0;
    public final static int MANAGER = 1;
    public final static int BUYER = 2;
    public final static int RIDER = 3;
    public final static int SELLER = 4;

    public int getUserType() {
        return userType;
    }
    public void setUserType(int userType) {
        this.userType = userType;
    }
}
