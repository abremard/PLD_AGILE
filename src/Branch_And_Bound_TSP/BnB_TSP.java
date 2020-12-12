
package Branch_And_Bound_TSP;

import objects.Request;
import processing.SuperArete;

import java.util.ArrayList;

/**
 * Interface for all BnB_TSP variants, which implement different heuristics to speed up computation.
 *
 * @see Template_BnB_TSP
 */
public interface BnB_TSP {

    /**
     * Start the computation for a given instance of the problem.
     * After running this method, the solution can be obtained using getSolution().
     *
     * @param timeLimit     The execution duration after which computing will automatically be stopped, in milliseconds
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     * @param listeRequetes A list of Requests to deal with
     */
    public void searchSolution(int timeLimit, SuperArete[][] matAdj, ArrayList<Request> listeRequetes);

    // Getters and setters

    public Integer[] getSolution();

    public float getExecTime();

}
