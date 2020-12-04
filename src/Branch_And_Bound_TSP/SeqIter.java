
package Branch_And_Bound_TSP;

import processing.SuperArete;

import java.util.Collection;
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
    public SeqIter(Collection<Integer> unvisited, int currentVertex, SuperArete[][] g) {
        this.candidates = new Integer[unvisited.size()];
        for (Integer s : unvisited) {
            if (g[currentVertex][s] != null)
                candidates[nbCandidates++] = s;
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
