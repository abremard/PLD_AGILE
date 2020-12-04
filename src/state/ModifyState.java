package state;

import command.ApplyModificationCommand;
import command.ListOfCommands;
import controller.MVCController;
import objects.PlanningRequest;
import objects.Map;

//TODO littéralement tout sauf la transition entre ModifyState et AddState

public class ModifyState implements State {
    public void addRequest(MVCController c){
        c.setCurrentState(c.getAddState());
        if (debug) {
            System.out.print("Going from ModifyState to AddState");
        }
    }

    public void removeRequest(MVCController c){
        c.setCurrentState(c.getRemoveState());
        if (debug) {
            System.out.print("Going from ModifyState to RemoveState");
        }
    }

    public void modifyRequest(MVCController c){
        c.setCurrentState(c.getModifyRequestState());
        if (debug) {
            System.out.print("Going from ModifyState to ModifyRequestState");
        }
    }

    public void done(ListOfCommands l, MVCController c, Map m, PlanningRequest p) {
        l.Add(new ApplyModificationCommand(m, p));
        c.setCurrentState(c.getTourState());
        if (debug) {
            System.out.print("Confirming all the modification to the request list ");
            System.out.println(l.getI());
        }
    }
}
