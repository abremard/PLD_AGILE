package processing;

import objects.PlanningRequest;
import objects.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * Heuristique de résolution du TSP pickup-delivery (TSPPC) d'après l'article
 * "A heuristic for the pickup and delivery traveling salesman problem" de J.
 * Renaud, F. F. Boctor et J. Ouenniche (2000)
 */
public class PaperHeuristicTSP {

    enum InsertionMethod {CONSECUTIVE, SPLIT}

    /**
     * Classe utilisée pour le retour de la méthode calculant le delta_i
     * pour une requête donnée sur le trajet actuel
     * */
    private class DeltaI {
        public InsertionMethod insertionMethod;
        public float cost;
        public int index1;
        public int index2;

        DeltaI(InsertionMethod insertionMethod, float cost, int index1, int index2) {
            this.insertionMethod = insertionMethod;
            this.cost = cost;
            this.index1 = index1;
            this.index2 = index2;
        }

        DeltaI(InsertionMethod insertionMethod, float cost, int index){
            this.insertionMethod = insertionMethod;
            this.cost = cost;
            this.index1 = index;
            this.index2 = -1;
        }
    }

    static float alpha = 1;     // pour le calcul de delta_i, 0 < alpha < 2
    static int r = 3;           // détermine la longueur des chaînes à utiliser pour 3-opt

    SuperArete[][] matAdj;
    PlanningRequest planning;
    HashMap<Long, Integer> ptsIdToIndex;
    LinkedList<TupleRequete> currentTour;
    LinkedList<Integer> currentTourIndexes;
    ArrayList<Request> requestList;
    int nbRequest;

    PaperHeuristicTSP(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> ptsIdToIndex) {
        this.matAdj = matAdj;
        this.planning = planning;
        this.ptsIdToIndex = ptsIdToIndex;
        this.currentTour = new LinkedList<>();
        this.currentTourIndexes = new LinkedList<>();

        this.requestList = planning.getRequestList();
        this.nbRequest = requestList.size();
    }

    /**
     * Etape 1 de l'algorithme : création d'un premier trajet par insertions
     * successives des requêtes une par une.
     * */
    void doubleInsertionHeuristic() {

        // --------- Etape 1.1 : initialisation
        // Principe : choisir la requête qui maximise la longueur du trajet
        // dépôt -> pickup -> delivery -> dépôt et initialiser le trajet avec.

        // choix du chemin le + long
        float maxCost = 0;
        int maxCostRequestIndex = 0;
        float cost;

        for (int i = 0; i < nbRequest; i++) {
            Request request = requestList.get(i);
            cost = matAdj[0][ptsIdToIndex.get(request.getPickup().getId())].getLongueur()
                    + matAdj[ptsIdToIndex.get(request.getPickup().getId())][ptsIdToIndex.get(request.getDelivery().getId())].getLongueur()
                    + matAdj[ptsIdToIndex.get(request.getDelivery().getId())][0].getLongueur();

            if (cost > maxCost) {
                maxCost = cost;
                maxCostRequestIndex = i;
            }
        }

        // initialisation du trajet
        this.currentTourIndexes.add(0);
        this.currentTourIndexes.add(ptsIdToIndex.get(requestList.get(maxCostRequestIndex).getPickup().getId()));
        this.currentTourIndexes.add(ptsIdToIndex.get(requestList.get(maxCostRequestIndex).getDelivery().getId()));
        this.currentTourIndexes.add(0);

    }

    /**
     * Calcule le 'minimum weighted insertion cost' (delta_i) pour une requête
     * donnée dans l'état actuel du trajet.
     * Utilisée dans les étapes 1.2 et 2.2 de l'algorithme
     *
     * @param request La requête dont on calcule le delta_i
     * @return delta_i, le minimum weighted insertion cost de la meilleure
     * combinaison trouvée, ainsi que sa méthode & sa position d'insertion
     */
    DeltaI minWeightedInsertionCost(Request request) {

        // (i, j) paire de pickup-delivery de la requête en paramètre
        int i_index = ptsIdToIndex.get(request.getPickup().getId());
        int j_index = ptsIdToIndex.get(request.getDelivery().getId());

        // première partie de l'équation : coût min si on insère le pickup
        // et le delivery à la suite
        float minConsecutiveInsertion = 1000000000;
        int consecutiveInsertionIndex = -1;

        // deuxième partie de l'équation : coût min si on insère le delivery
        // non pas directement après le pickup mais plus loin dans le trajet
        float minSplitInsertion = 1000000000;
        int pickupSplitInsertionIndex = -1;
        int deliverySplitInsertionIndex = -1;

        // variable temporaire servant à stocker les résultats intermédiaires
        float cost;
        // variables conservant les indices des k, l, s et t actuels dans
        // la matrice d'adjacence
        int k_index, l_index, s_index, t_index;

        for (int k = 0; k < currentTourIndexes.size() - 1; ++k) {
            k_index = ptsIdToIndex.get(currentTour.get(k).requete.getPickup().getId());
            l_index = ptsIdToIndex.get(currentTour.get(k+1).requete.getPickup().getId());

            // premier min : insertion consécutive
            cost = alpha * matAdj[k_index][i_index].getLongueur()
                    + matAdj[i_index][j_index].getLongueur()
                    + (2 - alpha) * matAdj[j_index][l_index].getLongueur()
                    - matAdj[k_index][l_index].getLongueur();

            // màj du coût min & de la position trouvée
            if (cost < minConsecutiveInsertion) {
                minConsecutiveInsertion = cost;
                consecutiveInsertionIndex = k;
            }

            // deuxième min : insertion séparée
            if (k < currentTour.size() - 2) {
                for (int s = k + 1; s < currentTour.size() - 1; ++k) {
                    s_index = ptsIdToIndex.get(currentTour.get(s).requete.getPickup().getId());
                    t_index = ptsIdToIndex.get(currentTour.get(s+1).requete.getPickup().getId());

                    cost = alpha * (matAdj[k_index][i_index].getLongueur()
                                    + matAdj[i_index][l_index].getLongueur()
                                    - matAdj[k_index][l_index].getLongueur())
                            + (2 - alpha) * (matAdj[s_index][j_index].getLongueur()
                                             + matAdj[j_index][t_index].getLongueur()
                                             - matAdj[s_index][t_index].getLongueur());

                    if (cost < minSplitInsertion) {
                        minSplitInsertion = cost;
                        pickupSplitInsertionIndex = k;
                        deliverySplitInsertionIndex = s;
                    }
                }
            }
        }

        // return le min entre les deux méthodes d'insertion
        if (minConsecutiveInsertion < minSplitInsertion) {
            return new DeltaI(InsertionMethod.CONSECUTIVE, minConsecutiveInsertion, consecutiveInsertionIndex);
        } else {
            return new DeltaI(InsertionMethod.SPLIT, minSplitInsertion, pickupSplitInsertionIndex, deliverySplitInsertionIndex);
        }
    }

}
