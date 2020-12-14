
package Branch_And_Bound_TSP;

import objects.Map;
import objects.PlanningRequest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import processing.ComputeTour;
import processing.Heuristique;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Branch_And_Bound_TSP Tester.
 *
 * @author H3402
 * @see ComputeTour
 */
public class Branch_And_Bound_TSP_Test {

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void testBnB() {

        // initialize necessary objects
        PlanningRequest planningRequest = new PlanningRequest();
        Map map = new Map("data/mediumMap.xml");

        try {
            planningRequest.parseRequest("data/requestsMedium5.xml");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        // compute a solution and build a Tournee object with it
        Assert.assertNotNull(ComputeTour.planTour(map, planningRequest, Heuristique.BRANCHANDBOUND));
    }

}
