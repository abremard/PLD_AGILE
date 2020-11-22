package objects;

import java.util.LinkedList;

public class Tournee {
    LinkedList<Segment> segmentList;
    LinkedList<Request> requestList;

    public Tournee(LinkedList<Segment> segmentList, LinkedList<Request> requestList) {
        this.segmentList = segmentList;
        this.requestList = requestList;
    }

    public LinkedList<Segment> getSegmentList() {
        return segmentList;
    }

    public void setSegmentList(LinkedList<Segment> segmentList) {
        this.segmentList = segmentList;
    }

    public LinkedList<Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(LinkedList<Request> requestList) {
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
