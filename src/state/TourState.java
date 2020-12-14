package state;

import command.SwapOrderCommand;
import controller.MVCController;
import command.ListOfCommands;
import command.NewTourCommand;
import objects.PlanningRequest;

/**
 * <h1>Tour State Class</h1>
 * <p>The Tour State object that defines the state where user is after successfully calculated a tour</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class TourState implements State {

    /**
     * When user clicks on "New Tour", the NewTourCommand is added and executed, state becomes Request State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void newTour(ListOfCommands l, MVCController c) {
        l.Add(new NewTourCommand(), c);
        c.setCurrentState(c.getRequestState());
        if (debug) {
            System.out.println(l.getI()+" - Adding New Tour Command from TourState to RequestState");
        }
    }

    /**
     * When user clicks on "Modify", state becomes Modify State
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void modifyRequestList(MVCController c) {
        c.setCurrentState(c.getModifyState());
        c.getL().setLowerBound(c.getL().getI()+1);
        if (debug) {
            System.out.println("Going from TourState to ModifyState");
            System.out.println("Lower bound is set to "+(c.getL().getI()+1));
        }
    }

}
