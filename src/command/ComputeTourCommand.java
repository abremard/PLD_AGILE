package command;

import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

/**
 * <h1>Compute Tour Command Class</h1>
 * <p>The Compute Tour Command defines the command to be called when user desires to calculate a Tournee given a map and planning request</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class ComputeTourCommand implements Command {

    /**
     * Map object used for tour computation
     */
    private Map map;
    /**
     * Planning Request object used for tour computation
     */
    private PlanningRequest planningRequest;

    /**
     * Constructor
     * @param m the Map object
     * @param p the Planning Request object
     */
    public ComputeTourCommand(Map m, PlanningRequest p) {
        this.map = m;
        this.planningRequest = p;
    }

    /**
     * Execute command : compute tour using a map and a planning request, heuristic is chosen by user and execution time is mesured
     * @param c the MVCController pointer used to update tournee
     */
    @Override
    public void doCommand(MVCController c) {
        //  mesure du temps d'exécution de la fonction de calcul du chemin
        long startTime = System.nanoTime();
        //  c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.BRANCHANDBOUND));
        //  c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.GREEDY));
        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION));      // THE BEST <3
        long stopTime = System.nanoTime();
        System.err.println(">>> Executed method 'planTour' in " + (double)(stopTime - startTime)/1000.0 + " μs");
    }

    @Override
    public void undoCommand(MVCController c) {}
}
