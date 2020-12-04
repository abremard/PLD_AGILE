package command;

import objects.PlanningRequest;
import objects.Request;

public class AddRequestCommand implements Command {

    private Request newRequest;

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

    public AddRequestCommand(PlanningRequest oldPlanningRequest, Request newRequest) {
        this.newRequest = newRequest;
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
    }

    @Override
    public void doCommand() {
        this.newPlanningRequest.addRequest(newPlanningRequest.getRequestList().size()-1, newRequest);
    }

    @Override
    public void undoCommand() {
        this.newPlanningRequest.removeRequest(newPlanningRequest.getRequestList().size()-1);
    }

}
