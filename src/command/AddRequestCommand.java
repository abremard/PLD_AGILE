package command;

import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;
import state.State;

import java.util.ArrayList;

/**
 * <h1>Add Request Command Class</h1>
 * <p>The Add Request Command defines the command to be called when user confirms the addition of a new request</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class AddRequestCommand implements Command {

    /**
     * The new request that will be added to the controller's planning request.
     */
    private Request newRequest;
    /**
     * The new pickup location tag content that will be added to the controller's ltcList.
     */
    private Controller.LocationTagContent newPickupLtc;
    /**
     * The new delivery location tag content that will be added to the controller's ltcList.
     */
    private Controller.LocationTagContent newDeliveryLtc;

    /**
     * Constructor
     * @param newRequest the request object
     * @param newPickupLtc the pickup ltc object
     * @param newDeliveryLtc the delivery ltc object
     */
    public AddRequestCommand(Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc) {
        this.newRequest = newRequest;
        this.newPickupLtc = newPickupLtc;
        this.newDeliveryLtc = newDeliveryLtc;
    }

    /**
     * Execute command : add newRequest to planning request + add newPickupLtc and newDeliveryLtc to ltcList
     * @param c the MVCController pointer used to update its planning request and ltcList
     */
    @Override
    public void doCommand(MVCController c) {
        c.getPlanningRequest().addRequest(c.getPlanningRequest().getRequestList().size()-1, newRequest);
        c.getLtcList().add(c.getLtcList().size()-1, newPickupLtc);
        c.getLtcList().add(c.getLtcList().size()-1, newDeliveryLtc);

        if(debug){
            System.out.println(c.getLtcList());
        }
    }

    /**
     * Undo command : revert this command's previous execution, remove newRequest + remove newPickupLtc and newDeliveryLtc
     * @param c the MVCController pointer used to update its planning request and ltcList
     */
    @Override
    public void undoCommand(MVCController c) {
        c.getPlanningRequest().removeRequest(newRequest);
        c.getLtcList().remove(c.getLtcList().size()-2);
        c.getLtcList().remove(c.getLtcList().size()-2);

        if(debug){
            System.out.println(c.getLtcList());
        }
    }

}
