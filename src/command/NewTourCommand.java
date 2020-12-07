package command;

import controller.MVCController;

public class NewTourCommand implements Command {
    public NewTourCommand() {}

    @Override
    public void doCommand(MVCController c) {}

    @Override
    public void undoCommand(MVCController c) {}
}
