package state;

import command.*;
import controller.MVCController;
import objects.Intersection;
import objects.PlanningRequest;
import objects.Map;
import processing.ComputeTour;
import processing.Heuristique;
import sample.Controller;

import java.util.ArrayList;

/**
 * <h1>Modify State Class</h1>
 * <p>The Modify State object that defines the state where user can modify a tour request</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class ModifyState implements State {

    /**
     * When the user clicks on Add Request, the state of the mvcController becomes AddState
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void addRequest(MVCController c){
        c.setCurrentState(c.getAddState());
        if (debug) {
            System.out.println("Going from ModifyState to AddState");
        }
    }

    /**
     * When the user clicks on a can trash on the IHM, the state of the mvcController becomes RemoveState
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void removeRequest(MVCController c){
        c.setCurrentState(c.getRemoveState());
        if (debug) {
            System.out.println("Going from ModifyState to RemoveState");
        }
    }

    /**
     * When the user clicks on a pen on the IHM, the state of the mvcController becomes ModifyRequestState
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void modifyRequest(MVCController c){
        c.setCurrentState(c.getModifyRequestState());
        if (debug) {
            System.out.println("Going from ModifyState to ModifyRequestState");
        }
    }

    /**
     * When the user clicks on an arrow on the IHM, the Swap Order Command is added to the history of commands (thus, executed)
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param a The index of the first card to swap
     * @param b The index of the second card to swap
     * @param ltcList A list of cards of pickup/delivery in which 2 cards will be swaped
     */
    public void swapRequest(ListOfCommands l, MVCController c, int a, int b, ArrayList<Controller.LocationTagContent> ltcList){
        l.Add(new SwapOrderCommand(a, b, ltcList), c);
        if (debug) {
            System.out.println(l.getI()+" Calling Swap Order Command from ModifyState");
        }
    }

    /**
     * When the user is done with the Tour's modifications, he can click on "Done". The Apply Modification Command is added to the history of commands (thus, executed) and current state becomes Tour State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param m The map chosen by the user on which all the pickups and deliveries are made
     * @param p The requests' planning to be satisfied
     * @param ltcList A list of cards of pickup/delivery
     */
    public void applyModificationDone(ListOfCommands l, MVCController c, Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList) {
        l.Add(new ApplyModificationCommand(m, p, ltcList), c);
        c.setCurrentState(c.getTourState());
        if (debug) {
            System.out.println(l.getI()+" - Adding ApplyModificationCommand from ModifyState to TourState");
        }
    }

}
