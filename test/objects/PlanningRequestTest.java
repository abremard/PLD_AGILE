package objects;

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class PlanningRequestTest extends TestCase {

    public void testParseRequest() throws IOException, SAXException, ParserConfigurationException {
        PlanningRequest planningRequestParse = new PlanningRequest();
        planningRequestParse.parseRequest("requestsMedium3.xml");
    }
}