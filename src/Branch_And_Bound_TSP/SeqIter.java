
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class SeqIter implements Iterator<Integer> {

    private ArrayList<Integer> candidates;
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
        this.candidates = new ArrayList<Integer>();
        for (TupleRequete s : unvisited) {
            if (g[currentVertex][ptsIdToIndex.get(s.getCurrentGoal().getId())] != null) {// && ! candidates.contains(ptsIdToIndex.get(s.getCurrentGoal().getId()))) {
                candidates.add(ptsIdToIndex.get(s.getCurrentGoal().getId()));
                nbCandidates++;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return candidates.size() > 0;
    }

    @Override
    public Integer next() {
        int result = candidates.get(candidates.size()-1);
        candidates.remove(candidates.size()-1);
        return result;
    }

    @Override
    public void remove() {
    }

}
