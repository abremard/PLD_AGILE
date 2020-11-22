
package processing;

import objects.Intersection;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;

import java.util.PriorityQueue;

public class processingTest {

    public static void main(String[] args) {

        Map map = new Map("data/smallMap.xml");
        System.out.println("Map chargee, nombre d'intersections : " + map.getNoOfIntersections() + ",   nombre de segments : " + map.getNoOfSegments());

        PlanningRequest listRequests = new PlanningRequest();
        try {
            listRequests.parseRequest("data/requestsSmall2.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Liste de requetes chargee, nombre de requetes : " + listRequests.getRequestList().size());

        Tournee tournee = ComputeTour.planTour(map, listRequests.getRequestList());
        System.out.println("Tournee calculee :");
        System.out.println(tournee.getSegmentList());
    }

}
