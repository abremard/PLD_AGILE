package command;

import controller.MVCController;

/**
 * <h1>Command Class</h1>
 * <p>The Command object defines a user action in time. This allows the system to undo and redo changes</p>
 *
 * @author H4302
 */
public interface Command {

    /**
     * Debug mode : true | false, will print out debug lines to stdout
     */
    boolean debug = true;

    /**
     * Execute command, called initially and when user redoes an action
     */
    void doCommand(MVCController c);
    /**
     * Undo command, called when user undoes an action
     */
    void undoCommand(MVCController c);
}
