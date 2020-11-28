package objects;

public class Segment {
    Intersection origin;
    Intersection destination;
    Float length;
    Float time;
    String name;

    public Segment(Intersection origin, Intersection destination, Float length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }

    public Intersection getOrigin() {
        return origin;
    }

    public Intersection getDestination() {
        return destination;
    }

    public Float getLength() {
        return length;
    }

    public Float getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }

    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public void setTime(Float time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {      // généré par IntelliJ <3
        return "Segment{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", length=" + length +
                ", time=" + time +
                ", name='" + name + '\'' +
                '}';
    }
}
