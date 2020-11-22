
package processing;

import objects.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class ComputeTour {

    public static Tournee planTour(Map map, LinkedList<Request> requestList) {

        // constructeur : Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur, [LocalTime startTime])
        // constructeur : Segment(int origin, int destination, Float length, String name)
        // constructeur : Tournee(LinkedList<Segment> segmentList, LinkedList<Request> requestList)
        return null;
    }

    // ----------------------------- Fonctions utilitaires

    private static LinkedList<Segment> Dijkstra(Map map, Intersection depart, LinkedList<Intersection> ptsInteret) {

        // indexation des Intersections
        // todo changer linkedlist vers arraylist :(

        for (int i = 0; i < map.getIntersectionList().toArray().length; ++i) {

        }

        // On recupere la liste d'adjacence
        ArrayList<ArrayList<Segment>> adjList = new ArrayList<ArrayList<Segment>>();

        // add, poll, peek
        PriorityQueue<TupleDijkstra> pQueue  = new PriorityQueue<TupleDijkstra>();
        pQueue.add(new TupleDijkstra(0, depart));

        while(!pQueue.isEmpty()) {
            TupleDijkstra curNoeud = pQueue.poll();
            for (Segment seg : adjList.get(0)) {

            }
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
