
package processing;

import objects.Intersection;

public class TupleDijkstra implements Comparable<TupleDijkstra> {

    float distance;
    Intersection intersection;
    Color color;

    enum Color {
        WHITE, GREY, BLACK;
    }

    public TupleDijkstra(Intersection intersection, float distance){
        this.distance = distance;
        this.intersection = intersection;
        this.color = Color.WHITE;
    }

    public String toString(){
        return "[" + intersection.getId() + " : " + distance + "]";
    }

    @Override
    public int compareTo(TupleDijkstra tup) {
        return Float.compare(this.distance, tup.distance);
    }

}

