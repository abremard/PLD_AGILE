
package Branch_And_Bound_TSP;

import processing.SuperArete;

import java.util.Collection;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {

	@Override
	protected float bound(Integer currentVertex, Collection<Integer> unvisited) {
		return 0;
	}

	@Override
	protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, SuperArete[][] g) {
		return new SeqIter(unvisited, currentVertex, g);
	}

}
