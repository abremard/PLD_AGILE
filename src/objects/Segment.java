package objects;

public class Segment {
    int origin;
    int destination;
    Float length;
    Float time;
    String name;

    public Segment(int origin, int destination, Float length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDestination() {
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

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public void setDestination(int destination) {
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
}
