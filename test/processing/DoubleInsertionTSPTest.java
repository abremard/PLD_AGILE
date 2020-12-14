package processing;

import objects.Map;
import objects.PlanningRequest;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Basic DoubleInsertionTSP Tester.
 *
 * @author Emma Neiss
 * @version 1.0
 * @since <pre>Dec. 14, 2020</pre>
 */
public class DoubleInsertionTSPTest {


    /**
     * Method: doubleInsertionHeuristic()
     * <p>
     * Uses a basic dataset.
     */
    @Test
    public void testDoubleInsertionHeuristicNormalLarge() throws Exception {

        // initialize necessary objects
        PlanningRequest planningRequest = new PlanningRequest();
        Map map = new Map("data/largeMap.xml");

        try {
            planningRequest.parseRequest("data/requestsLarge9.xml");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // compute a solution and build a Tournee object with it
        ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION);
    }

    /**
     * Method: doubleInsertionHeuristic()
     * <p>
     * Checks for computability of a solution on a very large set of points (40
     * pickup/delivery points).
     */
    @Test
    public void testDoubleInsertionHeuristicVeryLarge() throws Exception {

        // initialize necessary objects
        PlanningRequest planningRequest = new PlanningRequest();
        Map map = new Map("data/largeMap.xml");

        try {
            planningRequest.parseRequest("data/requestsLarge-veryLarge.xml");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // compute a solution and build a Tournee object with it
        ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION);
    }

    /**
     * Method: doubleInsertionHeuristic()
     * <p>
     * Checks for computability of a solution where some points appear more than
     * once in the requests' points.
     */
    @Test
    public void testDoubleInsertionHeuristicDuplicatedPoints() throws Exception {

        // initialize necessary objects
        PlanningRequest planningRequest = new PlanningRequest();
        Map map = new Map("data/largeMap.xml");

        try {
            planningRequest.parseRequest("data/requestsLarge-dupesTest.xml");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // compute a solution and build a Tournee object with it
        ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION);
    }

} 
