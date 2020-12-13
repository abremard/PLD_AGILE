package processing;

import objects.PlanningRequest;
import objects.Request;
import objects.Segment;
import objects.Tournee;

import java.util.*;

import static java.lang.StrictMath.*;


/**
 * Heuristic-based pickup-delivery traveling salesman problem (TSPPC) solution
 * adapted from the paper "A heuristic for the pickup and delivery traveling
 * salesman problem" of J. Renaud, F. F. Boctor and J. Ouenniche (2000).
 *
 * Only the first part is implemented here, and in the case of our application,
 * it does not seem relevant to try to optimize the solution computed any
 * further.
 */
public class DoubleInsertionTSP {

    enum InsertionMethod {CONSECUTIVE, SPLIT}

    /**
     * Ways of assembling a given path A-B-C-D to which we removed the 3 edges
     * A-B, B-C and C-D, these 4 letters each representing a portion of this
     * path, made up of one or more vertex(es), without considering cases that
     * can be reduced as 2-opt cases (when an edge that has been removed is
     * added back at the same place and in the same direction as before).
     *
     * - DOUBLE_REVERSE : A - B reversed - C reversed - D
     * - INVERT-ORDER : A - C - B - D
     * - REVERSE_B : B - C - B reversed - D
     * - REVERSE_C : A - C reversed - B - D
     * - INVERT_REVERSE : A - C reversed - C reversed - D
     */
    enum AssembleOrder {DOUBLE_REVERSE, INVERT_ORDER, REVERSE_B, REVERSE_C, INVERT_REVERSE}

    /**
     * Class used as the return of the method computing the delta_i of a given
     * request on the current path.
     *
     * @see DoubleInsertionTSP#minWeightedInsertionCost(Request)
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

    /**
     * Class used to represent a 3-integers tuple corresponding to the 3 cut
     * points in the sub-path used in 3-opt optimization.
     *
     * @see DoubleInsertionTSP#allPossibleCuts(int)
     */
    private class ThreeOptCuts {

        public int cut1;
        public int cut2;
        public int cut3;

        ThreeOptCuts(int cut1, int cut2, int cut3) {
            this.cut1 = cut1;
            this.cut2 = cut2;
            this.cut3 = cut3;
        }

        @Override
        public String toString() {
            return "ThreeOptCuts{" + cut1 + ", " + cut2 + ", " + cut3 + '}';
        }
    }

    static float alpha = 1;     // pour le calcul de delta_i, 0 < alpha < 2
    static int r = 3;           // détermine la longueur des chaînes à utiliser pour 3-opt
    boolean verbose = false;    // (dés)active les prints sur stderr

    int nbRequest;
    SuperArete[][] matAdj;
    PlanningRequest planning;
    HashMap<Long, Integer> ptsIdToIndex;
    LinkedList<Integer> currentTourIndexes;
    LinkedList<TupleRequete> currentTourPoints;     // stocke les indices des points dans la tournée
    ArrayList<Request> requestList;     // pour stocker les requêtes associées aux points qu'on a choisi
    // !!! le premier & le dernier élément d'un trajet complet sont des null car
    // pas de requête depuis/vers le dépôt !

    DoubleInsertionTSP(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> ptsIdToIndex) {
        this.matAdj = matAdj;
        this.planning = planning;
        this.ptsIdToIndex = ptsIdToIndex;
        this.currentTourIndexes = new LinkedList<>();
        this.currentTourPoints = new LinkedList<>();

        this.requestList = planning.getRequestList();
        this.nbRequest = requestList.size();
    }

    // --------------------------- Algorithm core methods

    /**
     * First step of the algorithm : Iteratively building an initial tour by
     * inserting one by one each request and applying a local optimization
     * after each insertion.
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

        if (verbose)
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
            if (verbose)
                System.err.println("Current tour indexes: " + currentTourIndexes);


            // --------- Etape 1.4 : Optimisation locale (3-opt)
            if (currentTourIndexes.size() > 4) {        // 3-opt uniquement si + d'1 requête dans le trajet

                // stocker les deux centres des chemins sur lesquels on applique le 3-opt
                ArrayList<Integer> centers = new ArrayList<>();
                // point de pickup
                centers.add(minDeltaI.index1);
                // delivery
                if (minDeltaI.insertionMethod == InsertionMethod.CONSECUTIVE) {
                    centers.add(minDeltaI.index1 + 1);
                } else {
                    centers.add(minDeltaI.index2 + 1);  // delivery (+1 car décalé après insertion du pickup)
                }

                float initialCost;
                float newCost;

                for (int center : centers) {        // 3-opt autour du pickup et du delivery

                    // combinaisons possibles pour les "coupures" d'arêtes sur le chemin
                    ArrayList<ThreeOptCuts> possibleCuts = allPossibleCuts(minDeltaI.index1);
                    boolean stop = false;

                    // pour chaque possibilité de 3-opt, évaluer son coût, et la garder si meilleure que l'originale
                    for (ThreeOptCuts cuts : possibleCuts) {
                        initialCost = longueurCheminEntre(cuts.cut1, cuts.cut3 + 1);

                        for (AssembleOrder order : AssembleOrder.values()) {
                            newCost = threeOptCost(cuts.cut1, cuts.cut2, cuts.cut3, order);

                            // si on trouve un meilleur trajet et qu'il respecte les contraintes de précédence, on l'applique
                            if (newCost < initialCost) {
                                // on applique l'optimisation si elle est valide
                                if (applyThreeOptIfValid(cuts.cut1, cuts.cut2, cuts.cut3, order)) {
                                    if (verbose)
                                        System.err.println("Optimized path from cost " + initialCost + " to " + newCost);
                                    stop = true;        // propage le break à la boucle du dessus
                                    break;
                                }
                            }
                        }

                        // ne plus considérer les autres possibilités de cuts si on a déjà amélioré le chemin
                        if (stop) {
                            break;
                        }
                    }
                }
            }   // fin 3-opt
        }
    }

    /**
     * Computes the 'minimum weighted insertion cost' (delta_i) for a given
     * request given the current state of the tour.
     * Used in parts 1.2 and 2.2 of the main algorithm.
     *
     * @param request The request for which we compute delta_i
     * @return delta_i, the minimum weighted insertion cost of the best
     * combination found, as well as the position(s) and method of insertion
     * @see DeltaI
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
     * Compute the cost of the given 3-opt permutation according to the given
     * method of assembling.
     * This cost corresponds to the length of the sub-path considered after
     * being reordered according to the given method, that is to say the length
     * of the path starting at index cut1 and ending at index cu3 + 1 included.
     * This sub-path is made up of 2r+1 vertices (points) and the edges
     * connecting them.
     *
     * @param cut1  The index in the current tour of the point from which we
     *              'cut' the first edge.
     * @param cut2  The index in the current tour of the point from which we
     *              'cut' the second edge.
     * @param cut3  The index in the current tour of the point from which we
     *              'cut' the third edge.
     * @param order The order according to which we reorder the sub-paths
     *              obtained after cutting the first sub-path.
     * @see InsertionMethod
     * @see DoubleInsertionTSP#doubleInsertionHeuristic()
     */
    public float threeOptCost(int cut1, int cut2, int cut3, AssembleOrder order) {

        float cost = 0;

        /*
         * Ordres possibles :
         * - DOUBLE_REVERSE : A - B à l'envers - C à l'envers - D
         * - INVERT-ORDER : A - C - B - D
         * - REVERSE_B : A - C - B à l'envers - D
         * - REVERSE_C : A - C à l'envers - B - D
         * - INVERT_REVERSE : A - C à l'envers - B à l'envers - D
         * */
        switch (order) {
            case DOUBLE_REVERSE:        // A - B à l'envers - C à l'envers - D
                // fin de A -> fin de B
                cost += longueurEntre(currentTourIndexes.get(cut1), currentTourIndexes.get(cut2));
                // B à l'envers (fin -> début)
                cost += longueurCheminEntre(cut2, cut1 + 1);
                // début de B -> fin de C
                cost += longueurEntre(currentTourIndexes.get(cut1 + 1), currentTourIndexes.get(cut3));
                // C à l'envers (fin -> début)
                cost += longueurCheminEntre(cut3, cut2 + 1);
                // début de C -> début de D
                cost += longueurEntre(currentTourIndexes.get(cut2 + 1), currentTourIndexes.get(cut3 + 1));
                break;

            case INVERT_ORDER:          // A - C - B - D
                // fin de A -> début de C
                cost += longueurEntre(currentTourIndexes.get(cut1), currentTourIndexes.get(cut2 + 1));
                // C à l'endroit (début -> fin)
                cost += longueurCheminEntre(cut2 + 1, cut3);
                // fin de C -> début de B
                cost += longueurEntre(currentTourIndexes.get(cut3), currentTourIndexes.get(cut1 + 1));
                // B à l'endroit (début -> fin)
                cost += longueurCheminEntre(cut1 + 1, cut2);
                // fin de B -> début de D
                cost += longueurEntre(currentTourIndexes.get(cut2), currentTourIndexes.get(cut3 + 1));
                break;

            case REVERSE_B:          // A - C - B à l'envers - D
                // fin de A -> début de C
                cost += longueurEntre(currentTourIndexes.get(cut1), currentTourIndexes.get(cut2 + 1));
                // C à l'endroit (début -> fin)
                cost += longueurCheminEntre(cut2 + 1, cut3);
                // fin de C -> fin de B
                cost += longueurEntre(currentTourIndexes.get(cut3), currentTourIndexes.get(cut2));
                // B à l'envers (fin -> début)
                cost += longueurCheminEntre(cut2, cut1 + 1);
                // début de B -> début de D
                cost += longueurEntre(currentTourIndexes.get(cut1 + 1), currentTourIndexes.get(cut3 + 1));
                break;

            case REVERSE_C:          // A - C à l'envers - B - D
                // fin de A -> fin de C
                cost += longueurEntre(currentTourIndexes.get(cut1), currentTourIndexes.get(cut3));
                // C à l'envers (fin -> début)
                cost += longueurCheminEntre(cut3, cut2 + 1);
                // début de C -> début de B
                cost += longueurEntre(currentTourIndexes.get(cut2 + 1), currentTourIndexes.get(cut1 + 1));
                // B à l'endroit (début -> fin)
                cost += longueurCheminEntre(cut1 + 1, cut2);
                // fin de B -> début de D
                cost += longueurEntre(currentTourIndexes.get(cut2), currentTourIndexes.get(cut3 + 1));
                break;

            case INVERT_REVERSE:          // A - C à l'envers - B à l'envers - D
                // fin de A -> fin de C
                cost += longueurEntre(currentTourIndexes.get(cut1), currentTourIndexes.get(cut3));
                // C à l'envers (fin -> début)
                cost += longueurCheminEntre(cut3, cut2 + 1);
                // début de C -> fin de B
                cost += longueurEntre(currentTourIndexes.get(cut2 + 1), currentTourIndexes.get(cut2));
                // B à l'envers (fin -> début)
                cost += longueurCheminEntre(cut2, cut1 + 1);
                // début de B -> début de D
                cost += longueurEntre(currentTourIndexes.get(cut1 + 1), currentTourIndexes.get(cut3 + 1));
                break;

        }

        return cost;
    }

    /**
     * Applies the reordering method given to the current tour (once its cost
     * evaluated) ONLY IF the resulting path is valid (if each pickup is visited
     * before its associated delivery).
     *
     * @param cut1   The index in the current tour of the point from which we
     *               'cut' the first edge.
     * @param cut2   The index in the current tour of the point from which we
     *               'cut' the second edge.
     * @param cut3   The index in the current tour of the point from which we
     *               'cut' the third edge.
     * @param order  The order according to which we reorder the sub-paths
     *               obtained after cutting the first sub-path.
     * @return true if the given optimization has been applied (= if it was
     * valid), else false
     * @see InsertionMethod
     * @see #threeOptCost(int, int, int, AssembleOrder)
     */
    boolean applyThreeOptIfValid(int cut1, int cut2, int cut3, AssembleOrder order) {

        // indices dans le trajet initial du début de chacun des sous-chemins A et B
        int firstB = cut1 + 1;
        int firstC = cut2 + 1;
        int lgB = cut2 - cut1;
        int lgC = cut3 - cut2;
        int i, j;

        // tableaux qui contiendront la portion de chemin modifiée
        int[] newPathIndexes = new int[lgB + lgC];
        ArrayList<TupleRequete> newPathPoints = new ArrayList<>();

        for (i = 0; i < lgB + lgC; ++i) {
            newPathPoints.add(new TupleRequete(null, false));
        }

        /*
         * Ordres possibles :
         * - DOUBLE_REVERSE : A - B à l'envers - C à l'envers - D
         * - INVERT-ORDER : A - C - B - D
         * - REVERSE_B : A - C - B à l'envers - D
         * - REVERSE_C : A - C à l'envers - B - D
         * - INVERT_REVERSE : A - C à l'envers - B à l'envers - D
         * */
        switch (order) {
            case DOUBLE_REVERSE:        // A - B à l'envers - C à l'envers - D
                // B à l'envers (fin -> début)
                for (i = 0; i < lgB; ++i) {
                    newPathIndexes[i] = currentTourIndexes.get(cut2 - i);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut2 - i));
                }
                // C à l'envers (fin -> début)
                for (j = 0; i < lgB + lgC; ++i, ++j) {
                    newPathIndexes[i] = currentTourIndexes.get(cut3 - j);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut3 - j));
                }
                break;

            case INVERT_ORDER:          // A - C - B - D
                // C à l'endroit (début -> fin)
                for (i = 0; i < lgC; ++i) {
                    newPathIndexes[i] = currentTourIndexes.get(firstC + i);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(firstC + i));
                }
                // B à l'endroit (début -> fin)
                for (j = 0; i < lgB + lgC; ++i, ++j) {
                    newPathIndexes[i] = currentTourIndexes.get(firstB + j);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(firstB + j));
                }
                break;

            case REVERSE_B:          // A - C - B à l'envers - D
                // C à l'endroit (début -> fin)
                for (i = 0; i < lgC; ++i) {
                    newPathIndexes[i] = currentTourIndexes.get(firstC + i);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(firstC + i));
                }
                // B à l'envers (fin -> début)
                for (j = 0; i < lgB + lgC; ++i, ++j) {
                    newPathIndexes[i] = currentTourIndexes.get(cut2 - j);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut2 - j));
                }
                break;

            case REVERSE_C:          // A - C à l'envers - B - D
                // C à l'envers (fin -> début)
                for (i = 0; i < lgC; ++i) {
                    newPathIndexes[i] = currentTourIndexes.get(cut3 - i);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut3 - i));
                }
                // B à l'endroit (début -> fin)
                for (j = 0; i < lgB + lgC; ++i, ++j) {
                    newPathIndexes[i] = currentTourIndexes.get(firstB + j);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(firstB + j));
                }
                break;

            case INVERT_REVERSE:          // A - C à l'envers - B à l'envers - D
                // C à l'envers (fin -> début)
                for (i = 0; i < lgC; ++i) {
                    newPathIndexes[i] = currentTourIndexes.get(cut3 - i);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut3 - i));
                }
                // B à l'envers (fin -> début)
                for (j = 0; i < lgB + lgC; ++i, ++j) {
                    newPathIndexes[i] = currentTourIndexes.get(cut2 - j);
                    newPathPoints.get(i).setFrom(currentTourPoints.get(cut2 - j));
                }
                break;
        }

        // ----- vérification de la validité du nouvel ordre des points de passage
        boolean isValid = true;
        HashSet<Integer> requestsStarted = new HashSet<>();

        // initialisation des requêtes dont on a déjà parcouru le pickup
        for (i = 0; i < firstB; ++i) {
            if (this.currentTourPoints.get(i) != null) {    // ne pas prendre les null du dépôt
                requestsStarted.add(this.currentTourPoints.get(i).requete.getId());
            }
        }

        // parcours des points qu'on a réordonnés
        for (i = 0; i < lgB + lgC; ++i) {
            TupleRequete req = newPathPoints.get(i);
            if (req.isDepart()) {   // on ajoute le pickup à la liste des requêtes en cours (/finies)
                requestsStarted.add(req.requete.getId());
            } else {        // on vérifie qu'on a déjà parcouru le pickup du delivery actuel
                if (!requestsStarted.contains(req.requete.getId())) {
                    isValid = false;
                    break;
                }
            }
        }

        if (isValid) {

            if (verbose) {
                System.err.println("Found a valid local optimization !!");
                System.err.println("- Old path: " + this.currentTourIndexes);
            }

            for (i = 0; i < newPathIndexes.length; ++i) {
                this.currentTourIndexes.set(firstB + i, newPathIndexes[i]);
                this.currentTourPoints.set(firstB + i, newPathPoints.get(i));
            }

            if (verbose)
                System.err.println("- New path: " + this.currentTourIndexes);

            return true;
        } else {
//            System.err.println("Local optimization found was invalid and not applied");
            return false;
        }
    }

    // --------------------------- Utilities

    /**
     * Generate each possible combination of cutting 3 edges in the sub-path of
     * length 2r+1 centered around center for the 3-opt local optimization.
     *
     * @param center The index in the current tour of the point around which the
     *               sub-path is centered
     * @return A list of all possible sets of 3 points (their indexes in the
     * current tour) from which we can 'cut' an edge and apply the 3-opt method.
     * @see DoubleInsertionTSP.ThreeOptCuts
     */
    ArrayList<ThreeOptCuts> allPossibleCuts(int center) {

        // prendre en compte les bounds du trajet pour ne pas dépasser
        int first = max(0, center - r);
        int last = min(currentTourIndexes.size() - 1, center + r);

        // ne pas calculer si moins de 3 arêtes à couper
        if (last - first < 3) {
            if (verbose)
                System.err.println("WARNING: Asking for an optimization with a path too small!");
            return null;
        }

        ArrayList<ThreeOptCuts> possibleCuts = new ArrayList<>();

        for (int i = first; i < last - 2; ++i) {
            for (int j = i + 1; j < last - 1; ++j) {
                for (int k = j + 1; k < last; ++k) {
                    possibleCuts.add(new ThreeOptCuts(i, j, k));
                }
            }
        }

        return possibleCuts;
    }

    /**
     * Build an use-ready Tournee object from the current tour.
     *
     * @return The fully usable Tournee object
     */
    Tournee buildTour() {

        // initialisation des éléments à passer au constructeur de Tournee
        ArrayList<Request> requestList = planning.getRequestList();

        ArrayList<TupleRequete> ptsPassage = new ArrayList<>();
        for (TupleRequete tupleRequete : this.currentTourPoints) {
            ptsPassage.add(tupleRequete);       // laisser les null ! ils sont traités après !
        }

        // initialisation de la liste des segments avec le chemin dépôt -> premier pickup
        ArrayList<Segment> segmentList = new ArrayList<>(matAdj[0][currentTourIndexes.get(1)].chemin);

        // remplissage du chemin avec la suite de la tournée
        for (int i = 1; i < ptsPassage.size() - 1; ++i) {
            TupleRequete pt1 = ptsPassage.get(i);
            TupleRequete pt2 = ptsPassage.get(i + 1);

            if (pt1 != null && pt2 != null) {        // attention aux null
                if (pt1.getCurrentGoal().getId() != pt2.getCurrentGoal().getId()) {
                    segmentList.addAll(matAdj[ptsIdToIndex.get(pt1.getCurrentGoal().getId())][ptsIdToIndex.get(pt2.getCurrentGoal().getId())].chemin);
                } else {
                    if (verbose)
                        System.err.println("Not adding path with identical pickup & delivery");
                }
            } else if (i == ptsPassage.size() - 2) {      // dernier delivery -> dépôt
                segmentList.addAll(matAdj[ptsIdToIndex.get(pt1.getCurrentGoal().getId())][0].chemin);
            } else {
                if (verbose) {
                    System.err.println("AAAATTENTION AUX NUUUUUUUUUUUUUUUUUUL(L)S !! i = " + i);
                    System.err.println(pt1 + ", " + pt2);
                }
            }
        }

        // ajout des heures d'arrivée à chaque point de passage + le chemin qui y mène
        Tournee tournee = new Tournee(segmentList, requestList);
        ComputeTour.recreateTimesTournee(tournee, planning);
        return tournee;
    }

    /**
     * Return the length of the shortest path between 2 points according to
     * matAdj.
     *
     * @param depart  The index in matAdj of the starting point of the desired
     *                path
     * @param arrivee The index in matAdj of the arrival point of the desired
     *                path
     * @return The length of the shortest path between depart and arrivee
     */
    private float longueurEntre(int depart, int arrivee) {
        if (matAdj[depart][arrivee] != null) {
            return matAdj[depart][arrivee].getLongueur();
        } else {
            return 0;
        }
    }

    /**
     * Compute the length of the sub-path in the current tour between the given
     * starting and arrival points, possibly by taking it backwards if
     * arrivee < depart.
     *
     * @param depart  The index in the current tour of the starting point of the
     *                desired path
     * @param arrivee The index in the current tour of the arrival point of the
     *                desired path
     * @return The length of the desired path
     * @see DoubleInsertionTSP#longueurEntre(int, int)
     */
    private float longueurCheminEntre(int depart, int arrivee) {

        if (depart == arrivee) {        // pas de chemin, points confondus
            return 0;
        } else if ((depart - arrivee == 1) || (depart - arrivee == -1)) {      // chemin trivial (depart, arrivee)
            return longueurEntre(currentTourIndexes.get(depart), currentTourIndexes.get(arrivee));
        }

        // chemin non trivial composé d'au moins 3 points
        float sum = 0;
        if (arrivee > depart) {         // parcours dans le sens normal
            for (int i = depart; i < arrivee; ++i) {
                sum += longueurEntre(currentTourIndexes.get(i), currentTourIndexes.get(i + 1));
            }
        } else {                        // parcours dans le sens inverse
            for (int i = depart; i > arrivee; --i) {
                sum += longueurEntre(currentTourIndexes.get(i), currentTourIndexes.get(i - 1));
            }
        }

        return sum;
    }

    /**
     * Add a given point to the current tour at a given index.
     *
     * @param index        The index in the current tour where the point is to
     *                     be inserted (its index after insertion)
     * @param tupleRequete The point to be inserted
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
     * Overload of the identically-named method to add a point directly to the
     * end of the current tour.
     *
     * @param tupleRequete The point to add at the end ot the tour
     * @see DoubleInsertionTSP#ajouterPointTournee(TupleRequete, int)
     */
    void ajouterPointTournee(TupleRequete tupleRequete) {
        ajouterPointTournee(tupleRequete, currentTourIndexes.size());
    }

    @Override
    public String toString() {
        return "PaperHeuristicTSP{" +
                "currentTourIndexes=" + currentTourIndexes +
                ", nbRequest=" + nbRequest +
                '}';
    }

}
