
package processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import objects.*;

/*
* TODO
*  - arrêter Dijsktra quand on a traité tous les points d'intérêt
*  - ajouter dépot dans la matrice d'adj
*  - ajouter les heures de récupération & de dépôt de livraison ?
*  - algo pour le TSP (génétique ?)
* */

public class ComputeTour {

    public static Tournee planTour(Map map, PlanningRequest planning) {

        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);

        // version triviale
        //return tourneeTriviale(map, planning, intersecIdToIndex);

        // version greedy
        SuperArete[][] matAdj = getOptimalFullGraph(map, planning.getRequestList(), intersecIdToIndex);
        return greedy(matAdj, planning, intersecIdToIndex);

        // version genetique
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

    private static float longueurChemin(ArrayList<Segment> chemin) {
        float longueur = 0;
        for (Segment seg: chemin) {
            longueur += seg.getLength();
        }
        return longueur;
    }

    private static ArrayList<SuperArete> cheminAleatoire(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        /* Principe :
         * Initialiser le pool d'intersections à tous les départs des requêtes
         * Choisir un départ de requête au hasard dans le pool et le retirer
         * Initialiser le chemin à la SuperArete [dépôt -> départ choisi]
         * Ajouter l'arrivée de la requête asssociée départ choisi au pool
         * Tant que le pool n'est pas vide :
         *  - Prendre un élément au hasard dans le pool
         *  - Si cet élément est un départ de requête, ajouter son arrivée dans le pool
         *  - Ajouter au chemin la SuperArete [dernière Intersection du chemin -> élément choisi]
         *
         * -> comment représenter le pool ?
         * */
        return null;
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

    private static Tournee geneticATSP(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        return null;
    }

    private static Tournee greedy(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        /* Principe :
        *  - initialiser un pool à la liste des départs
        *  - prendre le départ le plus proche du dépot du pool
        *  - ajouter son arrivée au pool
        *  - tant qu'on a pas fini le chemin (= tant que le pool n'est pas vide)
        *    - prendre le point du pool le plus proche du dernier point choisi
        *    - si c'est un départ, ajouter son arrivée au pool
        */

        ArrayList<Segment> chemin = new ArrayList<Segment>();
        ArrayList<Request> requests = planning.getRequestList();

        LinkedList<TupleRequete> pool = new LinkedList<TupleRequete>();
        for (Request req : requests) {
            pool.add(new TupleRequete(req, true));
        }

        // todo : recup le point le plus proche du depot
        int curDepartInd = 0;
        for(TupleRequete req : pool) {
            if(req.requete.getPickup().getId() == matAdj[curDepartInd][1].depart.getId()) {
                req.isDepart = false;
            }
        }

        while(!pool.isEmpty()) {
            // trouver le plus proche
            //     choper l'indice de curDepartInd dans matAdj
            //     parcourir matAdj[index] et chercher le min -> minNode
            SuperArete curChemin;
            int curArriveeInd = 0;
            curChemin = matAdj[curDepartInd][0]; // point le plus proche de curDepartInd
            for (int i = 0; i < matAdj.length; i++) {
                if(curChemin == null || (matAdj[curDepartInd][i] != null && matAdj[curDepartInd][i].longueur < curChemin.longueur)) {
                    curChemin = matAdj[curDepartInd][i];
                    curArriveeInd = i;
                }
            }
            System.out.println("On va aller de " + curDepartInd + " à " + curArriveeInd);
            // parcourir pool, pour chaque requete où il faut actuellement aller à minNode :
            //      si c'est un départ : transformer en arrivee
            //      si c'est une arrivee : virer du pool
            long currIDarrivee = curChemin.arrivee.getId();
            LinkedList<TupleRequete> aDelete = new LinkedList<TupleRequete>();
            for (TupleRequete dest : pool) {
                if(dest.isDepart && dest.requete.getPickup().getId() == currIDarrivee) {
                    dest.isDepart = false;
                }
                if(!dest.isDepart && dest.requete.getDelivery().getId() == currIDarrivee) {
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
            }
            if(!stillUsed) {
                for (int i = 0; i < matAdj.length; i++) {
                    matAdj[i][curDepartInd] = null;
                }
            }
            // ajouter au chemin
            chemin.addAll(curChemin.chemin);
            // curDepartInd = minNode
            curDepartInd = curArriveeInd;
        }

        return new Tournee(chemin, requests);
    }

}
