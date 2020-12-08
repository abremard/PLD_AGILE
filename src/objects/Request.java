
package objects;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Classe métier représentant une requête, contenant les Intersections de récupération et de dépôt, ainsi que
 * les durées de récupération et de dépôt.
 * @author H4302
 * @see Intersection
 */
public class Request {

    /**
     * Compte le nombre d'instances de Request qui ont été construites.
     */
    static int nbInstances = 0;

    /**
     * L'intersection de récupération de la requête
     */
    Intersection pickup;

    /**
     * L'intersection de dépôt de la requête
     */
    Intersection delivery;

    /**
     * La durée nécessaire pour compléter la récupération, en secondes.
     */
    double pickupDur;

    /**
     * La durée nécessaire pour compléter le dépôt, en secondes.
     */
    double deliveryDur;

    /**
     * L'ID de la requête.
     */
    int id;

    // Constructeur

    public Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur) {
        this.pickup = pickup;
        this.delivery = delivery;
        this.pickupDur = pickupDur;
        this.deliveryDur = deliveryDur;

        this.id = nbInstances;
        ++nbInstances;
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public Intersection getPickup() {
        return pickup;
    }

    public void setPickup(Intersection pickup) {
        this.pickup = pickup;
    }

    public Intersection getDelivery() {
        return delivery;
    }

    public void setDelivery(Intersection delivery) {
        this.delivery = delivery;
    }

    public double getPickupDur() {
        return pickupDur;
    }

    public void setPickupDur(double pickupDur) {
        this.pickupDur = pickupDur;
    }

    public double getDeliveryDur() {
        return deliveryDur;
    }

    public void setDeliveryDur(double deliveryDur) {
        this.deliveryDur = deliveryDur;
    }

    // Surcharges

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", pickup=" + pickup +
                ", delivery=" + delivery +
                ", pickupDur=" + pickupDur +
                ", deliveryDur=" + deliveryDur +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this.getClass() == object.getClass()) {
            Request request = (Request) object;
            if ((this.getDelivery() == request.getDelivery()) && (this.getPickup() == request.getPickup()) && (this.getDeliveryDur() == request.getDeliveryDur()) && (this.getPickupDur() == request.getPickupDur())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
