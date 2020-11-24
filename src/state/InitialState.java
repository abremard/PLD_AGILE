package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;
import javafx.stage.Window;

public class InitialState implements State {
    public void loadMap(ListOfCommands l, MVCController c, Window w, String p) {
        if (p != null) {
            // w.displayMessage("Loading Map")
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
        }
    }
}
