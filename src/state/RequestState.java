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
            l.Add(new LoadMapCommand(p));
            LoadMapCommand loadMapCommand = (LoadMapCommand) l.getL().get(l.getI());
            c.setMap(loadMapCommand.getMap());
            c.setCurrentState(c.getRequestState());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Load Map Command from RequestState to index ");
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
                System.out.println(" - Adding Load Request Command from RequestState to index ");
            }
        }
    }

    public void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p));
            ComputeTourCommand computeTourCommand = (ComputeTourCommand) l.getL().get(l.getI());
            c.setTour(computeTourCommand.getTournee());
            c.setCurrentState(c.getTourState());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Calculate Tour Command from RequestState to index ");
            }
        }
    }
}
