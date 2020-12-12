
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
 * Classe métier représentant un planning, c'est-à-dire une liste non ordonnée de requêtes à traiter ainsi que
 * le dépôt duquel la tournée doit partir et où elle doit finir.
 * @author H4302
 * @see Depot
 * @see Request
 */
public class PlanningRequest {

    /**
     * Le dépôt duquel la tournée doit partir et où elle doit finir.
     */
    Depot depot;

    /**
     * La liste non ordonnée des requêtes à traiter.
     */
    ArrayList<Request> requestList = new ArrayList<>();

    // Constructeurs

    public PlanningRequest() {
    }

    public PlanningRequest(PlanningRequest copyOfPlanningRequest) {
        this.depot = copyOfPlanningRequest.depot;
        this.requestList = new ArrayList<>(copyOfPlanningRequest.requestList);
    }

    // Getters et setters

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

    // Fonctions utilitaires

    /**
     * Charge les informations d'un fichier XML correspondant à un planning.
     * @param requestFile   le chemin du fichier XML à charger
     * @throws ParserConfigurationException en cas d'échec de la construction du DocumenBuilder
     * @throws IOException                  en cas d'échec du parsing du fichier
     * @throws SAXException                 en cas d'échec du parsing du fichier
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
     * Ajoute une requête à la liste des requêtes à traiter.
     * @param index         L'index auquel ajouter la requête (sert pour undo/redo)
     * @param newRequest    La requête à ajouter
     */
    public void addRequest(int index, Request newRequest) {
        requestList.add(index, newRequest);
    }

    /**
     * Ajoute une requête à la liste des requêtes à traiter (en dernière position).
     * @param newRequest    La requête à ajouter
     */
    public void addRequest(Request newRequest) {
        requestList.add(newRequest);
    }

    /**
     * Retire une requête de la liste des requêtes à traiter.
     * @param requestToRemove   La requête à retirer
     * @return un booléen indiquant si la requête a bien été retirée.
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
     * Retire une requête de la liste des requêtes à traiter.
     * @param index     l'indice de la liste de requêtes auquel retirer une requête
     */
    public void removeRequest(int index) {
        requestList.remove(index);
    }

    /**
     * Remplace une requête par une autre dans la liste
     * @param requestToRemove   la requête à remplacer
     * @param requestToAdd      la requête par laquelle elle doit être remplacée
     */
    public void modifyRequest(Request requestToRemove, Request requestToAdd) {
        for (Request request : requestList) {
            if (request == requestToRemove) {
                requestList.remove(requestToRemove);
                requestList.add(requestToAdd);
            }
        }
    }

    public int findIndexOfRequest(Request request){
        int index = 0;
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
