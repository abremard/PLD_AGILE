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

    private Request oldRequest;
    private Request newRequest;
    private Request returnedRequest;
    private Map map;
    private PlanningRequest planningRequest;
    private ArrayList<Controller.LocationTagContent> ltcList;

    public EditRequestCommand(Request oldRequest, Request newRequest, Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList) {
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.returnedRequest = newRequest;
        this.map = m;
        this.planningRequest = p;
        this.ltcList = ltcList;
    }

    @Override
    public void doCommand(MVCController c) {
        returnedRequest = newRequest;
        c.setTour(ComputeTour.recreateTourneeWithOrder(map, planningRequest, ltcList));
    }

    @Override
    public void undoCommand(MVCController c) { returnedRequest = oldRequest; }

    public Request getOldRequest() {
        return oldRequest;
    }

    public void setOldRequest(Request oldRequest) {
        this.oldRequest = oldRequest;
    }

    public Request getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(Request newRequest) {
        this.newRequest = newRequest;
    }

    public Request getReturnedRequest() {
        return returnedRequest;
    }

    public void setReturnedRequest(Request returnedRequest) {
        this.returnedRequest = returnedRequest;
    }
}
