
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

    private static LinkedList<Segment> Dijkstra(Map map, Intersection depart, LinkedList<Intersection> ptsInteret) {

        // --------- indexation des Intersections
        // liste des intersections -> todo changer les linkedlist vers arraylist ou qqch comme ça ?
        Intersection[] intersections = (Intersection[]) map.getIntersectionList().toArray();

        // dico id -> index dans les tableaux indexés par intersections
        // pour le sens inverse : utiliser le tableau intersections
        HashMap<Integer, Integer> intersecIdToIndex = new HashMap<Integer, Integer>();

        for (int i = 0; i < intersections.length; ++i) {
            intersecIdToIndex.put(intersections[i].getId(), i);
        }

        // On recupere la liste d'adjacence
        ArrayList<ArrayList<Segment>> adjList = GetListeAdj(map, intersecIdToIndex);

        // methodes dispo sur la pQueue : add, poll, peek
        PriorityQueue<TupleDijkstra> pQueue  = new PriorityQueue<TupleDijkstra>();
        pQueue.add(new TupleDijkstra(0, depart));

        while(!pQueue.isEmpty()) {
            TupleDijkstra curNoeud = pQueue.poll();
            for (Segment seg : adjList.get(0)) {

            }
        }

        return null;
    }

    private static ArrayList<ArrayList<Segment>> GetListeAdj(Map map, HashMap<Integer, Integer> intersecIdToIndex) {

        ArrayList<ArrayList<Segment>> listeAdj = new ArrayList<>();

        for (Segment segment: map.getSegmentList()) {

        }

        return null;
    }

    // ----------------------------- Heuristiques

    private static Tournee tourneeTriviale(Map map, LinkedList<Request> requestList) {

        return null;
    }

    private static Tournee geneticATSP() {

        return null;
    }
}
