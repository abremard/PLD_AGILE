package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;

public class InitialState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            LoadMapCommand loadMapCommand = (LoadMapCommand) l.getL().get(l.getI());
            c.setMap(loadMapCommand.getMap());
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Load Map Command from Initial State to index");
                System.out.println(l.getI());
            }
        }
    }
}
