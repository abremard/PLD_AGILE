
package Branch_And_Bound_TSP;

import processing.SuperArete;
import processing.TupleRequete;

import java.util.*;

/**
 * Inheritor implementing an improved bound function to improve computating speed.
 *
 * @see Template_BnB_TSP
 */
public class BnB_TSP2 extends Template_BnB_TSP {

    /**
     * Computes a lower bound of the length of the path left to travel to complete all remaining Requests.
     * Returns the sum of the lengths of the paths from each node to its closest successor.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param requetes      The list of Requests left to deal with
     * @return a lower bound of the cost of paths in <code>matAdj</code> starting from <code>currentVertex</code>,
     * visiting every interest point in <code>requetes</code>, and returning back to vertex <code>0</code>.
     */
    @Override
    protected float bound(Integer currentVertex, ArrayList<TupleRequete> requetes) {
        if (requetes.size() == 1) {
            if (currentVertex == 0) {
                return 0;
            }
            return matAdj[currentVertex][0].getLongueur();
        }

        LinkedList<Integer> nonVisites = new LinkedList<Integer>();
        for (TupleRequete req : requetes) {
            nonVisites.add(ptsIdToIndex.get(req.getCurrentGoal().getId()));
            if (req.isDepart()) {
                nonVisites.add(ptsIdToIndex.get(req.getRequete().getDelivery().getId()));
            }
        }

//        float maxVal = 0;
        float minSum = 0;
        float minVal = Float.MAX_VALUE;
        float longueur = 0;

        for (Integer ind1 : nonVisites) {
            if (ind1 != 0 && matAdj[0][ind1].getLongueur() < minVal) {
                minVal = matAdj[0][ind1].getLongueur();
            }
        }
        minSum += minVal;

        for (Integer ind1 : nonVisites) {
//            if(! ind1.equals(currentVertex)) {
//                longueur = g[currentVertex][ind1].getLongueur();
//                maxVal = Math.max(maxVal, longueur);
//            }

            minVal = Float.MAX_VALUE;
            for (Integer ind2 : nonVisites) {
                if (ind1.equals(ind2)) {
                    continue;
                }
                longueur = matAdj[ind1][ind2].getLongueur();
                minVal = Math.min(minVal, longueur);
            }
            if (ind1 != 0) {
                longueur = matAdj[ind1][0].getLongueur();
                minVal = Math.min(minVal, longueur);
            }
            minSum += minVal;
        }

        return minSum;
//        return Math.max(maxVal, minSum);
    }

    /**
     * Creates an iterator that scrolls through all remaining interest points.
     * Scrolls in the immediate order of <code>unvisited</code>.
     *
     * @param currentVertex The index of the current node (interest point)
     * @param unvisited     The unordered list of nodes (interest points) that must still be visited
     * @param ptsIdToIndex  The dictionnary associating and Intersection ID with its index in the adjacence matrix
     * @param matAdj        The optimal full sub-graph of the map as an adjacence matrix
     * @return an iterator for visiting all remaining interest points in <code>requetes</code>.
     */
    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<TupleRequete> unvisited, HashMap<Long, Integer> ptsIdToIndex, SuperArete[][] matAdj) {
        return new SeqIter(currentVertex, unvisited, ptsIdToIndex, matAdj);
    }

}
