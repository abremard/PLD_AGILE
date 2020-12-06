package state;

import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;

public class MapState implements State {
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            LoadMapCommand loadMapCommand = (LoadMapCommand) l.getL().get(l.getI());
            c.setMap(loadMapCommand.getMap());
            c.setCurrentState(c.getMapState());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Load Map Command from MapState to index ");
            }
        }
    }
    public void loadRequestPlan(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p));
            LoadRequestPlanCommand loadRequestPlanCommand = (LoadRequestPlanCommand) l.getL().get(l.getI());
            c.setPlanningRequest(loadRequestPlanCommand.getPlanningRequest());
            c.setCurrentState(c.getRequestState());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Load Request Command from MapState to index ");
            }
        }
    }
}
