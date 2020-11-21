package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import controller.Controller;
import javafx.stage.Window;

public class InitialState implements State {
    public void loadMap(ListOfCommands l, Controller c, Window w, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
        }
    }
}
