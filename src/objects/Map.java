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
 * Classe métier représentant la carte, c'est-à-dire le graphe que l'on va parcourir.
 * @author H4302
 * @see Intersection
 * @see Segment
 */
public class Map {

    /**
     * Liste des intersections contenues dans la map.
     */
    ArrayList<Intersection> intersectionList;

    /**
     * Liste des segments contenus dans la map.
     */
    ArrayList<Segment> segmentList;

    /**
     * Nombre d'intersections contenues dans la map. Equivalent à intersectionList.size().
     */
    Integer noOfIntersections;

    /**
     * Nombre de segments contenus dans la map. Equivalent à segmentList.size().
     */
    Integer noOfSegments;

    // Constructeurs

    public Map() {
        this.intersectionList = new ArrayList<Intersection>();
        this.segmentList = new ArrayList<Segment>();
        this.noOfIntersections = 0;
        this.noOfSegments = 0;
    }

    /**
     * Construit une map en chargeant un fichier XML.
     * @param documentName  le chemin du fichier XML contenant les données de la map
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

    // Getters et setters

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

    // Fonctions utilitaires

    /**
     * Recherche l'intersection d'id donné parmis les intersections de la map.
     * @param id    L'id de l'intersection à rechercher
     * @return l'intersection trouvée, ou null si aucune intersection n'a été trouvée
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
     * @param id    L'id de l'intersection
     * @return la liste des noms des Segments
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
     * Recherche l'intersection la plus proche d'un point donné.
     * Les distances sont considérées à partir de la longitude et latitude, à vol d'oiseau (distance euclidienne).
     * @param cursorIntersection    L'intersection dont les coordonnées sont celles du point duquel on veut trouver
     *                              l'intersection la plus proche. Cette intersection peut être fictive, c'est-à-dire
     *                              ne pas exister dans la liste des intersections de la map.
     * @return l'intersection la plus proche, parmi la liste d'intersections de la map
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
     * @param cursorLatitude    la latitude de la première position
     * @param cursorLongitude   la longitude de la première position
     * @param intersection      l'intersection dont les coordonnées sont celles de la deuxième position
     * @return la distance euclidienne entre les deux positions
     */
    private double calculateDistanceBetweenCursorAndIntersection(double cursorLatitude, double cursorLongitude, Intersection intersection) {
        return Math.sqrt((Math.pow(cursorLatitude - intersection.getLatitude(), 2)) + Math.pow(cursorLongitude - intersection.getLongitude(), 2));
    }
}
