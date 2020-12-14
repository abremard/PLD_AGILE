package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;

/**
 * <h1>Initial State Class</h1>
 * <p>The Initial State object that defines the state where user is at the very beginning of the application</p>
 *
 * @author H4302
 * @see State
 * @see ListOfCommands
 */
public class InitialState implements State {
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
                System.out.println(l.getI()+" - Adding Load Map Command from Initial State to Map State");
            }
        }
    }
}
