
package objects;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Business class representing a request, containing the Intersections of delivery and pickup, as well as the
 * pickup and delivery durations.
 * @author H4302
 * @see Intersection
 */
public class Request {

    /**
     * The Intersection of the Request's delivery.
     */
    Intersection pickup;

    /**
     * The Intersection of the Request's pickup.
     */
    Intersection delivery;

    /**
     * The necessary duration to complete the pickup, in seconds.
     */
    double pickupDur;

    /**
     * The necessary duration to complete the delivery, in seconds.
     */
    double deliveryDur;

    /**
     * The ID of the Request.
     */
    int id;

    // Constructors

    public Request(int id, Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur) {
        this.pickup = pickup;
        this.delivery = delivery;
        this.pickupDur = pickupDur;
        this.deliveryDur = deliveryDur;
        this.id = id;
    }

    public Request(Request referencedRequest) {
        this.pickup = referencedRequest.pickup;
        this.delivery = referencedRequest.delivery;
        this.pickupDur = referencedRequest.pickupDur;
        this.deliveryDur = referencedRequest.deliveryDur;
        this.id = referencedRequest.id;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

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

    // Overrides

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
