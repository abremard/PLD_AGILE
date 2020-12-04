package command;

import objects.PlanningRequest;

public class SwapOrderCommand implements Command {

    private int a;
    private int b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public PlanningRequest getNewPlanningRequest() {
        return newPlanningRequest;
    }

    public void setNewPlanningRequest(PlanningRequest newPlanningRequest) {
        this.newPlanningRequest = newPlanningRequest;
    }

    private PlanningRequest newPlanningRequest;

    public SwapOrderCommand(int a, int b, PlanningRequest oldPlanningRequest) {
        this.a = a;
        this.b = b;
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
