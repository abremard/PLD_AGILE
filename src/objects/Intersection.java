package objects;
public class Intersection {
    double latitude;
    double longitude;
    int id;

    public Intersection(double latitude, double longitude, int id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
