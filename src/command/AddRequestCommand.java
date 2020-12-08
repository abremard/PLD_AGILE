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
    private ArrayList<Controller.LocationTagContent> oldLtcList;

    public ArrayList<Controller.LocationTagContent> getNewLtcList() {
        return newLtcList;
    }

    public void setNewLtcList(ArrayList<Controller.LocationTagContent> newLtcList) {
        this.newLtcList = newLtcList;
    }

    private ArrayList<Controller.LocationTagContent> newLtcList;

    public Request getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(Request newRequest) {
        this.newRequest = newRequest;
    }

    public PlanningRequest getNewPlanningRequest() {
        return newPlanningRequest;
    }

    public void setNewPlanningRequest(PlanningRequest newPlanningRequest) {
        this.newPlanningRequest = newPlanningRequest;
    }

    private PlanningRequest newPlanningRequest;

    public AddRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, Request newRequest) {
        this.newRequest = newRequest;
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.oldLtcList = new ArrayList<>(ltcList);
        this.newLtcList = null;
    }

    @Override
    public void doCommand(MVCController c) {
        this.newPlanningRequest.addRequest(newPlanningRequest.getRequestList().size()-1, newRequest);
        c.setPlanningRequest(newPlanningRequest);
        if (newLtcList != null) {
            c.setLtcList(newLtcList);
        }
    }

    @Override
    public void undoCommand(MVCController c) {
        this.newPlanningRequest.removeRequest(newPlanningRequest.getRequestList().size()-1);
        c.setPlanningRequest(newPlanningRequest);
        c.setLtcList(this.oldLtcList);
    }

}
