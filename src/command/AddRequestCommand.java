package command;

import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

/**
 * Commande qui nous permet d'ajouter une requête à la tournée.
 */
public class AddRequestCommand implements Command {

    private Request newRequest;
    private Controller.LocationTagContent newPickupLtc;
    private Controller.LocationTagContent newDeliveryLtc;


    public AddRequestCommand(Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc) {
        this.newRequest = newRequest;
        this.newPickupLtc = newPickupLtc;
        this.newDeliveryLtc = newDeliveryLtc;
    }

    @Override
    public void doCommand(MVCController c) {
        c.getPlanningRequest().addRequest(c.getPlanningRequest().getRequestList().size()-1, newRequest);
        c.getLtcList().add(c.getLtcList().size()-1, newPickupLtc);
        c.getLtcList().add(c.getLtcList().size()-1, newDeliveryLtc);
    }

    @Override
    public void undoCommand(MVCController c) {
        c.getPlanningRequest().removeRequest(c.getPlanningRequest().getRequestList().size()-1);
        c.getLtcList().remove(c.getLtcList().size()-1);
        c.getLtcList().remove(c.getLtcList().size()-1);
    }

}
