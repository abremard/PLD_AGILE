
package Branch_And_Bound_TSP;

import objects.Request;
import processing.SuperArete;
import processing.TupleRequete;

import java.util.*;

/**
 * Template of a class that solves the Pickup/Delivery TSP problem using the Branch And Bound paradigm.
 * Inheritors may override functions to implement heuristics that accelerate computing.
 *
 * @see BnB_TSP
 */
public abstract class Template_BnB_TSP implements BnB_TSP {

    /**
     * List of nodes indices for the current best found solution.
     */
    private Integer[] bestSol;

    /**
     * Sum of the distances of all Segments of the current best solution.
     */
    private float bestSolCost;

    /**
     * The optimal full sub-graph of the map as an adjacence matrix.
     */
    protected SuperArete[][] matAdj;

    /**
     * The dictionnary associating and Intersection ID with its index in the adjacence matrix.
     */
    HashMap<Long, Integer> ptsIdToIndex;

    /**
     * Duration after which computation will automatically stop, in milliseconds.
     */
    private int timeLimit;

    /**
     * Time at which the computation started.
     */
    private long startTime;

    /**
     * Number of iterations the program went through (branchAndBound()).
     */
    private int nbIter;

    /**
     * Number of solutions the program considered (branchAndBound()).
     */
    private int nbSol;

    /**
     * Start the computation for a given instance of the problem.
     * After running this method, the solution can be obtained using getSolution().
     *
     * @param timeLimit     The execution duration after which computing will automatically be stopped, in milliseconds
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     * @param listeRequetes A list of Requests to deal with
     */
    public void searchSolution(int timeLimit, SuperArete[][] matAdj, ArrayList<Request> listeRequetes) {
        nbIter = nbSol = 0;

        if (timeLimit <= 0) return;
        startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit;
        this.matAdj = matAdj;
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

    // Getters and setters
    public Integer[] getSolution() {
        System.out.println("Done with Branch and Bound computation, number of Iterations : " + nbIter + ", compared " + nbSol + " solutions");
        return bestSol;
    }

    public float getExecTime() {
        return (System.currentTimeMillis() - startTime) / (float) 1000;
    }

    public void setIndexDico() {
        ptsIdToIndex = new HashMap<>();
        for (int i = 0; i < matAdj.length; i++) {
            ptsIdToIndex.put(matAdj[i][i == 0 ? 1 : 0].getDepart().getId(), i);
        }
    }

    // Core logic methods

    /**
     * Computes a lower bound of the length of the path left to travel to complete all remaining Requests.
     * Method that must be defined in TemplateTSP subclasses.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param requetes      The list of Requests left to deal with
     * @return a lower bound of the cost of paths in <code>matAdj</code> starting from <code>currentVertex</code>,
     * visiting every interest point in <code>requetes</code>, and returning back to vertex <code>0</code>.
     */
    protected abstract float bound(Integer currentVertex, ArrayList<TupleRequete> requetes);

    /**
     * Creates an iterator that scrolls through all remaining interest points.
     * Method that must be defined in TemplateTSP subclasses.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param unvisited     The unordered list of nodes (interest points) that must still be visited
     * @param ptsIdToIndex  The dictionnary associating and Intersection ID with its index in the adjacence matrix
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     * @return an iterator for visiting all remaining interest points in <code>requetes</code>.
     */
    protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] matAdj);

    /**
     * Template method of a branch and bound algorithm for solving the TSP in <code>matAdj</code>.
     * Recursive method, building paths with each iteration.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param leftTodo      the number of vertices that have not yet been visited
     * @param visited       the sequence of vertices that have been already visited (including currentVertex)
     * @param currentCost   the cost of the path corresponding to <code>visited</code>
     * @param requetes      The list of Requests left to deal with
     */
    private void branchAndBound(int currentVertex, int leftTodo, ArrayList<Integer> visited, float currentCost, ArrayList<TupleRequete> requetes) {
        nbIter++;

        if (System.currentTimeMillis() - startTime > timeLimit) {
            System.out.println("Hit time limit");
            return;
        }
        if (leftTodo == 0) {
            if (matAdj[currentVertex][0] != null) {
                nbSol++;

                if (currentCost + matAdj[currentVertex][0].getLongueur() < bestSolCost) {
                    bestSol = new Integer[visited.size()];
                    visited.toArray(bestSol);
//                    bestSol[visited.size()] = 0;
                    bestSolCost = currentCost + matAdj[currentVertex][0].getLongueur();
                }
            }
        } else if (currentCost + bound(currentVertex, requetes) < bestSolCost) {
            Iterator<Integer> it = iterator(currentVertex, requetes, ptsIdToIndex, matAdj);
            while (it.hasNext()) {
                Integer nextVertex = it.next();

                // on traite les requetes a ce noeud
                LinkedList<Integer> traites = new LinkedList<Integer>();
                LinkedList<TupleRequete> removed = new LinkedList<TupleRequete>();
                LinkedList<Integer> removedInd = new LinkedList<Integer>();
                ArrayList<TupleRequete> backupRequetes = new ArrayList<TupleRequete>(requetes);
                for (int i = 0; i < requetes.size(); i++) {
                    if (requetes.get(i).getCurrentGoal().getId() == matAdj[nextVertex][nextVertex == 0 ? 1 : 0].getDepart().getId()) {
                        if (requetes.get(i).isDepart()) {
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
                branchAndBound(nextVertex, leftTodo - removed.size(), visited,
                        currentCost + matAdj[currentVertex][nextVertex].getLongueur(), requetes);
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
