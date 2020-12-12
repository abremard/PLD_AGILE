
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

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Depot)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Depot d = (Depot) o;

        // Compare the data members and return accordingly
        return adresse.equals(d.adresse)
                && departureTime.equals(d.departureTime);
    }
}
