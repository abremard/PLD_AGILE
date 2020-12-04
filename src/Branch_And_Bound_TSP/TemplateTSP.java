
package Branch_And_Bound_TSP;

import processing.SuperArete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class TemplateTSP implements TSP {
    private Integer[] bestSol;
    protected SuperArete[][] g;
    private float bestSolCost;
    private int timeLimit;
    private long startTime;

    public void searchSolution(int timeLimit, SuperArete[][] g) {
        if (timeLimit <= 0) return;
        startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
        this.g = g;
        bestSol = new Integer[g.length];
        Collection<Integer> unvisited = new ArrayList<Integer>(g.length - 1);
        for (int i = 1; i < g.length; i++) unvisited.add(i);
        Collection<Integer> visited = new ArrayList<Integer>(g.length);
        visited.add(0); // The first visited vertex is 0
        bestSolCost = Integer.MAX_VALUE;
        branchAndBound(0, unvisited, visited, 0);
    }

    public Integer getSolution(int i) {
        if (g != null && i >= 0 && i < g.length)
            return bestSol[i];
        return -1;
    }

    public Integer[] getSolution() {
        return bestSol;
    }

    public float getSolutionCost() {
        if (g != null)
            return bestSolCost;
        return -1;
    }

    /**
     * Method that must be defined in TemplateTSP subclasses
     *
     * @param currentVertex
     * @param unvisited
     * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
     * every vertex in <code>unvisited</code> exactly once, and returning back to vertex <code>0</code>.
     */
    protected abstract float bound(Integer currentVertex, Collection<Integer> unvisited);

    /**
     * Method that must be defined in TemplateTSP subclasses
     *
     * @param currentVertex
     * @param unvisited
     * @param g
     * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
     */
    protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, SuperArete[][] g);

    /**
     * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
     *
     * @param currentVertex the last visited vertex
     * @param unvisited     the set of vertex that have not yet been visited
     * @param visited       the sequence of vertices that have been already visited (including currentVertex)
     * @param currentCost   the cost of the path corresponding to <code>visited</code>
     */
    private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
                                Collection<Integer> visited, float currentCost) {
        if (System.currentTimeMillis() - startTime > timeLimit) return;
        if (unvisited.size() == 0) {
            if (g[currentVertex][0] != null) {
                if (currentCost + g[currentVertex][0].getLongueur() < bestSolCost) {
                    visited.toArray(bestSol);
                    bestSolCost = currentCost + g[currentVertex][0].getLongueur();
                }
            }
        } else if (currentCost + bound(currentVertex, unvisited) < bestSolCost) {
            Iterator<Integer> it = iterator(currentVertex, unvisited, g);
            while (it.hasNext()) {
                Integer nextVertex = it.next();
                visited.add(nextVertex);
                unvisited.remove(nextVertex);
                branchAndBound(nextVertex, unvisited, visited,
                        currentCost + g[currentVertex][nextVertex].getLongueur());
                visited.remove(nextVertex);
                unvisited.add(nextVertex);
            }
        }
    }

}
