
package Branch_And_Bound_TSP;

import objects.Request;
import processing.SuperArete;
import processing.TupleRequete;

import java.util.*;

public abstract class TemplateTSP implements TSP {
    private Integer[] bestSol;
    protected SuperArete[][] g;
    HashMap<Long, Integer> ptsIdToIndex;
    private float bestSolCost;
    private int timeLimit;
    private long startTime;
    private int nbIter, nbSol;

    public void searchSolution(int timeLimit, SuperArete[][] g, ArrayList<Request> listeRequetes) {
        nbIter = nbSol = 0;

        if (timeLimit <= 0) return;
        startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
        this.g = g;
//        bestSol = new Integer[g.length];
//        ArrayList<Integer> unvisited = new ArrayList<Integer>(g.length - 1);
//        for (int i = 1; i < g.length; i++) unvisited.add(i);
        ArrayList<Integer> visited = new ArrayList<Integer>();
        visited.add(0); // The first visited vertex is 0
        bestSolCost = Integer.MAX_VALUE;

        ArrayList<TupleRequete> requetes = new ArrayList<TupleRequete>();
        for (Request req : listeRequetes) {
            requetes.add(new TupleRequete(req, true));
        }
        setIndexDico();

        branchAndBound(0, requetes.size(), visited, 0, requetes);
    }

    public Integer getSolution(int i) {
        if (g != null && i >= 0 && i < g.length)
            return bestSol[i];
        return -1;
    }

    public Integer[] getSolution() {
        System.out.println("Nombre d'iterations : " + nbIter + ", comparé " + nbSol + " solutions");
        return bestSol;
    }

    public float getSolutionCost() {
        if (g != null)
            return bestSolCost;
        return -1;
    }

    public float getExecTime() {
        return (System.currentTimeMillis() - startTime)/(float)1000;
    }

    public void setIndexDico() {
        ptsIdToIndex = new HashMap<>();
        for (int i = 0; i < g.length; i++) {
            ptsIdToIndex.put(g[i][i == 0 ? 1 : 0].getDepart().getId(), i);
        }
    }

    /**
     * Method that must be defined in TemplateTSP subclasses
     *
     * @param currentVertex
     * @param requetes
     * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
     * every vertex in <code>unvisited</code> exactly once, and returning back to vertex <code>0</code>.
     */
    protected abstract float bound(Integer currentVertex, ArrayList<TupleRequete> requetes);

    /**
     * Method that must be defined in TemplateTSP subclasses
     *
     * @param currentVertex
     * @param unvisited
     * @param g
     * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
     */
    protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] g);

    /**
     * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
     *
     * @param currentVertex the last visited vertex
     * @param leftTodo     the set of vertex that have not yet been visited
     * @param visited       the sequence of vertices that have been already visited (including currentVertex)
     * @param currentCost   the cost of the path corresponding to <code>visited</code>
     */
    private void branchAndBound(int currentVertex, int leftTodo, ArrayList<Integer> visited, float currentCost, ArrayList<TupleRequete> requetes) {
        nbIter ++;

//        if (System.currentTimeMillis() - startTime > timeLimit) {
//            System.out.println("Hit time limit");
//            return;
//        }
        if (leftTodo == 0) {
            if (g[currentVertex][0] != null) {
                nbSol ++;

                if (currentCost + g[currentVertex][0].getLongueur() < bestSolCost) {
                    bestSol = new Integer[visited.size()];
                    visited.toArray(bestSol);
//                    bestSol[visited.size()] = 0;
                    bestSolCost = currentCost + g[currentVertex][0].getLongueur();
                }
            }
        } else if (currentCost + bound(currentVertex, requetes) < bestSolCost) {
            Iterator<Integer> it = iterator(currentVertex, requetes, ptsIdToIndex, g);
            while (it.hasNext()) {
                Integer nextVertex = it.next();

                // on traite les requetes a ce noeud
                LinkedList<Integer> traites = new LinkedList<Integer>();
                LinkedList<TupleRequete> removed = new LinkedList<TupleRequete>();
                LinkedList<Integer> removedInd = new LinkedList<Integer>();
                ArrayList<TupleRequete> backupRequetes = new ArrayList<TupleRequete>(requetes);
                for (int i = 0; i < requetes.size(); i++) {
                    if (requetes.get(i).getCurrentGoal().getId() == g[nextVertex][nextVertex == 0 ? 1 : 0].getDepart().getId()) {
                        if(requetes.get(i).isDepart()) {
                            traites.add(i);
                        } else {
                            removed.add(requetes.get(i));
                            removedInd.add(i);
                        }
                    }
                }
                // traitement effectif
                for (int ind : traites) {
                    requetes.get(ind).setDepart(false);
                }
                requetes.removeAll(removed);

                // recursion
                visited.add(nextVertex);
//                visited.add(nextVertex);
//                unvisited.remove(nextVertex);
                branchAndBound(nextVertex, leftTodo-removed.size(), visited,
                        currentCost + g[currentVertex][nextVertex].getLongueur(), requetes);
                visited.remove(nextVertex);
//                visited.remove(nextVertex);
//                unvisited.add(nextVertex);

                // on annule le traitement des requetes a ce noeud
//                requetes.addAll(removed);
                requetes = backupRequetes;
                for (int ind : traites) {
                    requetes.get(ind).setDepart(true);
                }
            }
        }
    }

}
