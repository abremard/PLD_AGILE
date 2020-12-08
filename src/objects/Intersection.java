
package objects;

/**
 * Classe métier représentant une intersection sur la carte, c'est-à-dire un noeud dans le graphe.
 * @author H4302
 * @see Depot
 */
public class Intersection {

    /**
     * Latitude à laquelle se situe l'intersection.
     */
    double latitude;

    /**
     * Longitude à laquelle se situe l'intersection.
     */
    double longitude;

    /**
     * ID de l'intersection, tel que définit dans les fichiers XML de map.
     */
    long id;

    /**
     * ID de l'icône du marqueur à afficher sur la map pour cette intersection.
     */
    String markerId;

    // Constructeurs

    public Intersection(double latitude, double longitude, long id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public Intersection(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Intersection(long id) {
        this.id = id;
    }

    // Getters et setters

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    // Surcharges

    @Override
    public String toString() {
        return "Intersection{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this.getClass() == object.getClass()) {
            Intersection intersection = (Intersection) object;
            if ((this.getLatitude() == intersection.getLatitude()) && (this.getLongitude() == intersection.getLongitude()) && (this.getId() == intersection.getId())) {
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
}
