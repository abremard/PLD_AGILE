package command;

import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

public class RemoveRequestCommand implements Command {

    private int removedRequestIndex;
    private Request removedRequest;
    private int removedCardIndex1;
    private int removedCardIndex2;
    private Controller.LocationTagContent removedCard1;
    private Controller.LocationTagContent removedCard2;
    private PlanningRequest newPlanningRequest;
    private ArrayList<Controller.LocationTagContent> ltc;

    public int getRemovedRequestIndex() {
        return removedRequestIndex;
    }

    public void setRemovedRequestIndex(int removedRequestIndex) {
        this.removedRequestIndex = removedRequestIndex;
    }

    public Request getRemovedRequest() {
        return removedRequest;
    }

    public void setRemovedRequest(Request removedRequest) {
        this.removedRequest = removedRequest;
    }

    public PlanningRequest getNewPlanningRequest() {
        return newPlanningRequest;
    }

    public void setNewPlanningRequest(PlanningRequest newPlanningRequest) {
        this.newPlanningRequest = newPlanningRequest;
    }

    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2) {
        this.removedRequestIndex = removedRequestIndex;

        this.removedRequest = oldPlanningRequest.getRequestList().get(removedRequestIndex);
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.removedCardIndex1 = removedCardIndex1;
        this.removedCardIndex2 = removedCardIndex2;
        this.removedCard1 = ltc.get(removedCardIndex1);
        this.removedCard2 = ltc.get(removedCardIndex2);
    }

    @Override
    public void doCommand() {
        this.newPlanningRequest.removeRequest(removedRequestIndex);

    }

    @Override
    public void undoCommand() {
        this.newPlanningRequest.addRequest(removedRequestIndex, removedRequest);
    }

}
