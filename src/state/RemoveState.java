package state;

import command.ListOfCommands;
import command.RemoveRequestCommand;
import controller.MVCController;
import objects.PlanningRequest;

public class RemoveState implements State {
    public void done(ListOfCommands l, MVCController c, PlanningRequest p, int i) {
        l.Add(new RemoveRequestCommand(p, i));
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Confirming removal of a request of the request list ");
            System.out.println(l.getI());
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Going from RemoveState to ModifyState without having changed anything");
        }
    }
}
