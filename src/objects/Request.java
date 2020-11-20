package objects;

import java.time.LocalDateTime;

public class Request {
    Intersection pickup;
    Intersection delivery;
    double pickupDur;
    int deliveryDur;
    LocalDateTime startTime;

    public Request(Intersection pickup, Intersection delivery, double pickupDur, int deliveryDur, LocalDateTime startTime) {
        this.pickup = pickup;
        this.delivery = delivery;
        this.pickupDur = pickupDur;
        this.deliveryDur = deliveryDur;
        this.startTime = startTime;
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

    public int getDeliveryDur() {
        return deliveryDur;
    }

    public void setDelivery_dur(int deliveryDur) {
        this.deliveryDur = deliveryDur;
    }
}
