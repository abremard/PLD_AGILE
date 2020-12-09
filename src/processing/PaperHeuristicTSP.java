package processing;

import objects.PlanningRequest;
import objects.Request;
import objects.Segment;
import objects.Tournee;

import java.util.*;

import static java.lang.StrictMath.*;


/**
 * Heuristique de résolution du TSP pickup-delivery (TSPPC) d'après l'article
 * "A heuristic for the pickup and delivery traveling salesman problem" de J.
 * Renaud, F. F. Boctor et J. Ouenniche (2000)
 */
public class PaperHeuristicTSP {

    enum InsertionMethod {CONSECUTIVE, SPLIT}

    /**
     * Façons de réassembler un chemin A-B-C-D auquel on a enlevé les 3 arêtes
     * entre A et B, entre B et C et entre C et D, ces quatre lettres
     * représentant chacune une portion du chemin, composée d'un ou plus
     * point(s), sans considérer les cas 2-opt (on repose une arête là où on
     * vient d'en enlever une, dans le même sens)
     * <p>
     * - DOUBLE_REVERSE : A - B à l'envers - C à l'envers - D
     * - INVERT-ORDER : A - C - B - D
     * - REVERSE_B : B - C - B à l'envers - D
     * - REVERSE_C : A - C à l'envers - B - D
     * - INVERT_REVERSE : A - C à l'envers - B à l'envers - D
     */
    enum AssembleOrder {DOUBLE_REVERSE, INVERT_ORDER, REVERSE_B, REVERSE_C, INVERT_REVERSE}

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

    /**
     * Classe servant de tuple de 3 entiers pour définir les points de coupure
     * des arêtes dans l'optimisation 3-opt
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
                    ArrayList<ThreeOptCuts> possibleCuts = AllPossibleCuts(minDeltaI.index1);
                    boolean stop = false;

                    // pour chaque possibilité de 3-opt, évaluer son coût, et la garder si meilleure que l'originale
                    for (ThreeOptCuts cuts : possibleCuts) {
                        initialCost = longueurCheminEntre(cuts.cut1, cuts.cut3 + 1);

                        for (AssembleOrder order : AssembleOrder.values()) {
                            newCost = threeOptCost(center, cuts.cut1, cuts.cut2, cuts.cut3, order);

                            // si on trouve un meilleur trajet et qu'il respecte les contraintes de précédence, on l'applique
                            if (newCost < initialCost) {
                                // on applique l'optimisation si elle est valide
                                if (applyThreeOptIfValid(center, cuts.cut1, cuts.cut2, cuts.cut3, order)) {
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
            }
        }
    }

    /**
     * Calcule le 'minimum weighted insertion cost' (delta_i) pour une requête
     * donnée dans l'état actuel du trajet.
     * Utilisée dans les étapes 1.2 et 2.2 de l'algorithme
     *
     * @param request La requête dont on calcule le delta_i
     * @return delta_i, le minimum weighted insertion cost de la meilleure
     * combinaison trouvée, ainsi que sa méthode & sa position d'insertion
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
     * Renvoie le coût de la permutation 3-opt selon la méthode renseignée.
     * Ce coût correspond à la longueur du chemin considéré qui variera en
     * fonction de l'ordre dans lequel on assemble les sous-chemins, c'est-
     * à-dire le coût du chemin qui commence à cut1 et qui finit à cut3 + 1.
     *
     * @param center L'indice dans le chemin du point autour duquel on effectue
     *               l'optimisation (longueur du sous-chemin déterminée par r)
     * @param cut1   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               première arête (dernier point de A)
     * @param cut2   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               deuxième arête (dernier point de B)
     * @param cut3   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               troisième arête (dernier point de C)
     * @param order  L'ordre dans lequel on assemble les sous-chemins entre eux
     * @see InsertionMethod
     * @see PaperHeuristicTSP#doubleInsertionHeuristic()
     */
    public float threeOptCost(int center, int cut1, int cut2, int cut3, AssembleOrder order) {

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
     * Applique la méthode de réarrangement choisie après calcul de son coût par
     * 3-opt au trajet actuel uniquement si le trajet est valide
     *
     * @param center L'indice dans le chemin du point autour duquel on effectue
     *               l'optimisation (longueur du sous-chemin déterminée par r)
     * @param cut1   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               première arête (dernier point de A)
     * @param cut2   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               deuxième arête (dernier point de B)
     * @param cut3   L'indice du point dans le chemin depuis lequel on "coupe" la
     *               troisième arête (dernier point de C)
     * @param order  L'ordre dans lequel on assemble les sous-chemins entre eux
     * @return true si l'optimisation demandée était valide et a été appliquée,
     * false sinon
     * @see InsertionMethod
     * @see PaperHeuristicTSP#threeOptCost(int, int, int, int, AssembleOrder)
     */
    boolean applyThreeOptIfValid(int center, int cut1, int cut2, int cut3, AssembleOrder order) {

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

            System.err.println("Found a valid local optimization !!");
            System.err.println("- Old path: " + this.currentTourIndexes);

            for (i = 0; i < newPathIndexes.length; ++i) {
                this.currentTourIndexes.set(firstB + i, newPathIndexes[i]);
                this.currentTourPoints.set(firstB + i, newPathPoints.get(i));
            }

            System.err.println("- New path: " + this.currentTourIndexes);

            return true;
        } else {
//            System.err.println("Local optimization found was invalid and not applied");
            return false;
        }
    }

    /**
     * Méthode pour générer toutes les combinaisons possibles de 3 arêtes à
     * enlever au chemin de longueur 2r+1 centré en center pour l'optimisation
     * 3-opt
     *
     * @param center Le milieu du chemin sur lequel on applique l'optimisation
     * @return La liste des ensembles de 3 points à partir desquels on peut
     * "couper" une arête pour appliquer l'optimisation 3-opt
     * @see PaperHeuristicTSP.ThreeOptCuts
     */
    ArrayList<ThreeOptCuts> AllPossibleCuts(int center) {

        // prendre en compte les bounds du trajet pour ne pas dépasser
        int first = max(0, center - r);
        int last = min(currentTourIndexes.size() - 1, center + r);

        // ne pas calculer si moins de 3 arêtes à couper
        if (last - first < 3) {
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
     * Construit un objet Tournee utilisable par l'IHM à partir du trajet actuel
     *
     * @return L'objet complet et utilisable
     */
    Tournee buildTour() {

        // initialisation des éléments à passer au constructeur de Tournee
        ArrayList<Request> requestList = planning.getRequestList();

        ArrayList<TupleRequete> ptsPassage = new ArrayList<>();
        for (TupleRequete tupleRequete: this.currentTourPoints) {
            ptsPassage.add(tupleRequete);       // laisser les null ! ils sont traités après !
        }

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

        // debug : vérification de la continuité du chemin (liste de segments)
        boolean isCorrect = true;
        for (int i = 0; i < segmentList.size() - 1; ++i) {
            if (segmentList.get(i).getDestination() != segmentList.get(i+1).getOrigin()) {
                System.err.println("-_-_-_-_- BAH ALORS ON SAIT PAS CODER ?????? REGARDE DONC A L INDICE " + i);
                isCorrect = false;
            }
        }
        if (isCorrect) System.err.println("WOLA C PA MOA");

        // ajout des heures d'arrivée à chaque point de passage + le chemin qui y mène
        Tournee tournee = new Tournee(segmentList, requestList);
        ComputeTour.recreateTimesTournee(tournee, planning);
        return tournee;
    }

    /**
     * Renvoie la longueur du plus court chemin entre 2 points d'intérêt selon
     * la matrice d'adjacence
     *
     * @param depart  L'indice dans matAdj du point de départ du chemin
     * @param arrivee L'indice dans matAdj du point d'arrivée du chemin
     * @return La longueur du plus court chemin entre depart et arrivee
     */
    private float longueurEntre(int depart, int arrivee) {
        if (matAdj[depart][arrivee] != null) {
            return matAdj[depart][arrivee].getLongueur();
        } else {
            return 0;
        }
    }

    /**
     * Calcule la longueur du sous-chemin dans le trajet actuel entre les points
     * de départ et d'arrivée donnés, éventuellement en le parcourant à l'envers
     * si arrivee < depart.
     * Utilise longueurEntre(int, int) pour un sous-chemin constitué uniquement
     * d'un départ et d'une arrivée.
     *
     * @param depart  L'indice dans le trajet actuel du point de départ du sous-
     *                chemin dont on cherche à calculer la longueur
     * @param arrivee L'indice dans le trajet du point d'arrivée du sous-chemin
     * @return La longueur du chemin demandée
     * @see PaperHeuristicTSP#longueurEntre(int, int)
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
     *
     * @param tupleRequete le point de passage à insérer     *
     * @see PaperHeuristicTSP#ajouterPointTournee(TupleRequete, int)
     */
    void ajouterPointTournee(TupleRequete tupleRequete) {
        ajouterPointTournee(tupleRequete, currentTourIndexes.size());
    }

}
