
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Inheritor implementing trivial version of BnB_TSP heuristics, making it a near-exhaustive search.
 *
 * @see Template_BnB_TSP
 */
public class BnB_TSP1 extends Template_BnB_TSP {

    /**
     * Computes a lower bound of the length of the path left to travel to complete all remaining Requests.
     * Always returns 0, making the algorithm a near-exhaustive seach.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param requetes      The list of Requests left to deal with
     * @return a lower bound of the cost of paths in <code>matAdj</code> starting from <code>currentVertex</code>,
     * visiting every interest point in <code>requetes</code>, and returning back to vertex <code>0</code>.
     */
    @Override
    protected float bound(Integer currentVertex, ArrayList<TupleRequete> requetes) {
        return 0;
    }

    /**
     * Creates an iterator that scrolls through all remaining interest points.
     * Scrolls in the immediate order of <code>unvisited</code>.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param unvisited     The unordered list of nodes (interest points) that must still be visited
     * @param ptsIdToIndex  The dictionnary associating and Intersection ID with its index in the adjacence matrix
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     * @return an iterator for visiting all remaining interest points in <code>requetes</code>.
     */
    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] matAdj) {
        return new SeqIter(currentVertex, unvisited, ptsIdToIndex, matAdj);
    }

}
