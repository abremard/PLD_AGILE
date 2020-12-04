package command;

import objects.PlanningRequest;
import objects.Request;

public class RemoveRequestCommand implements Command {

    private int removedRequestIndex;
    private Request removedRequest;

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

    private PlanningRequest newPlanningRequest;

    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, int removedRequestIndex) {
        this.removedRequestIndex = removedRequestIndex;
        this.removedRequest = oldPlanningRequest.getRequestList().get(removedRequestIndex);
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
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
