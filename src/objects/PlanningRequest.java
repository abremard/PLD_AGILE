package objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PlanningRequest {
    Depot depot;
    List<Request> requestList = new ArrayList<Request>();

    public PlanningRequest() {
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public void parseRequest(String requestFile) throws ParserConfigurationException, IOException, SAXException {

        File fXmlFile = new File(requestFile);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        NodeList depotNodeList = doc.getElementsByTagName("depot");
        Element depotRecupere = (Element) depotNodeList.item(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        String dateDepot = depotRecupere.getAttribute("departureTime");
        LocalTime dateDepotParsed = LocalTime.parse(dateDepot, formatter);
        Depot depotParsed = new Depot(new Intersection(Integer.parseInt(depotRecupere.getAttribute("address"))), dateDepotParsed);
        setDepot(depotParsed);
        NodeList nodeRequestList = doc.getElementsByTagName("request");

        for (int temp = 0; temp < nodeRequestList.getLength(); temp++) {

            Node requestNode = nodeRequestList.item(temp);

            if (requestNode.getNodeType() == Node.ELEMENT_NODE) {

                Element requestElement = (Element) requestNode;
                Intersection pickupIntersection = new Intersection(Integer.parseInt(requestElement.getAttribute("pickupAddress")));
                Intersection deliveryIntersection = new Intersection(Integer.parseInt(requestElement.getAttribute("deliveryAddress")));
                Request requestParsed = new Request(pickupIntersection, deliveryIntersection, Double.parseDouble(requestElement.getAttribute("pickupDuration")), Double.parseDouble(requestElement.getAttribute("deliveryDuration")));
                // for debugging purposes
                // System.out.println(requestParsed.toString());
                requestList.add(requestParsed);
            }
        }
    }
}
