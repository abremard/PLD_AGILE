package state;

import command.ComputeTourCommand;
import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;

/**
 * <h1>Request State Class</h1>
 * <p>The Request State object that defines the state where user is after successfully loaded a planning request</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class RequestState implements State {

    /**
     * When user picks a map file in the View, the LoadMapCommand is added and executed, state becomes Map State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param p the path of the map file
     */
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p), c);
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Map Command from RequestState");
            }
        }
    }

    /**
     * When user picks a planning request file in the View, the LoadRequestPlanCommand is added and executed, state becomes Request State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param p the path of the planning request file
     */
    public void loadRequestPlan(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p), c);
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Request Command from RequestState");
            }
        }
    }

    /**
     * When user clicks on "Calculate Tour", the ComputeTourCommand is added and executed, state becomes Tour State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param p The requests' planning to be satisfied
     * @param m The map chosen by the user on which all the pickups and deliveries are made
     */
    public void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p), c);
            c.setCurrentState(c.getTourState());
            if (debug) {
                System.out.println(l.getI()+" - Adding Calculate Tour Command from RequestState to TourState");
            }
        }
    }
}
