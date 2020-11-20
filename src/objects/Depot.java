package objects;
import java.time.LocalDateTime;

public class Depot {
    Intersection adresse;
    LocalDateTime departureTime;

    public Depot(Intersection adresse, LocalDateTime departureTime) {
        this.adresse = adresse;
        this.departureTime = departureTime;
    }

    public Intersection getAdresse() {
        return adresse;
    }

    public void setAdresse(Intersection adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }
}
