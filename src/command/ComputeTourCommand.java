package command;

import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;

public class ComputeTourCommand implements Command {

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    public void setPlanningRequest(PlanningRequest planningRequest) {
        this.planningRequest = planningRequest;
    }

    private Map map;
    private PlanningRequest planningRequest;
    private Tournee tournee;

    public ComputeTourCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand() {
        tournee = ComputeTour.planTour(map, planningRequest);
    }

    @Override
    public void undoCommand() {

    }
}
