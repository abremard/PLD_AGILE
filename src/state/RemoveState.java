package state;

import command.AddRequestCommand;
import command.ListOfCommands;
import command.RemoveRequestCommand;
import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

/**
 * <h1>Remove State Class</h1>
 * <p>The Remove State object that defines the state where user can remove a request from the tour</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class RemoveState implements State {

    /**
     * When the user clicks on the trash can on the IHM, the Remove Request Command is added to the history of commands (thus, executed) and current state becomes Modify State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param oldPlanningRequest The requests' planning before the removal
     * @param ltcList The LocationTagContents' list before the removal
     * @param request The request to be removed
     * @param removedCardIndex1 The index of the first LocationTagContent to be removed in the MvcController's LtcList
     * @param removedCardIndex2 The index of the second LocationTagContent to be removed in the MvcController's LtcList
     */
    public void removeDone(ListOfCommands l, MVCController c, PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, Request request, int removedCardIndex1, int removedCardIndex2) {
        l.Add(new RemoveRequestCommand(oldPlanningRequest, ltcList, request, removedCardIndex1, removedCardIndex2), c);
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println(l.getI()+" - Calling RemoveRequestCommand from RemoveState to ModifyState");
        }
    }

    /**
     * Not used
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from RemoveState to ModifyState without having changed anything");
        }
    }
}
