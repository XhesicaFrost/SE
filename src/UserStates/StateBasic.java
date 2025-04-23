package UserStates;

import Graphic.GraphicalManager;

public abstract class StateBasic
{
    protected GraphicalManager gManager;
    /*
    public GraphicalManager getgManager() {
        return gManager;
    }
    public void setgManager(GraphicalManager gManager) {
        this.gManager = gManager;
    }
    */
    public StateBasic() {}

    public void enter() {
        gManager.initialize();
    }
    public void update() {
        gManager.update();
    }
    public void exit() {
        gManager.close();
    }
}