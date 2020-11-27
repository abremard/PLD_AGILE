
package processing;

import objects.Intersection;
import objects.Segment;

import java.util.ArrayList;
import java.util.HashMap;

public class SuperArete {

    float longueur;
    ArrayList<Segment> chemin;
    Intersection depart, arrivee;

    public SuperArete(ArrayList<Segment> chemin, ArrayList<Intersection> intersections, HashMap<Long, Integer> intersecIdToIndex) {
        this.chemin = chemin;
        depart = intersections.get(intersecIdToIndex.get(chemin.get(0).getOrigin()));
        arrivee = intersections.get(intersecIdToIndex.get(chemin.get(chemin.size()-1).getDestination()));
        longueur = 0;
        for (Segment seg : chemin) {
            longueur += seg.getLength();
        }
    }

    @Override
    public String toString() {
        return "SuperArete{" +
                "depart=" + depart +
                "arrivee=" + arrivee +
                "longueur=" + longueur +
                '}';
    }

}
