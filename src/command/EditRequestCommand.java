package command;

import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import processing.ComputeTour;
import sample.Controller;

import java.util.ArrayList;

/**
 * <h1>Edit Request Command Class</h1>
 * <p>The Edit Request Command defines the command to be called when user confirms the modification of a request</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class EditRequestCommand implements Command {

    /**
     * The index of the Request edited in the list
     */
    private int editedRequestIndex;
    /**
     * The index of the Location Tag Content being edited in the list
     */
    private int editedCardIndex;
    /**
     * The old duration of the pickup/delivery, used for command undo
     */
    private double oldDuration;
    /**
     * The new duration of the pickup/delivery, used for initial execution and command redo
     */
    private double newDuration;
    /**
     * Nature of the card being modified, pickup OR delivery
     */
    private boolean isPickup;
    /**
     * New Request object, used for initial execution and command redo
     */
    private Request newRequest;
    /**
     * Old Request object, used for command undo
     */
    private Request oldRequest;

    /**
     * Constructor
     * @param oldRequest old Request object
     * @param newRequest new Request object
     * @param editedRequestIndex index of the Request object
     * @param editedCardIndex index of the Location Tag Content object
     * @param oldDuration old duration of the pickup/delivery
     * @param newDuration new duration of the pickup/delivery
     * @param isPickup is the card a pickup or delivery
     */
    public EditRequestCommand(Request oldRequest, Request newRequest, int editedRequestIndex, int editedCardIndex, double oldDuration, double newDuration, boolean isPickup) {
        this.editedRequestIndex = editedRequestIndex;
        this.editedCardIndex = editedCardIndex;
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.oldDuration = oldDuration;
        this.newDuration = newDuration;
        this.isPickup = isPickup;
    }

    /**
     * Execute command : replace old request with new one + replace old card with new one
     * @param c the MVCController pointer used to update ltcList and planningRequest
     */
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

        if(debug){
            System.out.println(c.getPlanningRequest());
        }
    }

    /**
     * Undo command : replace new request with old one + replace new card with old one
     * @param c the MVCController pointer used to update ltcList and planningRequest
     */
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

        if(debug){
            System.out.println(c.getPlanningRequest());
        }
    }

}
