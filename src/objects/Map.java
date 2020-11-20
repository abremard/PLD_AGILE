package objects;

import java.util.LinkedList;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Map {
    LinkedList<Intersection> intersectionList;
    LinkedList<Segment> segmentList;

    public Map(String documentName) {
        try {
            File inputFile = new File("../temp/"+documentName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("intersection");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Intersection newIntersection = new Intersection(Double.parseDouble(eElement.getAttribute("latitude")),
                        Double.parseDouble(eElement.getAttribute("longitude")),
                            Integer.parseInt(eElement.getAttribute("id")));

                    intersectionList.add(newIntersection);
                }
            }

            nList = doc.getElementsByTagName("segment");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Segment newSegment = new Segment(Integer.parseInt(eElement.getAttribute("origin")),
                        Integer.parseInt(eElement.getAttribute("destination")),
                            Float.parseFloat(eElement.getAttribute("length")), eElement.getAttribute("name"));

                    segmentList.add(newSegment);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<Intersection> getIntersectionList() {
        return intersectionList;
    }

    public LinkedList<Segment> getSegmentList() {
        return segmentList;
    }

    public void setIntersectionList(LinkedList<Intersection> intersectionList) {
        this.intersectionList = intersectionList;
    }

    public void setSegmentList(LinkedList<Segment> segmentList) {
        this.segmentList = segmentList;
    }
}
