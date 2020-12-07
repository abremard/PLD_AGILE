package state;

import command.EditRequestCommand;
import command.ListOfCommands;
import controller.MVCController;
import objects.Request;

public class ModifyRequestState implements State {
    public void modifyRequestDone(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest) {
        l.Add(new EditRequestCommand(oldRequest, newRequest), c);
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println(l.getI()+" - Confirming modification of a request of the request list ");
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from ModifyRequestState to ModifyState without having changed anything");
        }
    }
}
