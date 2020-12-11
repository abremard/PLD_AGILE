
package processing;

import java.time.LocalTime;
import java.util.*;

import Branch_And_Bound_TSP.TSP;
import Branch_And_Bound_TSP.TSP1;
import Branch_And_Bound_TSP.TSP2;
import objects.*;
import objects.Map;
import sample.Controller;

/*
 * TODO
 *  - arrêter Dijsktra quand on a traité tous les points d'intérêt
 *  - heuristique du paper
 *  - JavaDoc
 *
 * FIXME
 *  - random passe pas par tous les points :'( (notamment largeMap + requestsLarge9)
 * */

/**
 * Class that groups the tour computation algorithms.
 * Use planTour() to get a Tour.
 *
 * @author H4302
 * @see command.ComputeTourCommand
 */
public class ComputeTour {

    static boolean verbose = false;

    /**
     * Main function of ComputeTour, computes a Tour according to the informations of the map and requests list
     * XML files, as well as a choice of heuristic.
     *
     * @param map         The map representing the graph on which we search
     * @param planning    The planning that includes a list of requests to deal with as well as the departure address
     * @param heuristique The heuristic to use (influences the quality of the result and execution speed)
     * @return the computed Tour
     */
    public static Tournee planTour(Map map, PlanningRequest planning, Heuristique heuristique) {

        Tournee tournee;
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        SuperArete[][] matAdj;

        switch (heuristique) {
            case TRIVIALE:
                // version triviale
                return tourneeTriviale(map, planning, intersecIdToIndex);
            case RANDOM:
                // version random
                return tourneeRandom(map, planning, intersecIdToIndex);
            case GREEDY:
                // version greedy
                matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
                tournee = greedy(matAdj, planning, intersecIdToIndex);
                recreateTimesTournee(tournee, planning);
                return tournee;
            case GENETIQUE:
                // version genetique
                return null;
            case BRANCHANDBOUND:
                // version greedy
                matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
                tournee = branchAndBoundOpti(matAdj, planning);
                recreateTimesTournee(tournee, planning);
                return tournee;
            case DOUBLEINSERTION:
                // version double-insertion heuristic
                matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
                // indexation des points d'intérêt
                HashMap<Long, Integer> ptsIdToIndex = indexerPtsInteret(planning);
                PaperHeuristicTSP doubleInsertion = new PaperHeuristicTSP(matAdj, planning, ptsIdToIndex);
                // calcul étape 1
                doubleInsertion.doubleInsertionHeuristic();

                // résultats
                if (verbose) {
                    System.err.println("=== Double-insertion heuristic results ===");
                    System.err.println(doubleInsertion);
                }
                return doubleInsertion.buildTour();
        }

        return null;
    }

    /**
     * Computes the Tour passing through all pickup and delivery points in a specific order.
     *
     * @param map         The map representing the graph on which we search
     * @param planning    The planning that includes a list of requests to deal with as well as the departure address
     * @param order       The points through which the Tour has to pass, in chronological order
     * @return the computed Tour
     */
    public static Tournee recreateTourneeWithOrder(Map map, PlanningRequest planning, ArrayList<Controller.LocationTagContent> order) {
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        HashMap<Long, Integer> indexPtsInterets = indexerPtsInteret(planning);
        SuperArete[][] matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);

        ArrayList<Segment> chemin = new ArrayList<Segment>();
        int previousInd = 0;
        for (int i = 0; i < order.size(); i++) {
            chemin.addAll(matAdj[previousInd][indexPtsInterets.get(order.get(i).getDestination().getId())].getChemin());
            previousInd = indexPtsInterets.get(order.get(i).getDestination().getId());
        }

        Tournee tournee = new Tournee(chemin, planning.getRequestList());
        recreateTimesTournee(tournee, planning);
        return tournee;
    }

    public static SuperArete[][] testFullGraph(Map map, PlanningRequest planning) {
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        return getOptimalFullGraph(map, planning, intersecIdToIndex);
    }

    // ----------------------------- Utilitarian functions

    /**
     * Dijkstra on the given map from a departure point to all interest points.
     *
     * @param map        The map representing the graph on which we search
     * @param depart     The starting point of the search
     * @param ptsInteret List of the interest points : points to which we want to know the shortest path from the
     *                   starting point
     * @return the predecessor list of each node according to the shortest path towards all interest points and nodes
     * on the path to interest nodes. The function stops when the shortest paths to all interest points are computed.
     */
    private static ArrayList<Segment> dijkstra(Map map, Intersection depart, LinkedList<Intersection> ptsInteret, HashMap<Long, Integer> intersecIdToIndex) {

        // --------- indexation des Intersections
        // liste des intersections -> todo changer les linkedlist vers arraylist ou qqch comme ça ?
        ArrayList<Intersection> intersections = map.getIntersectionList();

        // On recupere la liste d'adjacence
        ArrayList<ArrayList<Segment>> adjList = getListeAdj(map, intersecIdToIndex);

        // tableau des distances
        ArrayList<TupleDijkstra> dist = new ArrayList<>(map.getNoOfIntersections());
        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            dist.add(new TupleDijkstra(intersections.get(i), (float) -1));
        }
        dist.get(intersecIdToIndex.get(depart.getId())).distance = 0;   // initialisation de la distance de la premiere Intersection

        // liste des prédécesseurs
        ArrayList<Segment> pred = new ArrayList<>(map.getNoOfIntersections());
        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            pred.add(null);
        }

        // methodes dispo sur la pQueue : add, poll, peek
        PriorityQueue<TupleDijkstra> pQueue = new PriorityQueue<TupleDijkstra>();
        pQueue.add(dist.get(intersecIdToIndex.get(depart.getId())));       // initialisation de la pQueue

        while (!pQueue.isEmpty()) {

            TupleDijkstra curNoeud = pQueue.poll();
            int curIndex = intersecIdToIndex.get(curNoeud.intersection.getId());

            for (Segment seg : adjList.get(curIndex)) {     // parcours des voisins/arcs sortants
                int indexArrivee = intersecIdToIndex.get(seg.getDestination());
                float distVoisin = dist.get(indexArrivee).distance;

                // relâcher l'arc
                if (distVoisin == -1 || distVoisin > curNoeud.distance + seg.getLength()) {     // meilleur chemin qu'avant
                    TupleDijkstra voisin = dist.get(indexArrivee);
                    if (pQueue.contains(voisin)) {
                        pQueue.remove(voisin);
                        voisin.distance = curNoeud.distance + seg.getLength();      // màj distance
                        pQueue.add(voisin);
                    } else {
                        voisin.distance = curNoeud.distance + seg.getLength();      // màj distance
                    }
                    pred.set(indexArrivee, seg);            // màj prédécesseur

                    if (voisin.color == TupleDijkstra.Color.WHITE) {            // noeud pas encore traité
                        pQueue.add(voisin);
                        voisin.color = TupleDijkstra.Color.GREY;
                    }
                }
            } // fin du parcours des arcs sortants
        }

        return pred;
    }

    /**
     * Computes the full graph in which all nodes are pickup, delivery points or the departure address
     * (interest points), and vertices are the shortest paths on the Map between those interest points.
     *
     * @param map               The map representing the graph on which we search
     * @param planning          The planning that includes a list of requests to deal with as well as the
     *                          departure address
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the full graph as an adjacence matrix. Accessing it at indices [departure][destination] gives the
     * SuperArete object in which the path attibute is the shortest path (as list of map Segments) from the departure
     * node to the destination node.
     */
    private static SuperArete[][] getOptimalFullGraph(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Request> requests = planning.getRequestList();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        LinkedList<Intersection> ptsInteret = new LinkedList<Intersection>();
        ptsInteret.add(planning.getDepot().getAdresse());
        boolean found;
        for (Request req : requests) {
            found = false;
            for (Intersection node : ptsInteret) {
                if (req.getPickup().getId() == node.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ptsInteret.add(req.getPickup());
            }

            found = false;
            for (Intersection node : ptsInteret) {
                if (req.getDelivery().getId() == node.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                ptsInteret.add(req.getDelivery());
            }

//            if(!ptsInteret.contains(req.getPickup())){
//                ptsInteret.add(req.getPickup());
//            }
//            if(!ptsInteret.contains(req.getDelivery())){
//                ptsInteret.add(req.getDelivery());
//            }
        }

        int nInodes = ptsInteret.size();
        SuperArete[][] adjMatrix = new SuperArete[nInodes][nInodes];

        for (int i = 0; i < nInodes; i++) {
            ArrayList<Segment> predList = dijkstra(map, ptsInteret.get(i), ptsInteret, intersecIdToIndex);
            for (int j = 0; j < nInodes; j++) {
                if (i != j) {
                    ArrayList<Segment> chemin = recreateChemin(predList, ptsInteret.get(i), ptsInteret.get(j), intersections, intersecIdToIndex);
                    adjMatrix[i][j] = new SuperArete(chemin, intersections, intersecIdToIndex);
                }
            }
        }

        return adjMatrix;
    }

    /**
     * Computes the adjacence list corresponding to the graph built from the Segments and Intersections of the Map
     * given as parameter.
     *
     * @param map               The map representing the graph on which we search
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the adjacence list, indexed according to the indices given by the HashMap given as parameter,
     * and including for each Intersection the list of Segments having for origin this Intersection.
     */
    private static ArrayList<ArrayList<Segment>> getListeAdj(Map map, HashMap<Long, Integer> intersecIdToIndex) {

        // capacité initiale (taille max atteignable sans réallouer de la mémoire)
        ArrayList<ArrayList<Segment>> listeAdj = new ArrayList<>(map.getNoOfIntersections());

        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            listeAdj.add(new ArrayList<Segment>());     // optimisation possible : capacité initiale != 0
        }

        for (Segment segment : map.getSegmentList()) {
            int departId = intersecIdToIndex.get(segment.getOrigin());
            listeAdj.get(departId).add(segment);
        }

        return listeAdj;
    }

    /**
     * Creates a dictionnary giving the index of an Intersection in the Intersection list of the Map,
     * according to its ID.
     *
     * @param map        The map representing the graph on which we search
     * @return the dictionnary
     */
    private static HashMap<Long, Integer> indexationIntersections(Map map) {
        // dico id -> index dans les tableaux indexés par intersections
        // pour le sens inverse : utiliser le tableau intersections
        HashMap<Long, Integer> intersecIdToIndex = new HashMap<Long, Integer>();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            intersecIdToIndex.put(intersections.get(i).getId(), i);
        }

        return intersecIdToIndex;
    }

    /**
     * Recreates a path (as a Segments list) from a predecessor list, a departure node and a destination node.
     *
     * @param predList          The predecessor vertices list : at each index, the Segment leading to this node
     *                          from the previous node
     * @param depart            The Intersection at the departure of the path
     * @param arrivee           The Intersection at the destination of the path
     * @param intersections     The Intersection list of the map
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the path from the departure node to the destination node
     */
    private static ArrayList<Segment> recreateChemin(ArrayList<Segment> predList, Intersection depart, Intersection arrivee, ArrayList<Intersection> intersections, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        Intersection curNode = arrivee;

        while (curNode.getId() != depart.getId()) {
            Segment boutChemin = predList.get(intersecIdToIndex.get(curNode.getId()));
            chemin.add(0, boutChemin);
            curNode = intersections.get(intersecIdToIndex.get(boutChemin.getOrigin()));
        }

        return chemin;
    }

    /**
     * Computes the arrival times of each pickup/delivery, as well as the associated path fragments.
     * Used when it would be more complicated to compute this during the main Tour calculations.
     *
     * @param tournee       The Tour to modify (no return, modifies the parameter directly)
     * @param planning      The planning that includes a list of requests to deal with as well as the
     *                      departure address
     */
    public static void recreateTimesTournee(Tournee tournee, PlanningRequest planning) {

        LocalTime curTime = planning.getDepot().getDepartureTime();
        ArrayList<Segment> curChemin = new ArrayList<Segment>();
        // pool
        LinkedList<TupleRequete> pool = new LinkedList<TupleRequete>();
        for (Request req : planning.getRequestList()) {
            pool.add(new TupleRequete(req, true));
        }
        pool.add(new TupleRequete(new Request(planning.getRequestList().size(), planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), false));

        ArrayList<TupleRequete> ptsPassage = new ArrayList<TupleRequete>();

        for (Segment seg : tournee.getSegmentList()) {
            curTime = curTime.plusSeconds((long) (seg.getLength() * 3600 / 15000.0));
            curChemin.add(seg);

            LinkedList<TupleRequete> aDelete = new LinkedList<TupleRequete>();
            boolean found = false;
            for (TupleRequete req : pool) {
                if (req.isDepart && req.requete.getPickup().getId() == seg.getDestination()) {
                    req.isDepart = false;
                    curTime = curTime.plusSeconds((long) (req.getRequete().getPickupDur()));
                    if (verbose)
                        System.out.println("Pickup a " + req.getRequete().getPickup().getId() + " pendant " + req.getRequete().getPickupDur());
                    ptsPassage.add(new TupleRequete(req.getRequete(), true, curTime, curChemin));
                    found = true;
                }
                if (!req.isDepart && req.requete.getDelivery().getId() == seg.getDestination()) {
                    aDelete.add(req);
                    curTime = curTime.plusSeconds((long) (req.getRequete().getDeliveryDur()));
                    if (verbose)
                        System.out.println("Delivery a " + req.getRequete().getDelivery().getId() + " pendant " + req.getRequete().getDeliveryDur());
                    ptsPassage.add(new TupleRequete(req.getRequete(), false, curTime, curChemin));
                    found = true;
                }
            }
            pool.removeAll(aDelete);
            if (found) {
                if (verbose)
                    System.out.println("On va de " + curChemin.get(0).getOrigin() + " a " + curChemin.get(curChemin.size() - 1).getDestination());
                curChemin = new ArrayList<Segment>();
            }
        }

        tournee.setPtsPassage(ptsPassage);
    }

    /**
     * Chooses a random element in any ArrayList.
     *
     * @param arrayList     The list in which the element is chosen
     * @return the chosen element
     */
    private static <T> T pickRandom(ArrayList<T> arrayList) {
        int rnd = new Random().nextInt(arrayList.size());
        return arrayList.get(rnd);
    }

    /**
     * Indexes the pickup and delivery of each request as well as the departure address in the following way :
     * - index 0 : departure address
     * - index 1 and above : each interest point other than the departure address, indexed according to ptsIdToIndex
     * @param planning      The planning that includes a list of requests to deal with as well as the
     *                      departure address
     * @return the dictionnary associating the intersection ID to its index
     */
    private static HashMap<Long, Integer> indexerPtsInteret(PlanningRequest planning) {
        HashMap<Long, Integer> ptsIdToIndex = new HashMap<>();

        ptsIdToIndex.put(planning.getDepot().getAdresse().getId(), 0);

        int index = 1;
        for (Request req : planning.getRequestList()) {
            if (!ptsIdToIndex.containsKey(req.getPickup().getId())) {     // pas sûr que c'est nécessaire
                ptsIdToIndex.put(req.getPickup().getId(), index);
                ++index;
            }
            if (!ptsIdToIndex.containsKey(req.getDelivery().getId())) {     // pas sûr que c'est nécessaire
                ptsIdToIndex.put(req.getDelivery().getId(), index);
                ++index;
            }
        }

        return ptsIdToIndex;
    }

    /**
     * Rebuilds a Tour object from a full path and the given Requests.
     *
     * @param chemin    The path to use for the Tour
     * @return the computed Tour
     */
    private static Tournee cheminVersTournee(PlanningRequest planningRequest, ArrayList<SuperArete> chemin) {
        ArrayList<Segment> segmentList = new ArrayList<>();
        for (SuperArete sa : chemin) {
            if (sa.chemin != null) {
                segmentList.addAll(sa.chemin);
            }
        }
        return new Tournee(segmentList, planningRequest.getRequestList());
    }

    /**
     * Crée un chemin aléatoire valide, c'est-à-dire qui passe par tous les points de pickup et de delivery,
     * et que tous les pickups sont réalisés avant les deliveries associées.
     *
     * @param matAdj       The optimal full sub-graph of the map as an adjacence matrix
     * @param planning     The planning that includes a list of requests to deal with as well as the
     *                     departure address
     * @param ptsIdToIndex The dictionnary associating and Intersection ID with its index in the adjacence matrix
     * @return the path as a SuperAretes list, representing each the path between two consecutive interest points.
     * Contract : the first index in matAdj represents the departure address.
     */
    private static ArrayList<SuperArete> cheminAleatoire(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> ptsIdToIndex) {

        /* --- Principe :
         * Initialiser le pool d'intersections à tous les départs des requêtes
         * Choisir un départ de requête au hasard dans le pool et le retirer
         * Initialiser le chemin à la SuperArete [dépôt -> départ choisi]
         * Ajouter l'arrivée de la requête associée départ choisi au pool
         * Tant que le pool n'est pas vide :
         *  - Prendre un élément au hasard dans le pool
         *  - Ajouter au chemin la SuperArete [dernière Intersection du chemin -> élément choisi]
         *  - Si cet élément est un départ de requête, ajouter son arrivée dans le pool
         * Ajouter la SuperArete [dernier point -> dépôt]
         *
         * -> dans l'implémentation actuelle, les départs & les arrivées sont stockées dans un même objet TupleRequete
         * (on ne supprime pas un point de départ du pool, on met juste à jour isDepart de son objet TupleRequete)
         * */

        // initialisation du pool
        ArrayList<TupleRequete> pool = new ArrayList<>(planning.getRequestList().size());
//        Set<TupleRequete> poolset = new HashSet<>();      // todo tester avec le set pour optimiser les suppressions
        for (Request req : planning.getRequestList()) {
            pool.add(new TupleRequete(req, true));
        }

        TupleRequete pick = pickRandom(pool);

        // initialisation du chemin
        ArrayList<SuperArete> chemin = new ArrayList<>();
        int indexDernierPt = 0;
        int indexActuel = ptsIdToIndex.get(pick.requete.getPickup().getId());
        // ajout de la nouvelle portion de chemin
        if (indexActuel != indexDernierPt) {
            if (matAdj[indexDernierPt][indexActuel] != null) {
                chemin.add(matAdj[indexDernierPt][indexActuel]);  // dépôt -> premier départ
            } else {
                System.err.println("null found where it should not be ! (" + indexDernierPt + " -> " + indexActuel + ")");
            }
        } else {
            System.err.println("Path with identical pickup & delivery ignored");
        }
        pick.isDepart = false;

        // debug
        if (matAdj[indexDernierPt][indexActuel] == null) {
            if (indexActuel == indexDernierPt) {
                System.err.println("null added to chemin at " + indexDernierPt + ", " + indexActuel);
            }
        }

        while (pool.size() > 0) {
            // choix d'un chemin & update des indices
            pick = pickRandom(pool);
            indexDernierPt = indexActuel;
            indexActuel = ptsIdToIndex.get(pick.requete.getPickup().getId());

            // ajout de la nouvelle portion de chemin
            if (indexActuel != indexDernierPt) {
                if (matAdj[indexDernierPt][indexActuel] != null) {
                    chemin.add(matAdj[indexDernierPt][indexActuel]);  // dépôt -> premier départ
                } else {
                    System.err.println("null found where it should not be ! (" + indexDernierPt + " -> " + indexActuel + ")");
                }
            } else {
                System.err.println("Path with identical pickup & delivery ignored");
            }

            if (pick.isDepart) {
                pick.isDepart = false;
            } else {
                pool.remove(pick);
            }
        }

        // ajout du retour au dépôt
        indexDernierPt = indexActuel;
        indexActuel = 0;
        // ajout de la nouvelle portion de chemin
        if (indexActuel != indexDernierPt) {
            if (matAdj[indexDernierPt][indexActuel] != null) {
                chemin.add(matAdj[indexDernierPt][indexActuel]);  // dépôt -> premier départ
            } else {
                System.err.println("null found where it should not be ! (" + indexDernierPt + " -> " + indexActuel + ")");
            }
        } else {
            System.err.println("Path with identical pickup & delivery ignored");
        }

        return chemin;
    }

    // ----------------------------- Heuristics

    /**
     * Computes a Tour in the fastest possible way, by iteratively going from a request's pickup to its delivery,
     * then to the next request's pickup, for each Request.
     *
     * @param map               The map representing the graph on which we search
     * @param planning          The planning that includes a list of requests to deal with as well as the
     *                          departure address
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the computed Tour
     */
    private static Tournee tourneeTriviale(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {
        LocalTime curTime = planning.getDepot().getDepartureTime();
        ArrayList<TupleRequete> ptsPassage = new ArrayList<TupleRequete>();
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        Intersection previousDelivery = planning.getDepot().getAdresse();
        LinkedList<Intersection> ptsInteret;
        ArrayList<Segment> predList;
        for (Request request : planning.getRequestList()) {
            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getPickup());
            predList = dijkstra(map, previousDelivery, ptsInteret, intersecIdToIndex);
            ArrayList<Segment> curChemin = recreateChemin(predList, previousDelivery, request.getPickup(), intersections, intersecIdToIndex);
            chemin.addAll(curChemin);

            float travelDur = 0;
            for (Segment seg : curChemin) {
                travelDur += seg.getLength();
            }
            travelDur *= 3600.0 / 15000.0; // conversion de metres vers secondes
            curTime = curTime.plusSeconds((long) travelDur);

            ptsPassage.add(new TupleRequete(request, true, curTime, curChemin));

            curTime = curTime.plusSeconds((long) request.getPickupDur());


            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getDelivery());
            predList = dijkstra(map, request.getPickup(), ptsInteret, intersecIdToIndex);
            curChemin = recreateChemin(predList, request.getPickup(), request.getDelivery(), intersections, intersecIdToIndex);
            chemin.addAll(curChemin);

            travelDur = 0;
            for (Segment seg : curChemin) {
                travelDur += seg.getLength();
            }
            travelDur *= 3600.0 / 15000.0; // conversion de metres vers secondes
            curTime = curTime.plusSeconds((long) travelDur);

            ptsPassage.add(new TupleRequete(request, false, curTime, curChemin));

            curTime = curTime.plusSeconds((long) request.getPickupDur());

            previousDelivery = request.getDelivery();
        }

        // retour au depot
        ptsInteret = new LinkedList<Intersection>();
        ptsInteret.add(planning.getDepot().getAdresse());
        predList = dijkstra(map, previousDelivery, ptsInteret, intersecIdToIndex);
        ArrayList<Segment> lastChemin = recreateChemin(predList, previousDelivery, planning.getDepot().getAdresse(), intersections, intersecIdToIndex);
        float travelDur = 0;
        for (Segment seg : lastChemin) {
            travelDur += seg.getLength();
        }
        travelDur *= 3600.0 / 15000.0; // conversion de metres vers secondes
        curTime = curTime.plusSeconds((long) travelDur);
        chemin.addAll(lastChemin);
        ptsPassage.add(new TupleRequete(new Request(planning.getRequestList().size(), previousDelivery, planning.getDepot().getAdresse(), 0, 0), false, curTime, lastChemin));

        return new Tournee(chemin, planning.getRequestList(), ptsPassage);
    }

    /**
     * Todo : pas encore implémentée
     *
     * @param matAdj       The optimal full sub-graph of the map as an adjacence matrix
     * @param planning     The planning that includes a list of requests to deal with as well as the
     *                     departure address
     * @return the computed Tour
     */
    private static Tournee geneticATSP(SuperArete[][] matAdj, PlanningRequest planning) {
        HashMap<Long, Integer> ptIdToIndex = new HashMap<>();       // !! pas les mêmes index qu'avant
        return null;
    }

    /**
     * Computes a Tour using a greedy algorithm, heading at each iteration to the closest remaining interest point
     * to the current position.
     *
     * @param matAdj       The optimal full sub-graph of the map as an adjacence matrix
     * @param planning          The planning that includes a list of requests to deal with as well as the
     *                          departure address
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the computed Tour
     */
    private static Tournee greedy(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        /* Principe :
         *  - initialiser un pool à la liste des départs
         *  - prendre le départ le plus proche du dépot du pool
         *  - ajouter son arrivée au pool
         *  - tant qu'on a pas fini le chemin (= tant que le pool n'est pas vide)
         *    - prendre le point du pool le plus proche du dernier point choisi
         *    - si c'est un départ, ajouter son arrivée au pool
         */

        LocalTime curTime = planning.getDepot().getDepartureTime();
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        ArrayList<TupleRequete> ptsPassage = new ArrayList<TupleRequete>();
        ArrayList<Request> requests = planning.getRequestList();

        LinkedList<TupleRequete> pool = new LinkedList<TupleRequete>();
        for (Request req : requests) {
            pool.add(new TupleRequete(req, true));
        }
        //pool.add(new TupleRequete(new Request(planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), true));

        int curDepartInd = 0;
        for (TupleRequete req : pool) {
            if (req.requete.getPickup().getId() == matAdj[curDepartInd][1].depart.getId()) {
//                System.out.println("Pickup, attente de " + req.requete.getPickupDur() + " s");
                curTime = curTime.plusSeconds((long) req.requete.getPickupDur());
                req.isDepart = false;
            }
        }
        SuperArete curChemin;
        int curArriveeInd = 0;

        while (!pool.isEmpty()) {
            // trouver le plus proche
            //     choper l'indice de curDepartInd dans matAdj
            //     parcourir matAdj[index] et chercher le min -> minNode

//            for (SuperArete[] line : matAdj) {
//                for (SuperArete arete : line) {
//                    if(arete == null) {
//                        System.out.print("nul ");
//                    } else {
//                        System.out.print("okk ");
//                    }
//                }
//                System.out.println();
//            }

            // on trouve le point le plus proche de curDepartInd ou on a une requete a traiter (-> curIdArrivee)
            curChemin = null;
            curArriveeInd = 0;
            for (int i = 0; i < matAdj.length; i++) {
                if (matAdj[curDepartInd][i] != null && (curChemin == null || matAdj[curDepartInd][i].longueur < curChemin.longueur)) {

                    boolean found = false;
                    for (TupleRequete req : pool) {
                        if (req.isDepart && req.getRequete().getPickup().getId() == matAdj[curDepartInd][i].arrivee.getId()) {
                            found = true;
                            break;
                        }
                        if (!req.isDepart && req.getRequete().getDelivery().getId() == matAdj[curDepartInd][i].arrivee.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        curChemin = matAdj[curDepartInd][i];
                        curArriveeInd = i;
                    }

                }
            }

            // calcul du temps de trajet
            long curIDarrivee = curChemin.arrivee.getId();
            float travelDur = 0;
            for (Segment seg : curChemin.chemin) {
                travelDur += seg.getLength();
            }
            travelDur *= 3600.0 / 15000.0; // conversion de metres vers secondes
//            System.out.println("On va aller de " + curChemin.depart.getId() + " à " + curIDarrivee + " en " + travelDur + " s");
            curTime = curTime.plusSeconds((long) travelDur);

            // parcourir pool, pour chaque requete où il faut actuellement aller à minNode :
            //      si c'est un départ : transformer en arrivee
            //      si c'est une arrivee : virer du pool
            LinkedList<TupleRequete> aDelete = new LinkedList<TupleRequete>();
            for (TupleRequete dest : pool) {
                if (dest.isDepart && dest.requete.getPickup().getId() == curIDarrivee) {
                    ptsPassage.add(new TupleRequete(dest.requete, true, curTime, curChemin.chemin));
//                    System.out.println("Pickup, attente de " + dest.requete.getPickupDur() + " s");
                    curTime = curTime.plusSeconds((long) dest.requete.getPickupDur());
                    dest.isDepart = false;
                }
                if (!dest.isDepart && dest.requete.getDelivery().getId() == curIDarrivee) {
                    ptsPassage.add(new TupleRequete(dest.requete, false, curTime, curChemin.chemin));
//                    System.out.println("Delivery, attente de " + dest.requete.getDeliveryDur() + " s");
                    curTime = curTime.plusSeconds((long) dest.requete.getDeliveryDur());
                    aDelete.add(dest);
                }
            }
            pool.removeAll(aDelete);

            boolean stillUsed = false;
            for (TupleRequete dest : pool) {
                if (dest.isDepart && dest.requete.getPickup().getId() == curChemin.depart.getId()) {
                    stillUsed = true;
                    break;
                }
                if (!dest.isDepart && dest.requete.getDelivery().getId() == curChemin.depart.getId()) {
                    stillUsed = true;
                    break;
                }
            }
//            if(!stillUsed) {
//                for (int i = 0; i < matAdj.length; i++) {
//                    matAdj[i][curDepartInd] = null;
//                }
//            }
            // ajouter au chemin
            chemin.addAll(curChemin.chemin);
            // curDepartInd = minNode
            curDepartInd = curArriveeInd;
        }

        // retour au depot
        curChemin = matAdj[curArriveeInd][0];

        long curIDarrivee = curChemin.arrivee.getId();
        float travelDur = 0;
        for (Segment seg : curChemin.chemin) {
            travelDur += seg.getLength();
        }
        travelDur *= 3600.0 / 15000.0; // conversion de metres vers secondes
//        System.out.println("On va aller de " + curChemin.depart.getId() + " au depot " + curIDarrivee + " en " + travelDur + " s");
        curTime = curTime.plusSeconds((long) travelDur);

        chemin.addAll(curChemin.chemin);
        ptsPassage.add(new TupleRequete(new Request(planning.getRequestList().size(), planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), false, curTime, curChemin.chemin));

        Tournee tournee = new Tournee(chemin, requests);
//        Tournee tournee = new Tournee(chemin, requests, ptsPassage);
        recreateTimesTournee(tournee, planning);

        return tournee;
    }

    /**
     * Generates a random Tour that respects the pickup -> delivery order constraints, with the Requests and the Map
     * given as parameters
     * FIXME apparemment passe pas par tous les points nécessaires
     *
     * @param map               The map representing the graph on which we search
     * @param planning          The planning that includes a list of requests to deal with as well as the
     *                          departure address
     * @param intersecIdToIndex The dictionnary giving the index of an Intersection in the Intersection list of the map,
     *                          from its ID
     * @return the computed Tour
     */
    private static Tournee tourneeRandom(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        // dijkstra pour le graphe complet des plus courts chemins entre les points d'intérêt
        SuperArete[][] matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
        // indexation de ces points d'intérêt
        HashMap<Long, Integer> ptsIdToIndex = indexerPtsInteret(planning);

        // ligne à changer en fonction de la méthode choisie si copié-collé
        // ici, chemin aléatoire qui respecte les contraintes de précédence départ -> arrivée
        ArrayList<SuperArete> chemin = cheminAleatoire(matAdj, planning, ptsIdToIndex);

        // construction de l'objet Tournee à partir du résultat obtenu
        Tournee tournee = cheminVersTournee(planning, chemin);
        recreateTimesTournee(tournee, planning);
        return tournee;
    }

    /**
     * Computes a Tour using the Branch and Bound paradigm. The computed tour is optimal : its path is
     * the shortest possible. Execution time may as such be longer than the other heuristics.
     *
     * @param matAdj       The optimal full sub-graph of the map as an adjacence matrix
     * @param planning     The planning that includes a list of requests to deal with as well as the
     *                     departure address
     * @return the computed Tour
     */
    private static Tournee branchAndBoundOpti(SuperArete[][] matAdj, PlanningRequest planning) {

        TSP tsp = new TSP2();
        tsp.searchSolution(20 * 1000, matAdj, planning.getRequestList());
//        System.out.println("Solution trouvee en " + tsp.getExecTime() + " secondes");
        Integer[] solution = tsp.getSolution();

        ArrayList<Segment> chemin = new ArrayList<Segment>();

        for (int i = 1; i < solution.length; i++) {
            if (!solution[i - 1].equals(solution[i])) {
                chemin.addAll(matAdj[solution[i - 1]][solution[i]].getChemin());
            }
        }
        if (!solution[solution.length - 1].equals(0)) {
            chemin.addAll(matAdj[solution[solution.length - 1]][0].getChemin());
        }
        return new Tournee(chemin, planning.getRequestList());
    }

}
