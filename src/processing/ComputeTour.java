
package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import objects.*;

public class ComputeTour {

    public static Tournee planTour(Map map, ArrayList<Request> requestList) {

        // constructeur : Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur, [LocalTime startTime])
        // constructeur : Segment(int origin, int destination, Float length, String name)
        // constructeur : Tournee(LinkedList<Segment> segmentList, LinkedList<Request> requestList)
        return tourneeTriviale(map, requestList);
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
    private static ArrayList<Segment> dijkstra(Map map, Intersection depart, LinkedList<Intersection> ptsInteret) {

        // --------- indexation des Intersections
        // liste des intersections -> todo changer les linkedlist vers arraylist ou qqch comme ça ?
        ArrayList<Intersection> intersections = map.getIntersectionList();

        // dico id -> index dans les tableaux indexés par intersections
        // pour le sens inverse : utiliser le tableau intersections
        HashMap<Long, Integer> intersecIdToIndex = new HashMap<Long, Integer>();

        for (int i = 0; i < map.getNoOfIntersections(); ++i) {
            intersecIdToIndex.put(intersections.get(i).getId(), i);
        }

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
                    dist.get(indexArrivee).distance = curNoeud.distance + seg.getLength();      // màj distance
                    pred.set(indexArrivee, seg);            // màj prédécesseur

                    if (dist.get(indexArrivee).color == TupleDijkstra.Color.WHITE) {            // noeud pas encore traité
                        pQueue.add(dist.get(indexArrivee));
                        dist.get(indexArrivee).color = TupleDijkstra.Color.GREY;
                    }
                }
            } // fin du parcours des arcs sortants
        }

        return pred;
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

    // ----------------------------- Heuristiques

    private static Tournee tourneeTriviale(Map map, ArrayList<Request> requestList) {
        ArrayList<Segment> chemin = new ArrayList<Segment>();

        Intersection previousDelivery = null;
        LinkedList<Intersection> ptsInteret;
        for (Request request : requestList) {
            if (previousDelivery != null) {
                ptsInteret = new LinkedList<Intersection>();
                ptsInteret.add(request.getPickup());
                ArrayList<Segment> boutChemin = dijkstra(map, previousDelivery, ptsInteret);
                chemin.addAll(boutChemin);
            }
            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getDelivery());
            ArrayList<Segment> boutChemin = dijkstra(map, request.getPickup(), ptsInteret);
            chemin.addAll(boutChemin);
            previousDelivery = request.getDelivery();
        }

        return new Tournee(chemin, requestList);
    }

    private static Tournee geneticATSP() {

        return null;
    }
}
