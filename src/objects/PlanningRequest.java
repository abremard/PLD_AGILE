package objects;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PlanningRequest {
    Depot depot;
    List<Request> list = new ArrayList<Request>();

    public PlanningRequest(Depot depot) {
        this.depot = depot;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public void parseRequest(String requestFile) throws ParserConfigurationException, IOException, SAXException {

        File fXmlFile = new File("requestsMedium3.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        NodeList  depotNode = doc.getElementsByTagName("depot");
        NodeList nList = doc.getElementsByTagName("staff");

        System.out.println("----------------------------");

        for (int temp = 0; temp < nList.getLength(); temp++)

        {

            Node nNode = nList.item(temp);
        }
    }



}
