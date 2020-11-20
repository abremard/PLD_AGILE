package controller;

import objects.Map;
import objects.PlanningRequest;

public class ComputeTourneeCommand implements Command {

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

    public ComputeTourneeCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand() {
        // compute here...
    }

    @Override
    public void undoCommand() {

    }
}
