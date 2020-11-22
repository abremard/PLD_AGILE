
package processing;

import objects.Intersection;

public class TupleDijkstra implements Comparable<TupleDijkstra> {

    float distance;
    Intersection intersection;

    public TupleDijkstra(float distance, Intersection intersection){
        this.distance = distance;
        this.intersection = intersection;
    }

    public String toString(){
        return "[" + intersection.getId() + " : " + distance + "]";
    }

    @Override
    public int compareTo(TupleDijkstra tup) {
        return Float.compare(this.distance, tup.distance);
    }

}

