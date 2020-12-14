package objects;

import objects.*;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class PlanningRequestTest {

    @Test
    public void testSetRequestList() throws IOException, SAXException, ParserConfigurationException {
        PlanningRequest planningRequestParseTest1 = new PlanningRequest();
        PlanningRequest planningRequestParseTest2 = new PlanningRequest();
        planningRequestParseTest1.parseRequest("data/requestsMedium3.xml");
        planningRequestParseTest2.parseRequest("data/requestsMedium5.xml");
        planningRequestParseTest1.setRequestList(planningRequestParseTest2.getRequestList());
        Assert.assertEquals(planningRequestParseTest1.getRequestList(),planningRequestParseTest2.getRequestList());
    }
    @Test
    public void testRemoveAndModifyRequest() throws IOException, SAXException, ParserConfigurationException {
        PlanningRequest planningRequestParseTest1 = new PlanningRequest();
        planningRequestParseTest1.parseRequest("data/requestsLarge7.xml");
        int requestListSize = planningRequestParseTest1.getRequestList().size();
        planningRequestParseTest1.removeRequest(requestListSize - 1);
        Assert.assertEquals(requestListSize - 1, planningRequestParseTest1.getRequestList().size());
        Request requestToBeRemoved = planningRequestParseTest1.getRequestList().get(1);
        planningRequestParseTest1.removeRequest(requestToBeRemoved);
        int indexFound = planningRequestParseTest1.findIndexOfRequest(requestToBeRemoved);
        Assert.assertFalse(planningRequestParseTest1.removeRequest(requestToBeRemoved));
        Assert.assertEquals(-1,indexFound);
        Request requestToBeAdded = requestToBeRemoved;
        Request requestToBeRemoved2 = planningRequestParseTest1.getRequestList().get(2);
        planningRequestParseTest1.modifyRequest(requestToBeRemoved2,requestToBeAdded);
        Assert.assertEquals(-1,planningRequestParseTest1.findIndexOfRequest(requestToBeRemoved2));
        Assert.assertNotEquals(-1,planningRequestParseTest1.findIndexOfRequest(requestToBeAdded));
    }

    @Test
    public void testEquals()throws IOException, SAXException, ParserConfigurationException {
        PlanningRequest planningRequestParseTest1 = new PlanningRequest();
        PlanningRequest planningRequestParseTest2 = new PlanningRequest();
        planningRequestParseTest1.parseRequest("data/requestsMedium3.xml");
        planningRequestParseTest2.parseRequest("data/requestsMedium5.xml");
        Assert.assertFalse(planningRequestParseTest1.equals(planningRequestParseTest2));
        Assert.assertFalse(planningRequestParseTest1.equals(1));
        Assert.assertTrue(planningRequestParseTest1.equals(planningRequestParseTest1));

    }

}