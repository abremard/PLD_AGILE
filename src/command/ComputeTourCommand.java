package command;

import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;

public class ComputeTourCommand implements Command {

    private Map map;
    private PlanningRequest planningRequest;
    private Tournee tournee;

    public Map getMap() {
        return map;
    }

    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    public Tournee getTournee() {
        return tournee;
    }



    public void setPlanningRequest(PlanningRequest planningRequest) {
        this.planningRequest = planningRequest;
    }

    public ComputeTourCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand() {
        tournee = ComputeTour.planTour(map, planningRequest);
    }

    @Override
    public void undoCommand() { tournee = null; }
}
