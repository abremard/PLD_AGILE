package command;

import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;
import processing.SuperArete;

import java.util.ArrayList;

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
        // mesure du temps d'exécution de la fonction de calcul du chemin
        long startTime = System.nanoTime();
        tournee = ComputeTour.planTour(map, planningRequest, Heuristique.RANDOM);
        long stopTime = System.nanoTime();
        System.err.println("--- Executed method 'planTour' in " + (double)(stopTime - startTime)/1000.0 + " μs");
    }

    @Override
    public void undoCommand() { tournee = null; }
}
