
package Branch_And_Bound_TSP;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	int[][] cost;
	
	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param nbVertices
	 */
	public CompleteGraph(int nbVertices){
		this.nbVertices = nbVertices;
		int iseed = 1;
		cost = new int[nbVertices][nbVertices];
		for (int i=0; i<nbVertices; i++){
		    for (int j=0; j<nbVertices; j++){
		        if (i == j) cost[i][j] = -1;
		        else {
		            int it = 16807 * (iseed % 127773) - 2836 * (iseed / 127773);
		            if (it > 0)	iseed = it;
		            else iseed = 2147483647 + it;
		            cost[i][j] = MIN_COST + iseed % (MAX_COST-MIN_COST+1);
		        }
		    }
		}
	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public int getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

}
