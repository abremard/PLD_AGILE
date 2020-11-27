package processing;

import objects.Request;

import java.time.LocalTime;
import java.util.Objects;

public class TupleRequete {

    Request requete;
    boolean isDepart;       // vrai si l'objet TupleRequete correspond au départ de sa Request
    LocalTime time;     // pickup or delivery starting time according to isDepart

    public TupleRequete(Request requete, boolean isDepart) {
        this.requete = requete;
        this.isDepart = isDepart;
    }

    public TupleRequete(Request requete, boolean isDepart, LocalTime time) {
        this.requete = requete;
        this.isDepart = isDepart;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TupleRequete that = (TupleRequete) o;
        return isDepart == that.isDepart &&
                requete.equals(that.requete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requete, isDepart);
    }
}
