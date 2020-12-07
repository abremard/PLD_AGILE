package state;

import command.ComputeTourCommand;
import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;

public class RequestState implements State {

    public void loadMap(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadMapCommand(p), c);
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Map Command from RequestState");
            }
        }
    }

    public void loadRequestPlan(ListOfCommands l, MVCController c, String p) {
        if (p != null) {
            l.Add(new LoadRequestPlanCommand(p), c);
            if (debug) {
                System.out.println(l.getI()+" - Adding Load Request Command from RequestState");
            }
        }
    }

    public void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p), c);
            c.setCurrentState(c.getTourState());
            if (debug) {
                System.out.println(l.getI()+" - Adding Calculate Tour Command from RequestState to TourState");
            }
        }
    }
}
