package objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

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

                    long idOrigin = Long.parseLong(eElement.getAttribute("origin"));
                    long idDestination = Long.parseLong(eElement.getAttribute("destination"));

                    Intersection intersectionOrigin = null;
                    Intersection intersectionDestination = null;

                    for (int i=0; i<intersectionList.size(); i++){
                        if (intersectionList.get(i).getId() == idOrigin){
                            intersectionOrigin = intersectionList.get(i);
                        }
                        else if (intersectionList.get(i).getId() == idDestination){
                            intersectionDestination = intersectionList.get(i);
                        }
                    }

                    Segment newSegment = new Segment(
                            intersectionOrigin,
                            intersectionDestination,
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
            if (segmentList.get(i).getDestination().getId() == id || segmentList.get(i).getOrigin().getId() == id){
                segmentNameList.add(segmentList.get(i).getName());
            }
        }
        return segmentNameList;
    }

}
