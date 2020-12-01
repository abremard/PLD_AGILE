package command;

import objects.PlanningRequest;

public class SwapOrderCommand implements Command {

    private int a;
    private int b;
    private PlanningRequest oldPlanningRequest;
    private PlanningRequest newPlanningRequest;

    public SwapOrderCommand(int a, int b, PlanningRequest oldPlanningRequest) {
        this.a = a;
        this.b = b;
        this.oldPlanningRequest = oldPlanningRequest;
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
    }

    @Override
    public void doCommand() {
        this.newPlanningRequest.swapRequest(a, b);
    }

    @Override
    public void undoCommand() {
        this.newPlanningRequest.swapRequest(a, b);
    }

}
