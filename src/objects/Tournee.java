package objects;

import java.util.ArrayList;

public class Tournee {

    ArrayList<Segment> segmentList;
    ArrayList<Request> requestList;
    // todo List<LocationTagContent>

    public Tournee(ArrayList<Segment> segmentList, ArrayList<Request> requestList) {
        this.segmentList = segmentList;
        this.requestList = requestList;
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
