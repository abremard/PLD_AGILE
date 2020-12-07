package command;

import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

public class ComputeTourCommand implements Command {

    /** classe attributes **/
    private Map map;
    private PlanningRequest planningRequest;

    /** getters & setters **/
    public Map getMap() {
        return map;
    }
    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    /** constructor **/
    public ComputeTourCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    @Override
    public void doCommand(MVCController c) {
        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.GREEDY));
        // tournee = ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION);      // EXPERIMENTAL
    }

    @Override
    public void undoCommand(MVCController c) {}
}
