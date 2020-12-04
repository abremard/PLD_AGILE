
package Branch_And_Bound_TSP;

import objects.Request;
import processing.SuperArete;

import java.util.ArrayList;

public interface TSP {
	/**
	 * Search for a shortest cost hamiltonian circuit in <code>g</code> within <code>timeLimit</code> milliseconds
	 * (returns the best found tour whenever the time limit is reached)
	 * Warning: The computed tour always start from vertex 0
	 * @param timeLimit
	 * @param g
	 */
	public void searchSolution(int timeLimit, SuperArete[][] g, ArrayList<Request> listeRequetes);

	/**
	 * @param i
	 * @return the ith visited vertex in the solution computed by <code>searchSolution</code> 
	 * (-1 if <code>searcheSolution</code> has not been called yet, or if i < 0 or i >= g.getNbSommets())
	 */
	public Integer getSolution(int i);

	public Integer[] getSolution();
	
	/** 
	 * @return the total cost of the solution computed by <code>searchSolution</code> 
	 * (-1 if <code>searcheSolution</code> has not been called yet).
	 */
	public float getSolutionCost();

}
