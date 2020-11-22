
package processing;

import objects.*;

import java.util.PriorityQueue;

public class processingTest {

    public static void main(String[] args) {

        Map map = new Map("data/map_test.xml");
        System.out.println("Map chargee, nombre d'intersections : " + map.getNoOfIntersections() + ",   nombre de segments : " + map.getNoOfSegments());

        PlanningRequest listRequests = new PlanningRequest();
        try {
            listRequests.parseRequest("data/requests_test.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Liste de requetes chargee, nombre de requetes : " + listRequests.getRequestList().size());

        Tournee tournee = ComputeTour.planTour(map, listRequests.getRequestList());
        System.out.println("Tournee calculee :");
        for (Segment seg : tournee.getSegmentList()) {
            System.out.println(seg);
        }
    }

}
