package state;

import command.EditRequestCommand;
import command.ListOfCommands;
import controller.MVCController;
import objects.Request;

public class ModifyRequestState implements State {
    public void done(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest) {
        l.Add(new EditRequestCommand(oldRequest, newRequest));
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Confirming modification of a request of the request list ");
            System.out.println(l.getI());
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Going from ModifyRequestState to ModifyState without having changed anything");
        }
    }
}
