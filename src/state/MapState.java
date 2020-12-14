package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;

/**
 * <h1>Map State Class</h1>
 * <p>The Map State object that defines the state where user is after successfully loaded a map</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class MapState implements State {
    /**
     * When user picks a map file in the View, the LoadMapCommand is added and executed, state becomes Map State
     * @param l the history of commands in which will be added the new command to execute
     * @param c the MVCController object being passed to the command execution for updating purposes
     * @param p the path of the map file
     */
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p), c);
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Map Command from MapState to MapState");
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
            c.setCurrentState(c.getRequestState());
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Request Command from MapState to RequestState");
            }
        }
    }
}
