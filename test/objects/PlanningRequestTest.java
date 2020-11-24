package objects;

import junit.framework.TestCase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class PlanningRequestTest extends TestCase {

    public static void main() throws ParserConfigurationException, SAXException, IOException {
        testParseRequest();
    }


    public static void testParseRequest() throws IOException, SAXException, ParserConfigurationException {
        PlanningRequest planningRequestParse = new PlanningRequest();
        planningRequestParse.parseRequest("temp/fichiersXML2020/requestsMedium3.xml");
    }
}