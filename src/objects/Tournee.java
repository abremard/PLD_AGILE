
package objects;

import processing.TupleRequete;

import java.util.ArrayList;

/**
 * Classe métier représentant une Tournee, regroupant les informations concernant le trajet à emprunter pour compléter
 * la liste des requêtes à réaliser.
 * @author H4302
 * @see Depot
 */
public class Tournee {

    /**
     * Chemin à emprunter, en tant que liste ordonnée de Segments.
     */
    ArrayList<Segment> segmentList;

    /**
     * Liste non ordonnée des requêtes à réaliser.
     */
    ArrayList<Request> requestList;

    /**
     * Liste ordonnée des TupleRequete, représentant les points d'intérêts par lesquels on passe.
     */
    ArrayList<TupleRequete> ptsPassage;

    // Constructeurs

    public Tournee() {
        this.segmentList = new ArrayList<Segment>();
        this.requestList = new ArrayList<Request>();
    }

    public Tournee(ArrayList<Segment> segmentList, ArrayList<Request> requestList) {
        this.segmentList = segmentList;
        this.requestList = requestList;
    }

    public Tournee(ArrayList<Segment> segmentList, ArrayList<Request> requestList, ArrayList<TupleRequete> ptsPassage) {
        this.segmentList = segmentList;
        this.requestList = requestList;
        this.ptsPassage = ptsPassage;
    }

    // Getters et setters

    public ArrayList<Segment> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(ArrayList<Segment> segmentList) {
        this.segmentList = segmentList;
    }

    public ArrayList<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(ArrayList<Request> requestList) {
        this.requestList = requestList;
    }

    public ArrayList<TupleRequete> getPtsPassage() {
        return ptsPassage;
    }

    public void setPtsPassage(ArrayList<TupleRequete> ptsPassage) {
        this.ptsPassage = ptsPassage;
    }

    // Surcharges

    @Override
    public String toString() {
        return "Tournee{" +
                "segmentList=" + segmentList +
                ", requestList=" + requestList +
                '}';
    }
}
