
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class SeqIter implements Iterator<Integer> {

    private Integer[] candidates;
    private int nbCandidates;

    /**
     * Create an iterator to traverse the set of vertices in <code>unvisited</code>
     * which are successors of <code>currentVertex</code> in <code>g</code>
     * Vertices are traversed in the same order as in <code>unvisited</code>
     *
     * @param unvisited
     * @param currentVertex
     * @param g
     */
    public SeqIter(int currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] g) {
        this.candidates = new Integer[unvisited.size()];
        for (TupleRequete s : unvisited) {
            if (g[currentVertex][ptsIdToIndex.get(s.getCurrentGoal().getId())] != null)
                candidates[nbCandidates++] = ptsIdToIndex.get(s.getCurrentGoal().getId());
        }
    }

    @Override
    public boolean hasNext() {
        return nbCandidates > 0;
    }

    @Override
    public Integer next() {
        nbCandidates--;
        return candidates[nbCandidates];
    }

    @Override
    public void remove() {
    }

}
