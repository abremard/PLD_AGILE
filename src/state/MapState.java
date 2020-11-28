package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;

public class MapState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.print("Adding Load Map Command from MapState to index ");
                System.out.println(l.getI());
            }
        }
    }
    public void loadRequestPlan(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p));
            c.setCurrentState(c.getRequestState());
            if (debug) {
                System.out.print("Adding Load Request Command from MapState to index ");
                System.out.println(l.getI());
            }
        }
    }
}
