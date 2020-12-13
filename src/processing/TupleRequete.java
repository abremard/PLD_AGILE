package processing;

import objects.Intersection;
import objects.Request;
import objects.Segment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class that represents an interest point, as a starting point or end point of a Request.
 *
 * @author H4302
 * @see ComputeTour
 */
public class TupleRequete {

    /**
     * The Request which starting point or end point is the interest point that this object represents.
     */
    Request requete;

    /**
     * Indicates whether the interest point is the starting point of the end point of the Request.
     */
    boolean isDepart;

    /**
     * The time at which the pickup or delivery will start, according to the Tour computation.
     */
    LocalTime time;

    /**
     * The path to go from the previous interest point to this point, in the order of the computed Tour.
     */
    ArrayList<Segment> chemin;

    // Constructors

    public TupleRequete(Request requete, boolean isDepart) {
        this.requete = requete;
        this.isDepart = isDepart;
    }

    public TupleRequete(Request requete, boolean isDepart, LocalTime time, ArrayList<Segment> chemin) {
        this.requete = requete;
        this.isDepart = isDepart;
        this.time = time;
        this.chemin = chemin;
    }

    // Getters and setters

    public ArrayList<Segment> getChemin() {
        return chemin;
    }

    public void setChemin(ArrayList<Segment> chemin) {
        this.chemin = chemin;
    }

    public Request getRequete() {
        return requete;
    }

    public void setRequete(Request requete) {
        this.requete = requete;
    }

    public Intersection getCurrentGoal() {
        if (isDepart) {
            return requete.getPickup();
        } else {
            return requete.getDelivery();
        }
    }

    public boolean isDepart() {
        return isDepart;
    }

    public void setDepart(boolean depart) {
        isDepart = depart;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setFrom(TupleRequete tupleRequete) {
        this.setRequete(tupleRequete.requete);
        this.setDepart(tupleRequete.isDepart());
    }

    // Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleRequete that = (TupleRequete) o;
        return isDepart == that.isDepart &&
                requete.equals(that.requete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requete, isDepart);
    }
}
