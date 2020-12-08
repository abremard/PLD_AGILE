package command;

// TODO : if same as compute tour command then delete this class...

import controller.MVCController;
import objects.Intersection;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

import java.util.ArrayList;

/**
 * Commande qui nous permet de valider les modifications de la tournée.
 */
public class ApplyModificationCommand implements Command {

    private Map map;
    private PlanningRequest planningRequest;
    private ArrayList<Intersection> order;

    public ApplyModificationCommand(Map m, PlanningRequest p, ArrayList<Intersection> order) {
        this.map = m;
        this.planningRequest = p;
        this.order = order;
    }

    @Override
    public void doCommand(MVCController c) {
        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.GREEDY));
        // c.setTour(ComputeTour.recreateTourneeWithOrder(map, planningRequest, order)); // Uncomment when ready
    }

    @Override
    public void undoCommand(MVCController c) {}
}
