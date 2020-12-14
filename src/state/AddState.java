package state;

import command.ListOfCommands;
import command.AddRequestCommand;
import command.SwapOrderCommand;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import processing.ComputeTour;
import processing.Heuristique;
import sample.Controller;

import java.util.ArrayList;


/**
 * <h1>Add State Class</h1>
 * <p>The Add State object that defines the state where user can add a new request</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class AddState implements State {
    /**
     * When user clicks on "Done" on the View after adding a new request, the Add Request Command is added to the history of commands (thus, executed) and current state becomes Modify State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param newRequest the new Request object that will be added to the Planning Request
     * @param newPickupLtc the new pickup Location Tag Content object that will be added to the list of LTCs
     * @param newDeliveryLtc the new delivery Location Tag Content object that will be added to the list of LTCs
     */
    public void addDone(ListOfCommands l, MVCController c, Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc) {
        l.Add(new AddRequestCommand(newRequest, newPickupLtc, newDeliveryLtc), c);
        c.setCurrentState(c.getModifyState());

        if (debug) {
            System.out.println(l.getI()+" - Confirming addition of a new request to the request list ");
        }
    }

    /**
     * When user clicks on "Cancel" on the View after having clicked on "New Request". If the user added a request, it will be ignored
     * @param c the MVCController object whose state will be changed without any effect
     */
    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from AddState to ModifyState without having changed anything");
        }
    }
}
