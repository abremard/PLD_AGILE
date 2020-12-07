package command;

import controller.MVCController;

public class ReverseCommand implements Command{
    private Command cmd;

    public ReverseCommand(Command cmd) {
        this.cmd = cmd;
    }

    @Override
    public void doCommand(MVCController c) {
        cmd.undoCommand(c);
    }

    @Override
    public void undoCommand(MVCController c) {
        cmd.doCommand(c);
    }
}
