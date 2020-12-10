
package processing;

import java.time.LocalTime;
import java.util.*;

import Branch_And_Bound_TSP.TSP;
import Branch_And_Bound_TSP.TSP1;
import Branch_And_Bound_TSP.TSP2;
import Branch_And_Bound_TSP.TSP3;
import Branch_And_Bound_TSP.TSP4;
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
 *  - B&B : pas optimal ?
 *  - random passe pas par tous les points :'( (notamment largeMap + requestsLarge9)
 * */

/**
 * Classe regroupant les algorithmes de calcul de tournée.
 * Utiliser planTour() pour récupérer une tournée.
 *
 * @author H4302
 * @see command.ComputeTourCommand
 */
public class ComputeTour {

    static boolean verbose = false;

    /**
     * Fonction principale de ComputeTour, permet de calculer une tournée d'après les informations des fichiers XML
     * de map et de liste de requêtes, ainsi que d'un choix d'heuristique.
     *
     * @param map         La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning    Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param heuristique L'heuristique à utiliser (conditionne la qualité du résultat et la vitesse d'exécution)
     * @return la tournée calculée
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
     * Calcule la tournée passant par les points de récupération et de dépôt dans un ordre spécifique.
     *
     * @param map      La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param order    Les points par lesquelles la tournée doit passer, dans l'ordre chronologique.
     * @return la tournée calculée
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
     * points qui sont plus proches que le plus lointain point d'intérêt, pas forcément pour les autres
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
     * Calcule le graphe complet dont les sommets sont les points où il y a un pickup ou une delivery,
     * ainsi que le dépôt (points d'intérêt), et dont les arêtes sont les plus courts chemins sur la map entre ces
     * points d'intérêt.
     *
     * @param map               La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning          Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return le graphe complet sous forme d'une matrice d'adjacence. Y accéder aux indices [depart][arrivee] donne
     * l'objet SuperArete dont l'attribut chemin est le plus court chemin (en tant que liste de Segments de la map)
     * en allant du point de depart vers le point d'arrivée.
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
     * Renvoie la liste d'adjacence correspondant au graphe construit à partir des segments & intersections de la map
     * passée en paramètre.
     *
     * @param map               La carte représentant le graphe sur lequel on effectue le parcours
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
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

    /**
     * Crée un dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de la map,
     * à partir de son ID.
     *
     * @param map La carte représentant le graphe sur lequel on effectue le parcours
     * @return Le dictionnaire
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
     * Recree un chemin (en tant que liste de Segments) a partir d'une liste de predecesseurs,
     * d'un depart et d'une arrivee.
     *
     * @param predList          La liste d'arêtes prédécesseures : à chaque indice, le Segment menant du point précédent
     *                          vers ce point
     * @param depart            L'intersection au départ du chemin
     * @param arrivee           L'intersection à l'arrivée du chemin
     * @param intersections     La liste d'intersections de la map
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return le chemin du départ à l'arrivée
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
     * Calcule les heures d'arrivée à chaque point de pickup/delivery, ainsi que les fragments de chemins associés.
     * Utilisé lorsque le traitement de calcul de la tournée serait plus compliqué si il fallait calculer
     * ces attributs en même temps.
     *
     * @param tournee  La tournée à modifier (pas de return, modifie directement le paramètre)
     * @param planning Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
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
                    if(verbose) System.out.println("Pickup a " + req.getRequete().getPickup().getId() + " pendant " + req.getRequete().getPickupDur());
                    ptsPassage.add(new TupleRequete(req.getRequete(), true, curTime, curChemin));
                    found = true;
                }
                if (!req.isDepart && req.requete.getDelivery().getId() == seg.getDestination()) {
                    aDelete.add(req);
                    curTime = curTime.plusSeconds((long) (req.getRequete().getDeliveryDur()));
                    if(verbose) System.out.println("Delivery a " + req.getRequete().getDelivery().getId() + " pendant " + req.getRequete().getDeliveryDur());
                    ptsPassage.add(new TupleRequete(req.getRequete(), false, curTime, curChemin));
                    found = true;
                }
            }
            pool.removeAll(aDelete);
            if (found) {
                if(verbose) System.out.println("On va de " + curChemin.get(0).getOrigin() + " a " + curChemin.get(curChemin.size() - 1).getDestination());
                curChemin = new ArrayList<Segment>();
            }
        }

        tournee.setPtsPassage(ptsPassage);
    }

    private static float longueurChemin(ArrayList<Segment> chemin) {
        float longueur = 0;
        for (Segment seg : chemin) {
            longueur += seg.getLength();
        }
        return longueur;
    }

    /**
     * Choisit un élément aléatoire dans une ArrayList de type quelconque
     *
     * @param arrayList La liste dans laquelle l'élément est choisi
     * @return L'élément de la liste choisi
     */
    private static <T> T pickRandom(ArrayList<T> arrayList) {
        int rnd = new Random().nextInt(arrayList.size());
        return arrayList.get(rnd);
    }

    /**
     * Indexe les points de départ, d'arrivée de chaque requête + le dépôt de la façon suivante :
     * * indice 0 : dépot
     * * indices 1 à nb de pts d'intérêt : chaque point d'intérêt hors dépôt, indexé selon ptsIdToIndex
     * FIXME pas sûr que ça marche avec les points confondus
     */
    private static HashMap<Long, Integer> indexerPtsInteret(PlanningRequest planningRequest) {
        HashMap<Long, Integer> ptsIdToIndex = new HashMap<>();

        ptsIdToIndex.put(planningRequest.getDepot().getAdresse().getId(), 0);

        int index = 1;
        for (Request req : planningRequest.getRequestList()) {
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
     *
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
     * Crée un chemin aléatoire valide, c'est-à-dire qui passe par tous les points de pickup et de delivery,
     * et que tous les pickups sont réalisés avant les deliveries associées.
     *
     * @param matAdj       Le sous-graphe complet optimal de la map sous forme d'une matrice d'adjacence
     * @param planning     Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param ptsIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la matrice d'adjacence du
     *                     sous-graphe optimal, à partir de son ID
     * @return le chemin sous forme de liste de SuperAretes, représentant chacune le chemin entre deux points d'intérêt
     * consécutifs.
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

    // ----------------------------- Heuristiques

    /**
     * Calcule une tournée de la manière la plus rapide, en reliant de manière itérative le pickup d'une requête
     * avec sa delivery, puis avec le pickup de la requête suivante.
     *
     * @param map               La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning          Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return la tournée calculée
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
     * @param matAdj   Le sous-graphe complet optimal de la map sous forme d'une matrice d'adjacence
     * @param planning Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @return la tournée calculée
     */
    private static Tournee geneticATSP(SuperArete[][] matAdj, PlanningRequest planning) {
        HashMap<Long, Integer> ptIdToIndex = new HashMap<>();       // !! pas les mêmes index qu'avant
        return null;
    }

    /**
     * Calcule une tournée en utilisant un algorithme glouton, qui se dirige à chaque itération vers le point
     * d'intérêt restant le plus proche de la position actuelle.
     *
     * @param matAdj            Le sous-graphe complet optimal de la map sous forme d'une matrice d'adjacence
     * @param planning          Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return la tournée calculée
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
     * Génère une tournée aléatoire respectant les contraintes d'ordre pickup -> delivery avec les requêtes et la map*
     * passées en paramètre
     * FIXME apparemment passe pas par tous les points nécessaires
     *
     * @param map               La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning          Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return la tournée calculée
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
     * @param map               La carte représentant le graphe sur lequel on effectue le parcours
     * @param planning          Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @param intersecIdToIndex Le dictionnaire donnant l'indice d'une intersection dans la liste d'intersections de
     *                          la map, à partir de son ID
     * @return la tournée calculée
     */
    private static Tournee tourneePaper(Map map, PlanningRequest planning, HashMap<Long, Integer> intersecIdToIndex) {

        // dijkstra pour le graphe complet des plus courts chemins entre les points d'intérêt
        SuperArete[][] matAdj = getOptimalFullGraph(map, planning, intersecIdToIndex);
        // indexation de ces points d'intérêt
        HashMap<Long, Integer> ptsIdToIndex = indexerPtsInteret(planning);

        PaperHeuristicTSP heuristicTSP = new PaperHeuristicTSP(matAdj, planning, ptsIdToIndex);

        // TODO
        return null;
    }

    /**
     * Calcule une tournée en utilisant le principe du Branch and Bound. La tournée calculée est optimale : son chemin
     * est le plus court possible. Le temps d'exécution peut en revanche être plus long que pour les autres heuristiques.
     *
     * @param matAdj   Le sous-graphe complet optimal de la map sous forme d'une matrice d'adjacence
     * @param planning Le planning contenant la liste des requêtes à traiter ainsi que le dépôt
     * @return la tournée calculée
     */
    private static Tournee branchAndBoundOpti(SuperArete[][] matAdj, PlanningRequest planning) {

        TSP tsp = new TSP4();
        tsp.searchSolution(20000, matAdj, planning.getRequestList());
//        System.out.println("Solution trouvee en " + tsp.getExecTime() + " secondes");
        Integer[] solution = tsp.getSolution();

        ArrayList<Segment> chemin = new ArrayList<Segment>();

        for (int i = 1; i < solution.length; i++) {
            chemin.addAll(matAdj[solution[i - 1]][solution[i]].getChemin());
        }
        chemin.addAll(matAdj[solution[solution.length - 1]][0].getChemin());
        return new Tournee(chemin, planning.getRequestList());
    }

}
