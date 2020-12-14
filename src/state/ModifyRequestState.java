package state;

import command.EditRequestCommand;
import command.ListOfCommands;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

/**
 * <h1>Modify Request State Class</h1>
 * <p>The Map State object that defines the state where user is when he/she wants to modify a selected request</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class ModifyRequestState implements State {
    /**
     * When user modified a request and click on "Done" in the View, EditRequestCommand is added and executed and state becomes ModifyState
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param oldRequest old request object, used for undo command
     * @param newRequest new request object that will replace the old request
     * @param editedRequestIndex index of the edited request in the planning request object
     * @param editedCardIndex index of the edited Location Tag Content object in the list
     * @param oldDuration old pickup/delivery duration, used for undo command
     * @param newDuration new pickup/delivery duration that will replace the old one
     * @param isPickup whether the edited Location Tag Content corresponds to a pickup or a delivery
     * */
    public void modifyRequestDone(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest, int editedRequestIndex, int editedCardIndex, double oldDuration, double newDuration, boolean isPickup) {
        l.Add(new EditRequestCommand(oldRequest, newRequest, editedRequestIndex, editedCardIndex, oldDuration, newDuration, isPickup), c);
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println(l.getI()+" - Confirming modification of a request of the request list ");
        }
    }

    /**
     * When user clicks on "Cancel" on the View after having clicked on edit card pencil logo. If the user made changes, they will be discarded
     * @param c the MVCController object whose state will be changed without any effect
     */
    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from ModifyRequestState to ModifyState without having changed anything");
        }
    }
}
