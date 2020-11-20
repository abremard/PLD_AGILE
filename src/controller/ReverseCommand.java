package controller;

public class ReverseCommand implements Command{
    private Command cmd;

    public ReverseCommand(Command cmd) {
        this.cmd = cmd;
    }

    @Override
    public void doCommand() {
        cmd.undoCommand();
    }

    @Override
    public void undoCommand() {
        cmd.doCommand();
    }
}
