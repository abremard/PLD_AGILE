
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.*;

public class TSP2 extends TemplateTSP {

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

        float minVal = Float.MAX_VALUE;
        float maxVal = 0;
        float longueur;
        for (Integer ind1 : nonVisites) {
            if(! ind1.equals(currentVertex)) {
                longueur = g[currentVertex][ind1].getLongueur();
                maxVal = Math.max(maxVal, longueur);
            }

//            for (Integer ind2 : nonVisites) {
//                if(ind1.equals(ind2)) {
//                    continue;
//                }
//                longueur = g[ind1][ind2].getLongueur();
//                minVal = Math.min(minVal, longueur);
//            }
        }

        return maxVal;
//        return minVal * nonVisites.size();
//        return Math.max(maxVal, minVal*nonVisites.size());

//        float minVal = 0;
////        float minVal = Float.MAX_VALUE;
////        for (TupleRequete req : requetes) {
//        int oldInd = ptsIdToIndex.get(requetes.get(0).getCurrentGoal().getId());
//        for(int i=1; i<requetes.size(); i++){
//            int newInd = ptsIdToIndex.get(requetes.get(i).getCurrentGoal().getId());
//            minVal = Math.max(minVal, g[oldInd][newInd].getLongueur());
//            oldInd = newInd;
//            if(requetes.get(i).isDepart()) {
//                newInd = ptsIdToIndex.get(requetes.get(i).getRequete().getDelivery().getId());
//                minVal = Math.max(minVal, g[oldInd][newInd].getLongueur());
//                oldInd = newInd;
//            }
//        }
//        return minVal;
    }

    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] g) {
        return new SeqIter(currentVertex, unvisited, ptsIdToIndex, g);
    }

}
