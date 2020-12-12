
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An iterator which scrolls through the unvisited interest points of a Request list.
 *
 * @see Template_BnB_TSP
 */
public class SeqIter implements Iterator<Integer> {

    /**
     * The List of interest point indices (relative the the adjacence matrix indices) through which the iterator scrolls
     */
    private final ArrayList<Integer> candidates;

    /**
     * Create an iterator to traverse the set of interest points of <code>unvisited</code>.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param unvisited     The unordered list of nodes (interest points) that must still be visited
     * @param ptsIdToIndex  The dictionnary associating and Intersection ID with its index in the adjacence matrix
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     */
    public SeqIter(int currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] matAdj) {
        this.candidates = new ArrayList<Integer>();
        for (TupleRequete s : unvisited) {
            if (matAdj[currentVertex][ptsIdToIndex.get(s.getCurrentGoal().getId())] != null) {
                candidates.add(ptsIdToIndex.get(s.getCurrentGoal().getId()));
            }
        }
    }

    // Overrides

    @Override
    public boolean hasNext() {
        return candidates.size() > 0;
    }

    @Override
    public Integer next() {
        int result = candidates.get(candidates.size() - 1);
        candidates.remove(candidates.size() - 1);
        return result;
    }

    @Override
    public void remove() {
    }

}
