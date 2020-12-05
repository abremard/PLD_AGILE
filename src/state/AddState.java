package state;

import command.ListOfCommands;
import command.AddRequestCommand;
import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;

public class AddState implements State {
    public void done(ListOfCommands l, MVCController c, PlanningRequest p, Request r) {
        l.Add(new AddRequestCommand(p, r));
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print(l.getI());
            System.out.println(" - Confirming addition of a new request to the request list ");
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from AddState to ModifyState without having changed anything");
        }
    }
}
