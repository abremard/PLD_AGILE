package command;

import objects.PlanningRequest;

public class NewTourCommand implements Command {
    private PlanningRequest planningRequest;
    private String path;

    public NewTourCommand() {}

    @Override
    public void doCommand() {
        // TODO clear/reset window
    }

    @Override
    public void undoCommand() {
        // TODO
    }
}
