package state;

import command.ListOfCommands;
import command.RemoveRequestCommand;
import controller.MVCController;
import objects.PlanningRequest;
import sample.Controller;

import java.util.ArrayList;

public class RemoveState implements State {
    public void removeDone(ListOfCommands l, MVCController c, PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2) {
        l.Add(new RemoveRequestCommand(oldPlanningRequest, ltcList, removedRequestIndex, removedCardIndex1, removedCardIndex2));
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print(l.getI());
            System.out.println(" - Confirming removal of a request of the request list ");
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Going from RemoveState to ModifyState without having changed anything");
        }
    }
}
