package command;

import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

public class ComputeTourCommand implements Command {

    /** classe attributes **/
    private Map map;
    private PlanningRequest planningRequest;
    private Tournee tournee;

    /** getters & setters **/
    public Map getMap() {
        return map;
    }
    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }
    public Tournee getTournee() {
        return tournee;
    }

    /** constructor **/
    public ComputeTourCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand() {
        tournee = ComputeTour.planTour(map, planningRequest, Heuristique.BRANCHANDBOUND);
    }

    @Override
    public void undoCommand() {}
}
