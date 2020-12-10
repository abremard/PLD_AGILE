package state;

import command.EditRequestCommand;
import command.ListOfCommands;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

public class ModifyRequestState implements State {
    public void modifyRequestDone(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest, int editedRequestIndex, int editedCardIndex, double oldDuration, double newDuration, boolean isPickup) {
        l.Add(new EditRequestCommand(oldRequest, newRequest, editedRequestIndex, editedCardIndex, oldDuration, newDuration, isPickup), c);
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
