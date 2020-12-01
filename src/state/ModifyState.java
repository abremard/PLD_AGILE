package state;

import controller.MVCController;

//TODO littéralement tout sauf la transition entre ModifyState et AddState

public class ModifyState implements State {
    public void addRequest(MVCController c){
        c.setCurrentState(c.getAddState());
        if (debug) {
            System.out.print("Going from ModifyState to AddState");
        }
    }
}
