package state;

import controller.Controller;
import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import javafx.stage.Window;

public class MapState implements State {
    public void loadMap(ListOfCommands l, Controller c, Window w, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            // c.setCurrentState(c.getMapState());
        }
    }
    public void loadRequestPlan(ListOfCommands l, Controller c, Window w, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p));
            c.setCurrentState(c.getRequestState());
        }
    }
}
