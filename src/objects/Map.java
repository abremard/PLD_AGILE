
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

import java.lang.Math;

/**
 * Business class representing the map, that is to say the graph we will search.
 * @author H4302
 * @see Intersection
 * @see Segment
 */
public class Map {

    /**
     * List of the Intersections included in the map.
     */
    ArrayList<Intersection> intersectionList;

    /**
     * List of the Segments included in the map.
     *
     */
    ArrayList<Segment> segmentList;

    /**
     * Number of Intersections included in the map. Equivalent to intersectionList.size().
     */
    Integer noOfIntersections;

    /**
     * Number of Segments included in the map. Equivalent to segmentList.size().
     */
    Integer noOfSegments;

    // Constructors

    public Map() {
        this.intersectionList = new ArrayList<Intersection>();
        this.segmentList = new ArrayList<Segment>();
        this.noOfIntersections = 0;
        this.noOfSegments = 0;
    }

    /**
     * Builds a map by loading a XML file.
     * @param documentName  the name of the XML file containing the data of the map
     */
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

    // Getters and setters

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

    // Utilitarian functions

    /**
     * Searches the intersection with the given ID in the map's intersections.
     * @param id    The ID of the Intersection to search
     * @return the intersection that was found, or null if non was found
     */
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

    /**
     * Recherche la liste des noms des Segments (rues) dont l'une des extrémitées est l'intersection d'id donné.
     * Searches for the list of the names of the Segments (streets) of which one of the ends is the Intersection
     * of given ID.
     * @param id    The ID of the Intersection to search
     * @return the list of Segment names
     */
    public ArrayList<String> getSegmentNameFromIntersectionId(long id) {
        ArrayList<String> segmentNameList = new ArrayList<String>();
        for (int i = 0; i < segmentList.size(); i++) {
            if (segmentList.get(i).getDestination() == id || segmentList.get(i).getOrigin() == id) {
                if (segmentList.get(i).getName() != null && !segmentNameList.contains(segmentList.get(i).getName())) {
                    segmentNameList.add(segmentList.get(i).getName());
                }
            }
        }
        return segmentNameList;
    }

    /**
     * Searches the closest Intersection to a given point.
     * Distances are calculated using longitudes and latitudes, in euclidian distance.
     * @param cursorIntersection    The Intersection which coordinates are that of the point of which we want to
     *                              find the closest Intersection. This Intersection might be fictive, that is to say
     *                              it might not exist in the list of Intersections of the Map.
     * @return the closest Intersection within the list of Intersections of the Map
     */
    public Intersection findClosestIntersection(Intersection cursorIntersection) {
        double cursorLatitude = cursorIntersection.getLatitude();
        double cursorLongitude = cursorIntersection.getLongitude();
        Intersection soughtIntersection = null;
        double minimalDistance = 10000000;
        for (Intersection intersection : intersectionList) {
            double caculatedDistance = calculateDistanceBetweenCursorAndIntersection(cursorLatitude, cursorLongitude, intersection);
            if (caculatedDistance < minimalDistance) {
                minimalDistance = caculatedDistance;
                soughtIntersection = intersection;
            }
        }
        return soughtIntersection;
    }

    /**
     * Calcule la distance entre une intersection et les coordonnées données.
     * Les distances sont considérées à partir de la longitude et latitude, à vol d'oiseau (distance euclidienne).
     *
     * Computes the distances between an Intersection and the given coordinates.
     * Distances are calculated using longitudes and latitudes, in euclidian distance.
     * @param cursorLatitude    the latitude of the first position
     * @param cursorLongitude   the longitude of the first position
     * @param intersection      the Intersection which coordinates are that of the second position
     * @return the euclidian distance between the two positions
     */
    private double calculateDistanceBetweenCursorAndIntersection(double cursorLatitude, double cursorLongitude, Intersection intersection) {
        return Math.sqrt((Math.pow(cursorLatitude - intersection.getLatitude(), 2)) + Math.pow(cursorLongitude - intersection.getLongitude(), 2));
    }
}
