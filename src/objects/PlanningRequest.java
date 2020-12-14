
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
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Business class representing a planning : an unordered list of Requests to deal with and the departure address
 * from which the Tour must start and where it must end.
 * @author H4302
 * @see Depot
 * @see Request
 */
public class PlanningRequest {

    /**
     * The departure address from which the Tour must start and where it must end.
     */
    Depot depot;

    /**
     * The unordered list of Requests to deal with.
     */
    ArrayList<Request> requestList = new ArrayList<>();

    // Constructors

    public PlanningRequest() {
    }

    public PlanningRequest(PlanningRequest copyOfPlanningRequest) {
        this.depot = copyOfPlanningRequest.depot;
        this.requestList = new ArrayList<>(copyOfPlanningRequest.requestList);
    }

    // Getters and setters

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public ArrayList<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(ArrayList<Request> requestList) {
        this.requestList = requestList;
    }

    // Utilitarian functions

    /**
     * Loads the informations of an XML file corresponding to a planning.
     * @param requestFile   the path the the XML file to load
     * @throws ParserConfigurationException in case of failure of the construction of the DocumentBuilder
     * @throws IOException                  in case of failure of the file parsing
     * @throws SAXException                 in case of failure of the file parsing
     */
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
        Depot depotParsed = new Depot(new Intersection(Long.parseLong(depotRecupere.getAttribute("address"))), dateDepotParsed);
        setDepot(depotParsed);
        NodeList nodeRequestList = doc.getElementsByTagName("request");

        for (int temp = 0; temp < nodeRequestList.getLength(); temp++) {

            Node requestNode = nodeRequestList.item(temp);

            if (requestNode.getNodeType() == Node.ELEMENT_NODE) {

                Element requestElement = (Element) requestNode;
                Intersection pickupIntersection = new Intersection(Long.parseLong(requestElement.getAttribute("pickupAddress")));
                Intersection deliveryIntersection = new Intersection(Long.parseLong(requestElement.getAttribute("deliveryAddress")));
                Request requestParsed = new Request(this.getRequestList().size(),pickupIntersection, deliveryIntersection, Double.parseDouble(requestElement.getAttribute("pickupDuration")), Double.parseDouble(requestElement.getAttribute("deliveryDuration")));
                // for debugging purposes
                // System.out.println(requestParsed.toString());
                requestList.add(requestParsed);
            }
        }
    }

    /**
     * Adds a Request to the list of Requests to deal with.
     * @param index         The index where the Request should be added (used to undo/redo)
     * @param newRequest    The Request to add
     */
    public void addRequest(int index, Request newRequest) {
        requestList.add(index, newRequest);
    }

    /**
     * Adds a Request to the list of Requests to deal with (in last position).
     * @param newRequest    the Request to add
     */
    public void addRequest(Request newRequest) {
        requestList.add(newRequest);
    }

    /**
     * Removes a Request of the list of Requests to deal with.
     * @param requestToRemove   The Request to remove
     * @return a boolean indicating if the Request was successfully removed
     */
    public boolean removeRequest(Request requestToRemove) {
        for (Request request : requestList) {
            if (request == requestToRemove) {
                requestList.remove(requestToRemove);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a Request of the list of Requests to deal with.
     * @param index     The index in the list where a Request should be removed
     */
    public void removeRequest(int index) {
        requestList.remove(index);
    }

    /**
     * Replaces a Request with another in the list
     * @param requestToRemove   The Rrequest to replace
     * @param requestToAdd      The Request with which it must be replaced
     */
    public void modifyRequest(Request requestToRemove, Request requestToAdd) {
        for (Request request : requestList) {
            if (request == requestToRemove) {
                requestList.remove(requestToRemove);
                requestList.add(requestToAdd);
                break;
            }
        }
    }

    public int findIndexOfRequest(Request request){
        int index = -1;
        for (int i=0; i<requestList.size(); i++){
            if (request == requestList.get(i)){
                index = i;
            }
        }
        return index;
    }

    public void resetIndexOfRequestList() {
        int index = 0;
        for (Request request: requestList) {
            request.setId(index);
            ++index;
        }
    }

    public String toString(){
        String myString = "";
        myString += "Depot : " + depot.toString();
        for (int i=0; i<requestList.size(); i++){
            myString += " request numéro " + i + ": " + requestList.get(i).toString() + "\r\n";
        }
        return myString;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof PlanningRequest)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        PlanningRequest p = (PlanningRequest) o;

        // Compare the data members and return accordingly
        return depot.equals(p.depot)
                && requestList.equals(p.requestList);
    }

}
