
package processing;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

import Branch_And_Bound_TSP.TSP;
import Branch_And_Bound_TSP.TSP1;
import objects.*;
import objects.Map;

/*
* TODO
*  - arrêter Dijsktra quand on a traité tous les points d'intérêt
*  - algo pour le TSP (génétique ?)
*  - branch and bound
*
* FIXME
*  - random passe pas par tous les points :'(
*  - largeMap + requestsLarge9 + heuristique random -> bug
* */

public class ComputeTour {

    public static Tournee planTour(Map map, PlanningRequest planning, Heuristique heuristique) {

        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        SuperArete[][] matAdj;

        switch (heuristique) {
            case TRIVIALE :
                // version triviale
                return tourneeTriviale(map, planning, intersecIdToIndex);
            case RANDOM :
                // version random
                return tourneeRandom(map, planning, intersecIdToIndex);
            case GREEDY :
                // version greedy
                matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
                return greedy(matAdj, planning, intersecIdToIndex);
            case GENETIQUE :
                // version genetique
                return null;
            case BRANCHANDBOUND :
                // version greedy
                matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
                return branchAndBoundOpti(matAdj, planning, intersecIdToIndex);
        }

        return null;
    }

    public static SuperArete[][] testFullGraph(Map map, PlanningRequest planning) {
        HashMap<Long, Integer> intersecIdToIndex = indexationIntersections(map);
        return getOptimalFullGraph(map, planning, intersecIdToIndex);
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
    private static SuperArete[][] getOptimalFullGraph(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {
        ArrayList<Request> requests = planning.getRequestList();
        ArrayList<Intersection> intersections = map.getIntersectionList();

        LinkedList<Intersection> ptsInteret = new LinkedList<Intersection>();
        ptsInteret.add(planning.getDepot().getAdresse());
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

    private static void recreateTimesTournee(Tournee tournee, PlanningRequest planning) {
        LocalTime curTime = LocalTime.now();
        ArrayList<Segment> curChemin = new ArrayList<Segment>();
        // pool
        LinkedList<TupleRequete> pool = new LinkedList<TupleRequete>();
        for (Request req : planning.getRequestList()) {
            pool.add(new TupleRequete(req, true));
        }
        pool.add(new TupleRequete(new Request(planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), true));

        ArrayList<TupleRequete> ptsPassage = new ArrayList<TupleRequete>();

        for (Segment seg : tournee.getSegmentList()) {
            curTime = curTime.plusSeconds((long)(seg.getLength()*3600/15000.0));
            curChemin.add(seg);

            LinkedList<TupleRequete> aDelete = new LinkedList<TupleRequete>();
            for (TupleRequete req : pool) {
                if(req.isDepart && req.requete.getPickup().getId() == seg.getDestination()) {
                    req.isDepart = false;
                    ptsPassage.add(new TupleRequete(req.getRequete(), true, curTime, curChemin));
                    curChemin = new ArrayList<Segment>();
                }
                if(!req.isDepart && req.requete.getDelivery().getId() == seg.getDestination()) {
                    aDelete.add(req);
                    ptsPassage.add(new TupleRequete(req.getRequete(), false, curTime, curChemin));
                    curChemin = new ArrayList<Segment>();
                }
            }
            pool.removeAll(aDelete);
        }

        tournee.setPtsPassage(ptsPassage);
    }

    private static float longueurChemin(ArrayList<Segment> chemin) {
        float longueur = 0;
        for (Segment seg: chemin) {
            longueur += seg.getLength();
        }
        return longueur;
    }

    /**
     * Choisit un élément aléatoire dans une ArrayList de type quelconque
     * @param arrayList La liste dans laquelle l'élément est choisi
     * @return L'élément de la liste choisi
     */
    private static <T> T pickRandom(ArrayList<T> arrayList) {
        int rnd = new Random().nextInt(arrayList.size());
        return arrayList.get(rnd);
    }

    /**
     * Indexe les points de départ, d'arrivée de chaque requête + le dépôt de la façon suivante :
     *      * indice 0 : dépot
     *      * indices 1 à nb de pts d'intérêt : chaque point d'intérêt hors dépôt, indexé selon ptsIdToIndex
     * FIXME pas sûr que ça marche avec les points confondus
     * */
    private static HashMap<Long, Integer> indexerPtsInteret(PlanningRequest planningRequest) {
        HashMap<Long, Integer> ptsIdToIndex = new HashMap<>();

        ptsIdToIndex.put(planningRequest.getDepot().getAdresse().getId(), 0);

        int index = 1;
        for (Request req: planningRequest.getRequestList()) {
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
     * Reconstruit un objet Tournee à partir d'un chemin entier et des requêtes demandées
     * @param chemin Le chemin à utiliser pour la tournée
     * @return La tournée qui en résulte
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
     * Contrat : matAdj est indexé de la façon suivante :
     * indice 0 : dépot
     * indices 1 à nb de pts d'intérêt : chaque point d'intérêt hors dépôt, indexé selon ptsIdToIndex
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
        for (Request req: planning.getRequestList()) {
            pool.add(new TupleRequete(req, true));
        }

        TupleRequete pick = pickRandom(pool);

        // initialisation du chemin
        ArrayList<SuperArete> chemin = new ArrayList<>();
        int indexDernierPt = 0;
        int indexActuel = ptsIdToIndex.get(pick.requete.getPickup().getId());
        // ajout de la nouvelle portion de chemin
        if (indexActuel != indexDernierPt) {
            if (matAdj[indexDernierPt][indexActuel] != null){
                chemin.add(matAdj[indexDernierPt][indexActuel]);  // dépôt -> premier départ
            } else {
                System.err.println("null found where it should not be ! (" + indexDernierPt + " -> " + indexActuel + ")");
            }
        } else {
            System.err.println("Path with identical pickup & delivery ignored");
        }
        pick.isDepart = false;

        // debug
        if (matAdj[indexDernierPt][indexActuel] == null){
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
                if (matAdj[indexDernierPt][indexActuel] != null){
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
            if (matAdj[indexDernierPt][indexActuel] != null){
                chemin.add(matAdj[indexDernierPt][indexActuel]);  // dépôt -> premier départ
            } else {
                System.err.println("null found where it should not be ! (" + indexDernierPt + " -> " + indexActuel + ")");
            }
        } else {
            System.err.println("Path with identical pickup & delivery ignored");
        }

        return chemin;
    }

    // ----------------------------- Heuristiques

    private static Tournee tourneeTriviale(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {
        LocalTime curTime = LocalTime.now();
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
            travelDur *= 3600.0/15000.0; // conversion de metres vers secondes
            curTime = curTime.plusSeconds((long)travelDur);

            ptsPassage.add(new TupleRequete(request, true, curTime, curChemin));

            curTime = curTime.plusSeconds((long)request.getPickupDur());


            ptsInteret = new LinkedList<Intersection>();
            ptsInteret.add(request.getDelivery());
            predList = dijkstra(map, request.getPickup(), ptsInteret, intersecIdToIndex);
            curChemin = recreateChemin(predList, request.getPickup(), request.getDelivery(), intersections, intersecIdToIndex);
            chemin.addAll(curChemin);

            travelDur = 0;
            for (Segment seg : curChemin) {
                travelDur += seg.getLength();
            }
            travelDur *= 3600.0/15000.0; // conversion de metres vers secondes
            curTime = curTime.plusSeconds((long)travelDur);

            ptsPassage.add(new TupleRequete(request, false, curTime, curChemin));

            curTime = curTime.plusSeconds((long)request.getPickupDur());

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
        travelDur *= 3600.0/15000.0; // conversion de metres vers secondes
        curTime = curTime.plusSeconds((long)travelDur);
        chemin.addAll(lastChemin);
        ptsPassage.add(new TupleRequete(new Request(previousDelivery, planning.getDepot().getAdresse(), 0, 0), false, curTime, lastChemin));

        return new Tournee(chemin, planning.getRequestList(), ptsPassage);
    }

    private static Tournee geneticATSP(SuperArete[][] matAdj, PlanningRequest planning) {
        HashMap<Long, Integer> ptIdToIndex = new HashMap<>();       // !! pas les mêmes index qu'avant
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

        LocalTime curTime = LocalTime.now();
        ArrayList<Segment> chemin = new ArrayList<Segment>();
        ArrayList<TupleRequete> ptsPassage = new ArrayList<TupleRequete>();
        ArrayList<Request> requests = planning.getRequestList();

        LinkedList<TupleRequete> pool = new LinkedList<TupleRequete>();
        for (Request req : requests) {
            pool.add(new TupleRequete(req, true));
        }
        //pool.add(new TupleRequete(new Request(planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), true));

        int curDepartInd = 0;
        for(TupleRequete req : pool) {
            if(req.requete.getPickup().getId() == matAdj[curDepartInd][1].depart.getId()) {
                System.out.println("Pickup, attente de " + req.requete.getPickupDur() + " s");
                curTime = curTime.plusSeconds((long)req.requete.getPickupDur());
                req.isDepart = false;
            }
        }
        SuperArete curChemin;
        int curArriveeInd = 0;

        while(!pool.isEmpty()) {
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
                if(matAdj[curDepartInd][i] != null && (curChemin == null || matAdj[curDepartInd][i].longueur < curChemin.longueur)) {

                    boolean found = false;
                    for (TupleRequete req : pool) {
                        if(req.isDepart && req.getRequete().getPickup().getId() == matAdj[curDepartInd][i].arrivee.getId()) {
                            found = true;
                            break;
                        }
                        if(!req.isDepart && req.getRequete().getDelivery().getId() == matAdj[curDepartInd][i].arrivee.getId()) {
                            found = true;
                            break;
                        }
                    }
                    if(found) {
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
            travelDur *= 3600.0/15000.0; // conversion de metres vers secondes
            System.out.println("On va aller de " + curChemin.depart.getId() + " à " + curIDarrivee + " en " + travelDur + " s");
            curTime = curTime.plusSeconds((long)travelDur);

            // parcourir pool, pour chaque requete où il faut actuellement aller à minNode :
            //      si c'est un départ : transformer en arrivee
            //      si c'est une arrivee : virer du pool
            LinkedList<TupleRequete> aDelete = new LinkedList<TupleRequete>();
            for (TupleRequete dest : pool) {
                if(dest.isDepart && dest.requete.getPickup().getId() == curIDarrivee) {
                    ptsPassage.add(new TupleRequete(dest.requete, true, curTime, curChemin.chemin));
                    System.out.println("Pickup, attente de " + dest.requete.getPickupDur() + " s");
                    curTime = curTime.plusSeconds((long)dest.requete.getPickupDur());
                    dest.isDepart = false;
                }
                if(!dest.isDepart && dest.requete.getDelivery().getId() == curIDarrivee) {
                    ptsPassage.add(new TupleRequete(dest.requete, false, curTime, curChemin.chemin));
                    System.out.println("Delivery, attente de " + dest.requete.getDeliveryDur() + " s");
                    curTime = curTime.plusSeconds((long)dest.requete.getDeliveryDur());
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
        travelDur *= 3600.0/15000.0; // conversion de metres vers secondes
        System.out.println("On va aller de " + curChemin.depart.getId() + " au depot " + curIDarrivee + " en " + travelDur + " s");
        curTime = curTime.plusSeconds((long)travelDur);

        chemin.addAll(curChemin.chemin);
        ptsPassage.add(new TupleRequete(new Request(planning.getDepot().getAdresse(), planning.getDepot().getAdresse(), 0, 0), false, curTime, curChemin.chemin));

        return new Tournee(chemin, requests, ptsPassage);
    }

    /**
     * Génère une tournée aléatoire respectant les contraintes d'ordre pickup -> delivery avec les requêtes et la map*
     * passées en paramètre
     * FIXME apparemment passe pas par tous les points nécessaires
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

    private static Tournee tourneePaper(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        // dijkstra pour le graphe complet des plus courts chemins entre les points d'intérêt
        SuperArete[][] matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
        // indexation de ces points d'intérêt
        HashMap<Long, Integer> ptsIdToIndex = indexerPtsInteret(planning);

        PaperHeuristicTSP heuristicTSP = new PaperHeuristicTSP(matAdj, planning, ptsIdToIndex);

        // TODO
		return null;
	}

    private static Tournee branchAndBoundOpti(SuperArete[][] matAdj, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        TSP tsp = new TSP1();
        tsp.searchSolution(20000, matAdj);

        for (int ind : tsp.getSolution()) {
            if(ind == 0) {
                System.out.println(matAdj[ind][1].depart.getId());
            } else {
                System.out.println(matAdj[ind][0].depart.getId());
            }
        }

        return null;
    }

}
