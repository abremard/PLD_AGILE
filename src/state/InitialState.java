package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;

public class InitialState implements State {
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
