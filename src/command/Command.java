package command;

import controller.MVCController;

public interface Command {

    boolean debug = true;

    void doCommand(MVCController c);
    void undoCommand(MVCController c);
}
