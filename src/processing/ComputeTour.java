
package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import objects.*;

public class ComputeTour {

    public static Tournee planTour(Map map, LinkedList<Request> requestList) {

        // constructeur : Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur, [LocalTime startTime])
        // constructeur : Segment(int origin, int destination, Float length, String name)
        // constructeur : Tournee(LinkedList<Segment> segmentList, LinkedList<Request> requestList)
        return null;
    }

    // ----------------------------- Fonctions utilitaires

    private static LinkedList<Segment> dijkstra(Map map, Intersection depart, LinkedList<Intersection> ptsInteret) {

        // --------- indexation des Intersections
        // liste des intersections -> todo changer les linkedlist vers arraylist ou qqch comme ça ?
        Intersection[] intersections = (Intersection[]) map.getIntersectionList().toArray();

        // dico id -> index dans les tableaux indexés par intersections
        // pour le sens inverse : utiliser le tableau intersections
        HashMap<Integer, Integer> intersecIdToIndex = new HashMap<Integer, Integer>();

        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            intersecIdToIndex.put(intersections[i].getId(), i);
        }

        // On recupere la liste d'adjacence
        ArrayList<ArrayList<Segment>> adjList = getListeAdj(map, intersecIdToIndex);


        // tableau des distances
        ArrayList<Float> dist = new ArrayList<>(map.getNoOfIntersections());
        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            dist.add((float) -1);
        }
        dist.set(intersecIdToIndex.get(depart.getId()), (float) 0);     // initialisation de la distance de la premiere Intersection

        // methodes dispo sur la pQueue : add, poll, peek
        PriorityQueue<TupleDijkstra> pQueue = new PriorityQueue<TupleDijkstra>();
        pQueue.add(new TupleDijkstra(0, depart));       // initialisation de la pQueue

        while (!pQueue.isEmpty()) {

            TupleDijkstra curNoeud = pQueue.poll();
            int curIndex = intersecIdToIndex.get(curNoeud.intersection.getId());

            for (Segment seg : adjList.get(curIndex)) {     // parcours des voisins/arcs sortants
                int indexArrivee = intersecIdToIndex.get(seg.getDestination());

            }
        }

        return null;
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
    private static ArrayList<ArrayList<Segment>> getListeAdj(Map map, HashMap<Integer, Integer> intersecIdToIndex) {

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

    // ----------------------------- Heuristiques

    private static Tournee tourneeTriviale(Map map, LinkedList<Request> requestList) {
        LinkedList<Segment> chemin = new LinkedList<Segment>();

        Intersection previousDelivery = null;
        LinkedList<Intersection> ptsInteret;
        for(Request request : requestList) {
            if(previousDelivery != null) {
                ptsInteret = new LinkedList<Intersection>();
                ptsInteret.add(request.getPickup());
                LinkedList<Segment> boutChemin = dijkstra(map, previousDelivery, ptsInteret);
                chemin.addAll(boutChemin);
            }
            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getDelivery());
            LinkedList<Segment> boutChemin = dijkstra(map, request.getPickup(), ptsInteret);
            chemin.addAll(boutChemin);
            previousDelivery = request.getDelivery();
        }

        return new Tournee(chemin, requestList);
    }

    private static Tournee geneticATSP() {

        return null;
    }
}
