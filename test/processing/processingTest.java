
package processing;

import objects.*;

import java.util.PriorityQueue;

public class processingTest {

    public static void main(String[] args) {

        String mapPath = "data/map_test.xml";
        String reqPath = "data/requests_test.xml";

//        String mapPath = "data/largeMap.xml";
//        String reqPath = "data/requestsLarge9.xml";

        Map map = new Map(mapPath);
        System.out.println("Map chargee, nombre d'intersections : " + map.getNoOfIntersections() + ",   nombre de segments : " + map.getNoOfSegments());

        PlanningRequest planning = new PlanningRequest();
        try {
            planning.parseRequest(reqPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Liste de requetes chargee, nombre de requetes : " + planning.getRequestList().size() + "\n");

        Tournee tournee = ComputeTour.planTour(map, planning, Heuristique.RANDOM);
        System.out.println("Tournee calculee :"); // pour map_test.xml, requests_test.xml, tourneeTriviale : S01, S12, S25, S51, S23, S30
        for (Segment seg : tournee.getSegmentList()) {
            System.out.println(seg);
        }

//        SuperArete[][] adjMatrix = ComputeTour.testFullGraph(map, planning);
//        for (SuperArete[] line : adjMatrix) {
//            for (SuperArete arete : line) {
//                System.out.println(arete);
//            }
//            System.out.println();
//        }
        for (TupleRequete req : tournee.getPtsPassage()) {
            if(req.isDepart) {
                System.out.println(req.requete.getPickup().getId() + " " + req.time + " " + req.chemin);
            } else {
                System.out.println(req.requete.getDelivery().getId() + " " + req.time + " " + req.chemin);
            }
        }

    }

}
