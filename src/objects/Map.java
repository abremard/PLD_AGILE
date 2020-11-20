package objects;

import java.util.LinkedList;

public class Map {
    LinkedList<Intersection> intersectionList;
    LinkedList<Segment> segmentList;

    public Map(LinkedList<Intersection> intersectionList, LinkedList<Segment> segmentList) {
        this.intersectionList = intersectionList;
        this.segmentList = segmentList;
    }

    public LinkedList<Intersection> getIntersectionList() {
        return intersectionList;
    }

    public LinkedList<Segment> getSegmentList() {
        return segmentList;
    }

    public void setIntersectionList(LinkedList<Intersection> intersectionList) {
        this.intersectionList = intersectionList;
    }

    public void setSegmentList(LinkedList<Segment> segmentList) {
        this.segmentList = segmentList;
    }
}
