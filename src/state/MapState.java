package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;

public class MapState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p), c);
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Map Command from MapState to MapState");
            }
        }
    }
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
