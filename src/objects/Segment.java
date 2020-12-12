
package objects;

/**
 * Classe métier représentant un Segment (rue) sur la carte, c'est-à-dire une arête dans le graphe.
 * Les Segments sont considérés comme étant dirigés, ils ont donc une origine et une destination.
 * @author H4302
 * @see Depot
 */
public class Segment {

    /**
     * ID de l'intersection de laquelle le Segment part.
     */
    long origin;

    /**
     * ID de l'intersection à laquelle le Segment arrive.
     */
    long destination;

    /**
     * Longueur du Segment, en mètres.
     */
    Float length;

    /**
     * Nom du Segment (de la rue).
     */
    String name;

    // Constructeur

    public Segment(long origin, long destination, Float length, String name) {
        this.origin = origin;
        this.destination = destination;
        this.length = length;
        this.name = name;
    }

    // Getters et setters

    public long getOrigin() {
        return origin;
    }

    public long getDestination() {
        return destination;
    }

    public Float getLength() {
        return length;
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

    public void setName(String name) {
        this.name = name;
    }

    // Surcharges

    @Override
    public String toString() {      // généré par IntelliJ <3
        return "Segment{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", length=" + length +
                ", name='" + name + '\'' +
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
        if (!(o instanceof Segment)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Segment s = (Segment) o;

        // Compare the data members and return accordingly
        return origin == s.getOrigin()
                && destination == s.getDestination()
                && length.equals(s.getLength())
                && name.equals(s.getName());
    }
}
