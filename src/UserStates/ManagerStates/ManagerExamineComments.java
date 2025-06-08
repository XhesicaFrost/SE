package UserStates.ManagerStates;

import Graphic.GMDistributor;

public class ManagerExamineComments extends ManagerBasic {
    public ManagerExamineComments() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("ExamineItems")) {
            ManagerStatemachine.changeState(new ManagerExamineItems());
            return;
        }
        if(gManager.onClickButtonName().equals("ExamineSellers")) {
            ManagerStatemachine.changeState(new ManagerExamineSellers());
            return;
        }
    }
}
