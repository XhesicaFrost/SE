package UserStates;

import Graphic.GraphicalManager;

public abstract class UserState extends StateBasic
{
    public UserState(GraphicalManager gManager) {
        super(gManager);
    }

    public void enter() {
        super.enter();
    }
    public void update() {
        super.update();
    }
    public void exit() {
        super.exit();
    }
}