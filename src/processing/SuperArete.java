
package processing;

import objects.Intersection;
import objects.Segment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representing an edge in the optimal full sub-graph of the Map.
 * It is as such the shortest path between two interest points, as a sequence of Segments.
 *
 * @author H4302
 * @see ComputeTour
 */
public class SuperArete {

    /**
     * The path that this object represents.
     */
    ArrayList<Segment> chemin;

    /**
     * Interest point at the beginning of the path.
     */
    Intersection depart;

    /**
     * Interest point at the end of the path.
     */
    Intersection arrivee;

    /**
     * Length of the path contained in this SuperArete.
     */
    float longueur;

    // Constructor

    public SuperArete(ArrayList<Segment> chemin, ArrayList<Intersection> intersections, HashMap<Long, Integer> intersecIdToIndex) {
        this.chemin = chemin;
        depart = intersections.get(intersecIdToIndex.get(chemin.get(0).getOrigin()));
        arrivee = intersections.get(intersecIdToIndex.get(chemin.get(chemin.size() - 1).getDestination()));
        longueur = 0;
        for (Segment seg : chemin) {
            longueur += seg.getLength();
        }
    }

    // Overrides

    @Override
    public String toString() {
        return "SuperArete{" +
                "depart=" + depart +
                "arrivee=" + arrivee +
                "longueur=" + longueur +
                '}';
    }

    // Getters and setters

    public float getLongueur() {
        return longueur;
    }

    public ArrayList<Segment> getChemin() {
        return chemin;
    }

    public Intersection getDepart() {
        return depart;
    }

    public Intersection getArrivee() {
        return arrivee;
    }
}
