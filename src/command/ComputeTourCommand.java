package command;

import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;

/**
 * Commande qui nous permet de calculer la tournée optimale.
 */
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
        // mesure du temps d'exécution de la fonction de calcul du chemin
        long startTime = System.nanoTime();
//        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.BRANCHANDBOUND));
//        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.GREEDY));
        c.setTour(ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION));      // THE BEST <3
        long stopTime = System.nanoTime();
        System.err.println(">>> Executed method 'planTour' in " + (double)(stopTime - startTime)/1000.0 + " μs");
    }

    @Override
    public void undoCommand(MVCController c) {}
}
