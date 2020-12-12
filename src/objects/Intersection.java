
package objects;

/**
 * Business class representing an Intersection on the map, that is to say a node in the graph.
 * @author H4302
 * @see Depot
 */
public class Intersection {

    /**
     * Latitude at which the Intersection is located.
     *
     */
    double latitude;

    /**
     * Longitude at which the Intersection is located.
     */
    double longitude;

    /**
     * ID of the Intersection, as defined in the XML file of the map.
     */
    long id;

    /**
     * ID of the icon of the marker to be displayed on the map for this Intersection.
     */
    String markerId;

    // Constructors

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

    // Getters and setters

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

    // Overrides

    @Override
    public String toString() {
        return "Intersection{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Intersection)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Intersection i = (Intersection) o;
        
        return latitude == i.getLatitude()
                    && longitude == i.getLongitude()
                        && id == i.getId();
        
        /*
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
        }*/
    }
}
