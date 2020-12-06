package processing;

import objects.PlanningRequest;
import objects.Request;
import objects.Segment;
import objects.Tournee;

import java.util.*;


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
     */
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

        DeltaI(InsertionMethod insertionMethod, float cost, int index) {
            this.insertionMethod = insertionMethod;
            this.cost = cost;
            this.index1 = index;
            this.index2 = -1;
        }
    }

    static float alpha = 1;     // pour le calcul de delta_i, 0 < alpha < 2
    static int r = 3;           // détermine la longueur des chaînes à utiliser pour 3-opt

    int nbRequest;
    SuperArete[][] matAdj;
    PlanningRequest planning;
    HashMap<Long, Integer> ptsIdToIndex;
    LinkedList<Integer> currentTourIndexes;
    LinkedList<TupleRequete> currentTourPoints;     // stocke les indices des points dans la tournée
    ArrayList<Request> requestList;     // pour stocker les requêtes associées aux points qu'on a choisi
    // !!! le premier & le dernier élément d'un trajet complet sont des null car
    // pas de requête depuis/vers le dépôt !

    PaperHeuristicTSP(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> ptsIdToIndex) {
        this.matAdj = matAdj;
        this.planning = planning;
        this.ptsIdToIndex = ptsIdToIndex;
        this.currentTourIndexes = new LinkedList<>();
        this.currentTourPoints = new LinkedList<>();

        this.requestList = planning.getRequestList();
        this.nbRequest = requestList.size();
    }

    /**
     * Etape 1 de l'algorithme : création d'un premier trajet par insertions
     * successives des requêtes une par une.
     */
    void doubleInsertionHeuristic() {

        // --------- Etape 1.1 : initialisation
        // Principe : choisir la requête qui maximise la longueur du trajet
        // dépôt -> pickup -> delivery -> dépôt et initialiser le trajet avec.

        // choix du chemin le + long
        float maxCost = 0;
        int maxCostRequestIndex = 0;
        float cost;

        // initialisation des requêtes à ajouter dans le trajet final
        Set<Request> requestsToProcess = new HashSet<>();
        requestsToProcess.addAll(requestList);

        for (int i = 0; i < nbRequest; i++) {
            Request request = requestList.get(i);
            cost = longueurEntre(0, ptsIdToIndex.get(request.getPickup().getId()))
                    + longueurEntre(ptsIdToIndex.get(request.getPickup().getId()), ptsIdToIndex.get(request.getDelivery().getId()))
                    + longueurEntre(ptsIdToIndex.get(request.getDelivery().getId()), 0);

            if (cost > maxCost) {
                maxCost = cost;
                maxCostRequestIndex = i;
            }
        }

        // initialisation du trajet
        ajouterPointTournee(null);
        ajouterPointTournee(new TupleRequete(requestList.get(maxCostRequestIndex), true));
        ajouterPointTournee(new TupleRequete(requestList.get(maxCostRequestIndex), false));
        ajouterPointTournee(null);

        // on enlève la requête qu'on vient de process de la liste des requêtes à ajouter
        requestsToProcess.remove(requestList.get(maxCostRequestIndex));

        // debug : chemin actuel
        System.err.println("Initial tour indexes: " + this.currentTourIndexes);

        DeltaI currDelta;
        while (requestsToProcess.size() > 0) {

            // --------- Etape 1.2 : calcul des delta_i
            DeltaI minDeltaI = new DeltaI(InsertionMethod.CONSECUTIVE, 10000000, 0);
            Request bestChoice = null;

            for (Request possibleRequest : requestsToProcess) {
                currDelta = minWeightedInsertionCost(possibleRequest);
                if (currDelta.cost < minDeltaI.cost) {
                    bestChoice = possibleRequest;
                    minDeltaI = currDelta;
                }
            }

            // --------- Etape 1.3 : insertion de la requête avec le delta_i min
            // ajout du point de delivery
            if (minDeltaI.insertionMethod == InsertionMethod.CONSECUTIVE) {
                ajouterPointTournee(new TupleRequete(bestChoice, false), minDeltaI.index1);
            } else {
                ajouterPointTournee(new TupleRequete(bestChoice, false), minDeltaI.index2);
            }
            // ajout du point de pickup après (pour utiliser le même indice)
            ajouterPointTournee(new TupleRequete(bestChoice, true), minDeltaI.index1);

            // fin du traitement de cette requête
//            System.err.println("Removing request " + bestChoice);
            requestsToProcess.remove(bestChoice);
//            System.err.println("Requests to process: " + requestsToProcess);
            System.err.println("Current tour indexes: " + currentTourIndexes);
        }

        // --------- Etape 1.4 : Optimisation locale (3-opt)
        // TODO 3-opt
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
            k_index = currentTourIndexes.get(k);
            l_index = currentTourIndexes.get(k + 1);

            // premier min : insertion consécutive
//            System.err.println("i: " + i_index + ", j: " + j_index + ", k: " + k_index + ", l: " + l_index);       // debug
            cost = alpha * longueurEntre(k_index, i_index)
                    + longueurEntre(i_index, j_index)
                    + (2 - alpha) * longueurEntre(j_index, l_index)
                    - longueurEntre(k_index, l_index);

            // màj du coût min & de la position trouvée
            if (cost < minConsecutiveInsertion) {
                minConsecutiveInsertion = cost;
                consecutiveInsertionIndex = k + 1;
            }

            // deuxième min : insertion séparée
            if (k < currentTourIndexes.size() - 3) {
                for (int s = k + 1; s < currentTourIndexes.size() - 1; ++s) {
                    s_index = currentTourIndexes.get(s);
                    t_index = currentTourIndexes.get(s + 1);
//                    System.err.println("i: " + i_index + ", j: " + j_index + ", k: " + k_index + ", l: " + l_index + ", s: " + s_index + ", t: " + t_index);       // debug

                    cost = alpha * (longueurEntre(k_index, i_index)
                            + longueurEntre(i_index, l_index)
                            - longueurEntre(k_index, l_index))
                            + (2 - alpha) * (longueurEntre(s_index, j_index)
                            + longueurEntre(j_index, t_index)
                            - longueurEntre(s_index, t_index));

                    if (cost < minSplitInsertion) {
                        minSplitInsertion = cost;
                        pickupSplitInsertionIndex = k + 1;
                        deliverySplitInsertionIndex = s + 1;
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

    /**
     * Construit un objet Tournee utilisable par l'IHM à partir du trajet actuel
     *
     * @return L'objet complet et utilisable
     */
    Tournee buildTour() {

        // initialisation des éléments à passer au constructeur de Tournee
        ArrayList<Request> requestList = planning.getRequestList();
        ArrayList<TupleRequete> ptsPassage = new ArrayList<>(this.currentTourPoints);

        // initialisation de la liste des segments avec le chemin dépôt -> premier pickup
        ArrayList<Segment> segmentList = new ArrayList<>(matAdj[0][currentTourIndexes.get(1)].chemin);

        // remplissage du chemin avec la suite de la tournée
        for (int i = 1; i < ptsPassage.size() - 1; ++i) {
            TupleRequete pt1 = ptsPassage.get(i);
            TupleRequete pt2 = ptsPassage.get(i + 1);

            if (pt2 != null) {
                segmentList.addAll(
                        matAdj[ptsIdToIndex.get(pt1.getCurrentGoal().getId())][ptsIdToIndex.get(pt2.getCurrentGoal().getId())].chemin);
            } else {        // dernier delivery -> dépôt
                segmentList.addAll(
                        matAdj[ptsIdToIndex.get(pt1.getCurrentGoal().getId())][0].chemin);
            }
        }

        // ajout des heures d'arrivée à chaque point de passage + le chemin qui y mène
        Tournee tournee = new Tournee(segmentList, requestList);
        ComputeTour.recreateTimesTournee(tournee, planning);
        return tournee;
    }

    /**
     * Renvoie la longueur du plus court chemin entre 2 points d'intérêt selon
     * la matrice d'adjacence
     *
     * @param depart  Le point de départ du chemin
     * @param arrivee Le point d'arrivée du chemin
     * @return La longueur du plus court chemin entre depart et arrivee
     */
    private float longueurEntre(int depart, int arrivee) {
        if (matAdj[depart][arrivee] != null) {
            return matAdj[depart][arrivee].getLongueur();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "PaperHeuristicTSP{" +
                "currentTourIndexes=" + currentTourIndexes +
                ", nbRequest=" + nbRequest +
                '}';
    }

    /**
     * Ajoute un point de passage à la tournée actuelle
     *
     * @param index        l'index que le point aura après insertion dans les différentes listes
     * @param tupleRequete le point de passage à insérer
     */
    void ajouterPointTournee(TupleRequete tupleRequete, int index) {
        if (tupleRequete != null) {
            currentTourIndexes.add(index, ptsIdToIndex.get(tupleRequete.getCurrentGoal().getId()));
        } else {
            currentTourIndexes.add(0);
        }
        currentTourPoints.add(index, tupleRequete);
    }

    /**
     * Surcharge pour ajouter un point directement à la fin du trajet actuel
     */
    void ajouterPointTournee(TupleRequete tupleRequete) {
        ajouterPointTournee(tupleRequete, currentTourIndexes.size());
    }

    // todo supprimerPointTourner !
}
