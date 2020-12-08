
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
}
