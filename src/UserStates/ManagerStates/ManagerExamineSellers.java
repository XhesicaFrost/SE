package UserStates.ManagerStates;

import Graphic.GMDistributor;

public class ManagerExamineSellers extends ManagerBasic {
    public ManagerExamineSellers() {
        gManager= GMDistributor.getGM(this.getClass().getName());
    }
    public void update() {
        super.update();
        if(gManager.onClickButtonName().equals("ExamineItems")) {
            ManagerStatemachine.changeState(new ManagerExamineItems());
            return;
        }
        if(gManager.onClickButtonName().equals("ExamineComments")) {
            ManagerStatemachine.changeState(new ManagerExamineComments());
            return;
        }
    }
}
