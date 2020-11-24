package state;

import command.ComputeTourCommand;
import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.*;
import javafx.stage.Window;
import objects.Map;
import objects.PlanningRequest;

public class RequestState implements State {
    public void loadMap(ListOfCommands l, MVCController c, Window w, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p));
            c.setCurrentState(c.getMapState());
        }
    }
    public void loadRequestPlan(ListOfCommands l, MVCController c, Window w, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p));
            // c.setCurrentState(c.getRequestState());
        }
    }
    public void calculateTour(ListOfCommands l, MVCController c, Window w, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p));
            c.setCurrentState(c.getTourState());
        }
    }
}
