package command;

import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import processing.ComputeTour;
import sample.Controller;

import java.util.ArrayList;

/**
 * Commande qui nous permet de modifier une requête de la tournée.
 */
public class EditRequestCommand implements Command {

    private int editedRequestIndex;
    private int editedCardIndex;
    private double oldDuration;
    private double newDuration;
    private boolean isPickup;
    private Request newRequest;
    private Request oldRequest;

    public EditRequestCommand(Request oldRequest, Request newRequest, int editedRequestIndex, int editedCardIndex, double oldDuration, double newDuration, boolean isPickup) {
        this.editedRequestIndex = editedRequestIndex;
        this.editedCardIndex = editedCardIndex;
        /*
        if (isPickup) {
            this.oldDuration = p.getRequestList().get(editedRequestIndex).getPickupDur();
        } else {
            this.oldDuration = p.getRequestList().get(editedRequestIndex).getDeliveryDur();
        }
        */
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.oldDuration = oldDuration;
        this.newDuration = newDuration;
        this.isPickup = isPickup;
    }

    @Override
    public void doCommand(MVCController c) {
        c.getPlanningRequest().getRequestList().set(editedRequestIndex, newRequest);
        c.getLtcList().get(editedCardIndex).setRequest(newRequest);
        if (isPickup) {
            c.getPlanningRequest().getRequestList().get(editedRequestIndex).setPickupDur(this.newDuration);
            c.getLtcList().get(editedCardIndex).getRequest().setPickupDur(this.newDuration);
        } else {
            c.getPlanningRequest().getRequestList().get(editedRequestIndex).setDeliveryDur(this.newDuration);
            c.getLtcList().get(editedCardIndex).getRequest().setDeliveryDur(this.newDuration);
        }
    }

    @Override
    public void undoCommand(MVCController c) {
        c.getPlanningRequest().getRequestList().set(editedRequestIndex, oldRequest);
        c.getLtcList().get(editedCardIndex).setRequest(oldRequest);
        if (isPickup) {
            c.getPlanningRequest().getRequestList().get(editedRequestIndex).setPickupDur(this.oldDuration);
            c.getLtcList().get(editedCardIndex).getRequest().setPickupDur(this.oldDuration);
        } else {
            c.getPlanningRequest().getRequestList().get(editedRequestIndex).setDeliveryDur(this.oldDuration);
            c.getLtcList().get(editedCardIndex).getRequest().setDeliveryDur(this.oldDuration);
        }
    }

}
