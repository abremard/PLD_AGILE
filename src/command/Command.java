package command;

import controller.MVCController;

public interface Command {
    void doCommand(MVCController c);
    void undoCommand(MVCController c);
}
