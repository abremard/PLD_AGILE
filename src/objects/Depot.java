
package objects;

import java.time.LocalTime;

/**
 * Classe métier représentant le dépôt d'un planning, c'est-à-dire le point où la tournée doit commencer et finir.
 * @author H4302
 * @see Intersection
 */
public class Depot {

    /**
     * L'intersection où le dépôt est localisé.
     */
    Intersection adresse;

    /**
     * L'heure de départ du dépôt, c'est-à-dire l'heure à laquelle la tournée commence.
     */
    LocalTime departureTime;

    // Constructeur

    public Depot(Intersection adresse, LocalTime departureTime) {
        this.adresse = adresse;
        this.departureTime = departureTime;
    }

    // Getters et setters

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

    // Surcharges

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
