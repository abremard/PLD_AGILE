package command;

import objects.PlanningRequest;
import objects.Request;

public class RemoveRequestCommand implements Command {

    private PlanningRequest oldPlanningRequest;
    private int removedRequestIndex;
    private Request removedRequest;
    private PlanningRequest newPlanningRequest;

    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, int removedRequestIndex) {
        this.oldPlanningRequest = oldPlanningRequest;
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
        this.newPlanningRequest.addRequest(removedRequest);
    }

}
