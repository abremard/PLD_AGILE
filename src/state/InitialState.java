package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.MVCController;
import javafx.stage.Window;

public class InitialState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
        }
    }
}
