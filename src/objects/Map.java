package objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Map {

    ArrayList<Intersection> intersectionList;
    ArrayList<Segment> segmentList;
    // plus pratique pour l'algo
    Integer noOfIntersections;
    Integer noOfSegments;

    public Map(String documentName) {
        try {
            File inputFile = new File(documentName);
            // File inputFile = new File("../temp/"+documentName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("intersection");
            intersectionList = new ArrayList<Intersection>();
            segmentList = new ArrayList<Segment>();

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Intersection newIntersection = new Intersection(
                            Double.parseDouble(eElement.getAttribute("latitude")),
                            Double.parseDouble(eElement.getAttribute("longitude")),
                            Long.parseLong(eElement.getAttribute("id")));

                    intersectionList.add(newIntersection);
                }
            }

            nList = doc.getElementsByTagName("segment");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Segment newSegment = new Segment(
                            Long.parseLong(eElement.getAttribute("origin")),
                            Long.parseLong(eElement.getAttribute("destination")),
                            Float.parseFloat(eElement.getAttribute("length")), eElement.getAttribute("name"));

                    segmentList.add(newSegment);
                }
            }

            this.noOfIntersections = this.intersectionList.size();
            this.noOfSegments = this.segmentList.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Intersection> getIntersectionList() {
        return intersectionList;
    }

    public ArrayList<Segment> getSegmentList() {
        return segmentList;
    }

    public void setIntersectionList(ArrayList<Intersection> intersectionList) {
        this.intersectionList = intersectionList;
    }

    public void setSegmentList(ArrayList<Segment> segmentList) {
        this.segmentList = segmentList;
    }

    public Integer getNoOfIntersections() {
        return noOfIntersections;
    }

    public Integer getNoOfSegments() {
        return noOfSegments;
    }

    public Intersection matchIdToIntersection(long id) {
        Intersection matchedIntersection = null;
        for (Intersection intersection : intersectionList) {
            long idIntersection = intersection.getId();
            if (idIntersection == id) {
                matchedIntersection = intersection;
                break;
            }
        }
        return matchedIntersection;
    }

    public ArrayList<String> getSegmentNameFromIntersectionId(long id){
        ArrayList<String> segmentNameList = null;
        for (int i=0; i<segmentList.size(); i++){
            if (segmentList.get(i).getDestination() == id || segmentList.get(i).getOrigin() == id){
                segmentNameList.add(segmentList.get(i).getName());
            }
        }
        return segmentNameList;
    }

}
