package objects;

public class Segment {
    long origin;
    long destination;
    Float length;
    Float time;
    String name;

    public Segment(long origin, long destination, Float length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }

    public long getOrigin() {
        return origin;
    }

    public long getDestination() {
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

    public void setOrigin(long origin) {
        this.origin = origin;
    }

    public void setDestination(long destination) {
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
