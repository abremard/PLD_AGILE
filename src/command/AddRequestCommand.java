package command;

import objects.PlanningRequest;
import objects.Request;

public class AddRequestCommand implements Command {

    private PlanningRequest oldPlanningRequest;
    private Request newRequest;
    private PlanningRequest newPlanningRequest;

    public AddRequestCommand(PlanningRequest oldPlanningRequest, Request newRequest) {
        this.oldPlanningRequest = oldPlanningRequest;
        this.newRequest = newRequest;
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
    }

    @Override
    public void doCommand() {
        this.newPlanningRequest.addRequest(newRequest);
    }

    @Override
    public void undoCommand() {
        this.newPlanningRequest.removeRequest(newPlanningRequest.getRequestList().size()-1);
    }

}
