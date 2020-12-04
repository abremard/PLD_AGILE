package objects;

import java.time.LocalTime;
import java.util.Objects;

public class Request {

    static int nbInstances = 0;

    Intersection pickup;
    Intersection delivery;
    // les durées sont en secondes
    double pickupDur;
    double deliveryDur;
    LocalTime startTime;
    int id;

    public Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur) {
        this.pickup = pickup;
        this.delivery = delivery;
        this.pickupDur = pickupDur;
        this.deliveryDur = deliveryDur;

        this.id = nbInstances;
        ++nbInstances;
    }

    public Request(Intersection pickup, Intersection delivery, double pickupDur, double deliveryDur, LocalTime startTime) {
        this.pickup = pickup;
        this.delivery = delivery;
        this.pickupDur = pickupDur;
        this.deliveryDur = deliveryDur;
        this.startTime = startTime;

        this.id = nbInstances;
        ++nbInstances;
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

    @Override
    public String toString() {
        return "Request{" +
                "pickup=" + pickup +
                ", delivery=" + delivery +
                ", pickupDur=" + pickupDur +
                ", deliveryDur=" + deliveryDur +
                ", startTime=" + startTime +
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
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
