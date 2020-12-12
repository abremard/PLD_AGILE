
package objects;

import java.time.LocalTime;

/**
 * Business class representing the departure address of a planning : the point where the Tour must start and end.
 * @author H4302
 * @see Intersection
 */
public class Depot {

    /**
     * The Intersection where the departure address is located.
     */
    Intersection adresse;

    /**
     * The time of departure : time at which the Tour starts.
     */
    LocalTime departureTime;

    // Constructor

    public Depot(Intersection adresse, LocalTime departureTime) {
        this.adresse = adresse;
        this.departureTime = departureTime;
    }

    // Getters and setters

    public Intersection getAdresse() {
        return adresse;
    }

    public void setAdresse(Intersection adresse) {
        this.adresse = adresse;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    // Overrides

    @Override
    public String toString() {
        return "Depot{" +
                "adresse=" + adresse +
                ", departureTime=" + departureTime +
                '}';
    }
}
