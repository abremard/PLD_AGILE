package state;

import command.ComputeTourCommand;
import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.*;
import objects.Map;
import objects.PlanningRequest;

public class RequestState implements State {

    @Override
    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
        }
    }

    @Override
    public void loadRequestPlan(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p));
            // c.setCurrentState(c.getRequestState());
        }
    }

    @Override
    public void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p));
            c.setCurrentState(c.getTourState());
        }
    }
}
