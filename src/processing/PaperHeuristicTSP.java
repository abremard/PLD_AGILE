package processing;

import objects.PlanningRequest;
import objects.Request;

import java.util.HashMap;
import java.util.LinkedList;


/**
 * Heuristique de résolution du TSP pickup-delivery (TSPPC) d'après l'article
 * "A heuristic for the pickup and delivery traveling salesman problem" de J.
 * Renaud, F. F. Boctor et J. Ouenniche (2000)
 */
public class PaperHeuristicTSP {

    static float alpha = 1;     // paramètre de l'heuristique pour le calcul de delta_i, 0 < alpha < 2
    static int r = 3;           // paramètre de l'heuristique

    SuperArete[][] matAdj;
    PlanningRequest planning;
    HashMap<Long, Integer> ptsIdToIndex;
    LinkedList<TupleRequete> currentTour;

    PaperHeuristicTSP(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> ptsIdToIndex) {
        this.matAdj = matAdj;
        this.planning = planning;
        this.ptsIdToIndex = ptsIdToIndex;
        this.currentTour = new LinkedList<>();
    }

    /**
     * Calcule le 'minimum weighted insertion cost' (delta_i) pour une requête
     * donnée dans l'état actuel du trajet.
     *
     * @param request La requête dont on calcule le delta_i
     * @return delta_i, le minimum weighted insertion cost attendu
     */
    float minWeightedInsertionCost(Request request) {
        float res = 0;

        float minConsecutiveInsertion = 1000000000;

        return res;
    }
}
