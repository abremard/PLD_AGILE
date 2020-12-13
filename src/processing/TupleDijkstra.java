
package processing;

import objects.Intersection;

/**
 * Encapsulates an intersection with its current distance to the starting point in a Dijkstra run.
 * Used in a priority queue.
 *
 * @author H4302
 * @see ComputeTour
 */
public class TupleDijkstra implements Comparable<TupleDijkstra> {

    /**
     * The current distance of the node to the starting node.
     */
    float distance;

    /**
     * The encapsulated node.
     */
    Intersection intersection;

    /**
     * The node's current color.
     */
    Color color;

    /**
     * Represents the different possible colors of a point in a Dijkstra run :
     * - white if it has not been visited yet
     * - gray if it has been visited but its predecessor has not been established yet
     * - black if its predecessor has been established
     */
    enum Color {
        WHITE, GREY, BLACK;
    }

    // Constructor

    public TupleDijkstra(Intersection intersection, float distance) {
        this.distance = distance;
        this.intersection = intersection;
        this.color = Color.WHITE;
    }

    //  Overrides

    @Override
    public String toString() {
        return "[" + intersection.getId() + " : " + distance + "]";
    }

    @Override
    public int compareTo(TupleDijkstra tup) {
        return Float.compare(this.distance, tup.distance);
    }

}

