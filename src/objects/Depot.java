package objects;
import java.time.LocalTime;

public class Depot {
    Intersection adresse;
    LocalTime departureTime;

    public Depot(Intersection adresse, LocalTime departureTime) {
        this.adresse = adresse;
        this.departureTime = departureTime;
    }

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
}
