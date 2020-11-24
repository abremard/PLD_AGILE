
package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import objects.*;

public class ComputeTour {

    public static Tournee planTour(Map map, PlanningRequest planning) {

        // constructeur : Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur, [LocalTime startTime])
        // constructeur : Segment(int origin, int destination, Float length, String name)
        // constructeur : Tournee(LinkedList<Segment> segmentList, LinkedList<Request> requestList)
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        return tourneeTriviale(map, planning, intersecIdToIndex);
    }

    public static SuperArete[][] testFullGraph(Map map, PlanningRequest planning) {
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        return getOptimalFullGraph(map, planning.getRequestList(), intersecIdToIndex);
    }

    // ----------------------------- Fonctions utilitaires

    /**
     * Dijkstra sur la map donnée en entrée depuis le point de départ vers tous les points d'intérêts (plus les points
     * sur le chemin)
     *
     * @param map        La carte représentant le graphe sur lequel on effectue le parcours
     * @param depart     Le point de départ du parcours
     * @param ptsInteret La liste des points d'intérêt, c'est-à-dire les points vers lesquels on veut connaître le plus
     *                   court chemin depuis le point de départ
     * @return La liste des prédécesseurs de chaque sommet dans le plus court chemin depuis le départ vers tous les
     * points qui sont plus proches que le plus lointain point d'intérêt ,pas forcément pour les autres
     * (arrêt de l'algo quand on a fini de traiter tous les points d'intérêt)
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
                    if(pQueue.contains(voisin)) {
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

    // SuperArete[depart][arrivee]
    private static SuperArete[][] getOptimalFullGraph(Map map, ArrayList<Request> requests, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Intersection> intersections = map.getIntersectionList();

        LinkedList<Intersection> ptsInteret = new LinkedList<Intersection>();
        boolean found;
        for (Request req : requests) {
            found = false;
            for (Intersection node : ptsInteret) {
                if(req.getPickup().getId() == node.getId()){
                    found = true;
                    break;
                }
            }
            if(!found) {
                ptsInteret.add(req.getPickup());
            }

            found = false;
            for (Intersection node : ptsInteret) {
                if(req.getDelivery().getId() == node.getId()){
                    found = true;
                    break;
                }
            }
            if(!found) {
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

        for(int i=0; i<nInodes; i++) {
            ArrayList<Segment> predList = dijkstra(map, ptsInteret.get(i), ptsInteret, intersecIdToIndex);
            for(int j=0; j<nInodes; j++) {
                if(i != j) {
                    ArrayList<Segment> chemin = recreateChemin(predList, ptsInteret.get(i), ptsInteret.get(j), intersections, intersecIdToIndex);
                    adjMatrix[i][j] = new SuperArete(chemin, intersections, intersecIdToIndex);
                }
            }
        }

        return adjMatrix;
    }

    /**
     * Renvoie la liste d'adjacence correspondant au graphe construit à partir des segments & intersections de la map
     * passée en paramètre.
     *
     * @param map               La map à partir de laquelle on construit la liste d'adjacence (plus pratique pour les algos)
     * @param intersecIdToIndex Dictionnaire faisant la correspondance entre ID d'une Intersection et son indice/index
     *                          dans les différentes structures de données indexées par Intersection
     * @return La liste d'adjacence, indexée par indices (obtenus à partir des ID des intersections avec la HashMap
     * passée en paramètre) et contenant pour chaque Intersection la liste des segments ayant pour origine cette
     * Intersection.
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

    private static HashMap<Long, Integer> indexationIntersections(Map map) {
        // dico id -> index dans les tableaux indexés par intersections
        // pour le sens inverse : utiliser le tableau intersections
        HashMap<Long, Integer> intersecIdToIndex = new HashMap<Long, Integer>();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        for(int i = 0; i < map.getNoOfIntersections(); ++i){
            intersecIdToIndex.put(intersections.get(i).getId(), i);
        }

        return intersecIdToIndex;
    }

    private static ArrayList<Segment> recreateChemin(ArrayList<Segment> predList, Intersection depart, Intersection arrivee, ArrayList<Intersection> intersections, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        Intersection curNode = arrivee;

        while(curNode.getId() != depart.getId()) {
            Segment boutChemin = predList.get(intersecIdToIndex.get(curNode.getId()));
            chemin.add(0, boutChemin);
            curNode = intersections.get(intersecIdToIndex.get(boutChemin.getOrigin()));
        }

        return chemin;
    }

    // ----------------------------- Heuristiques

    private static Tournee tourneeTriviale(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        Intersection previousDelivery = planning.getDepot().getAdresse();
        LinkedList<Intersection> ptsInteret;
        ArrayList<Segment> predList;
        for (Request request : planning.getRequestList()) {
            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getPickup());
            predList = dijkstra(map, previousDelivery, ptsInteret, intersecIdToIndex);
            chemin.addAll(recreateChemin(predList, previousDelivery, request.getPickup(), intersections, intersecIdToIndex));

            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getDelivery());
            predList = dijkstra(map, request.getPickup(), ptsInteret, intersecIdToIndex);
            chemin.addAll(recreateChemin(predList, request.getPickup(), request.getDelivery(), intersections, intersecIdToIndex));
            previousDelivery = request.getDelivery();
        }

        // retour au depot
        ptsInteret = new LinkedList<Intersection>();
        ptsInteret.add(planning.getDepot().getAdresse());
        predList = dijkstra(map, previousDelivery, ptsInteret, intersecIdToIndex);
        chemin.addAll(recreateChemin(predList, previousDelivery, planning.getDepot().getAdresse(), intersections, intersecIdToIndex));

        return new Tournee(chemin, planning.getRequestList());
    }

    private static Tournee geneticATSP() {

        return null;
    }
}
