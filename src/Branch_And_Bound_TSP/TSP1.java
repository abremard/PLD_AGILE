
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {

	@Override
	protected float bound(Integer currentVertex, ArrayList<TupleRequete> requetes) {
		return 0;
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] g) {
		return new SeqIter(currentVertex, unvisited, ptsIdToIndex, g);
	}

}
