
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.*;

public class TSP3 extends TemplateTSP {

    @Override
    protected float bound(Integer currentVertex, ArrayList<TupleRequete> requetes) {
        if(requetes.size() == 1) {
            if(currentVertex == 0) {
                return 0;
            }
            return g[currentVertex][0].getLongueur();
        }

        LinkedList<Integer> nonVisites = new LinkedList<Integer>();
        for (TupleRequete req : requetes) {
            nonVisites.add(ptsIdToIndex.get(req.getCurrentGoal().getId()));
            if(req.isDepart()) {
                nonVisites.add(ptsIdToIndex.get(req.getRequete().getDelivery().getId()));
            }
        }

        float maxVal = 0;
        float minSum = 0;
        float longueur;
        for (Integer ind1 : nonVisites) {
            if(! ind1.equals(currentVertex)) {
                longueur = g[currentVertex][ind1].getLongueur();
                maxVal = Math.max(maxVal, longueur);
            }

            float minVal = Float.MAX_VALUE;
            for (Integer ind2 : nonVisites) {
                if(ind1.equals(ind2)) {
                    continue;
                }
                longueur = g[ind1][ind2].getLongueur();
                minVal = Math.min(minVal, longueur);
            }
            minSum += minVal;
        }

        return Math.max(maxVal, minSum);
    }

    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] g) {
        return new SeqIter(currentVertex, unvisited, ptsIdToIndex, g);
    }

}
