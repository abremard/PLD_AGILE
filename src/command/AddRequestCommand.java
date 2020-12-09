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
    private PlanningRequest newPlanningRequest;

    public void setNewLtcList(ArrayList<Controller.LocationTagContent> newLtcList) {
        this.newLtcList = newLtcList;
    }

    private ArrayList<Controller.LocationTagContent> newLtcList;

    public PlanningRequest getNewPlanningRequest() {
        return newPlanningRequest;
    }

    public void setNewPlanningRequest(PlanningRequest newPlanningRequest) {
        this.newPlanningRequest = newPlanningRequest;
    }

    public AddRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc) {
        this.newRequest = newRequest;
        this.newPickupLtc = newPickupLtc;
        this.newDeliveryLtc = newDeliveryLtc;
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.newLtcList = new ArrayList<>(ltcList);
    }

    @Override
    public void doCommand(MVCController c) {
        this.newPlanningRequest.addRequest(newPlanningRequest.getRequestList().size()-1, newRequest);
        c.setPlanningRequest(newPlanningRequest);
        this.newLtcList.add(newLtcList.size()-1, newPickupLtc);
        this.newLtcList.add(newLtcList.size()-1, newDeliveryLtc);
        c.setLtcList(this.newLtcList);
    }

    @Override
    public void undoCommand(MVCController c) {
        this.newPlanningRequest.removeRequest(newPlanningRequest.getRequestList().size()-1);
        c.setPlanningRequest(newPlanningRequest);
        this.newLtcList.remove(newLtcList.size()-1);
        this.newLtcList.remove(newLtcList.size()-1);
        c.setLtcList(this.newLtcList);
    }

}
