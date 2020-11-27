package objects;

import processing.TupleRequete;

import java.util.ArrayList;

public class Tournee {

    ArrayList<Segment> segmentList;
    ArrayList<Request> requestList;
    ArrayList<TupleRequete> ptsPassage;

    public ArrayList<TupleRequete> getPtsPassage() { return ptsPassage; }

    public void setPtsPassage(ArrayList<TupleRequete> ptsPassage) { this.ptsPassage = ptsPassage; }

    @Deprecated
    public Tournee(ArrayList<Segment> segmentList, ArrayList<Request> requestList) {
        this.segmentList = segmentList;
        this.requestList = requestList;
    }

    // nouvelle version à utiliser à la place de l'ancienne (pour initialiser ptsPassage)
    public Tournee(ArrayList<Segment> segmentList, ArrayList<Request> requestList, ArrayList<TupleRequete> ptsPassage) {
        this.segmentList = segmentList;
        this.requestList = requestList;
        this.ptsPassage = ptsPassage;
    }

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

    @Override
    public String toString() {
        return "Tournee{" +
                "segmentList=" + segmentList +
                ", requestList=" + requestList +
                '}';
    }
}
