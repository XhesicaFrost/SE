package UserStates.ManagerStates;

import Graphic.GMDistributor;

public class ManagerExamineItems extends ManagerBasic {
    public ManagerExamineItems() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("ExamineComments")) {
            ManagerStatemachine.changeState(new ManagerExamineComments());
            return;
        }
        if(gManager.onClickButtonName().equals("ExamineSellers")) {
            ManagerStatemachine.changeState(new ManagerExamineSellers());
            return;
        }
    }
}
