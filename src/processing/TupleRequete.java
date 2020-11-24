package processing;

import objects.Request;

import java.util.Objects;

public class TupleRequete {

    Request requete;
    boolean isDepart;

    public TupleRequete(Request requete, boolean isDepart) {
        this.requete = requete;
        this.isDepart = isDepart;
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
