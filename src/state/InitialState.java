package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;

public class InitialState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.print("Adding Load Map Command from Initial State to index ");
                System.out.println(l.getI());
            }
        }
    }
}
