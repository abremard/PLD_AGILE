
package objects;

import processing.TupleRequete;

import java.util.ArrayList;

/**
 * Business class representing a Tour, grouping informations concerning the path to take to complete the list of
 * pending requests.
 * @author H4302
 * @see Depot
 */
public class Tournee {

    /**
     * Path to take, as an ordered list of Segments.
     */
    ArrayList<Segment> segmentList;

    /**
     * Unordered list of pending Requests.
     */
    ArrayList<Request> requestList;

    /**
     * Ordered list of TupleRequete, representing the interest points through which to pass.
     */
    ArrayList<TupleRequete> ptsPassage;

    // Constructors

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

    // Getters and setters

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

    // Overrides

    @Override
    public String toString() {
        return "Tournee{" +
                "segmentList=" + segmentList +
                ", requestList=" + requestList +
                ", ptsPassage=" + ptsPassage +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Tournee)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Tournee t = (Tournee) o;

        // Compare the data members and return accordingly
        return segmentList.equals(t.segmentList)
                && requestList.equals(t.requestList)
                && ptsPassage.equals(t.ptsPassage);
    }
}
