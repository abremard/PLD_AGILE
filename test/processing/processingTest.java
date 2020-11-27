
package processing;

import objects.*;

import java.util.PriorityQueue;

public class processingTest {

    public static void main(String[] args) {

        Map map = new Map("data/map_test.xml");
        System.out.println("Map chargee, nombre d'intersections : " + map.getNoOfIntersections() + ",   nombre de segments : " + map.getNoOfSegments());

        PlanningRequest planning = new PlanningRequest();
        try {
            planning.parseRequest("data/requests_test.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Liste de requetes chargee, nombre de requetes : " + planning.getRequestList().size() + "\n");

        Tournee tournee = ComputeTour.planTour(map, planning, Heuristique.GREEDY);
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


    }

}
