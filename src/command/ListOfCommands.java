package command;

import controller.MVCController;

import java.util.LinkedList;

/**
 * <h1>List of Commands Class</h1>
 * <p>List of Commands keeps track of the user's history of executed commands, which allows the system to undo/redo changes</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class ListOfCommands {

    /**
     * History of executed commands, kept as a LinkedList
     */
    private LinkedList<Command> l;
    /**
     * Index of the most recent executed command
     */
    private int i;
    /**
     * Index of a lower bound that prevents user from undoing any commands with a lower index
     */
    private int lowerBound;

    /**
     * Getter for history of commands
     * @return the history of commands
     */
    public LinkedList<Command> getL() {
        return l;
    }
    /**
     * Getter for most recent command's index
     * @return the index of the most recent command
     */
    public int getI() {
        return i;
    }

    /**
     * Constructor : index i is initially set to -1 because no command has been executed yet, lower bound is set to 0 meaning user can undo the first command right away if permitted elsewhere
     */
    public ListOfCommands() {
        i = -1;
        l = new LinkedList<>();
        lowerBound = 0;
    }

    /**
     * Add a command object to the history of commands and execute it systematically
     * @param cmd the command being added
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void Add(Command cmd, MVCController c) {
        i++;
        if (i == l.size()) {
            l.add(i, cmd);
        }
        else {
            l = new LinkedList<>(l.subList(0, i+1));
            l.set(i, cmd);
        }
        cmd.doCommand(c);
    }

    /**
     * Call undo function of the most recent command and decrement the index by 1
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void Undo(MVCController c) {
        if (i>=lowerBound) {
            System.out.println("Undo command number "+i);
            l.get(i).undoCommand(c);
            i--;
        }
    }

    /**
     * For redo action, we will first need to increment the index, then execute the appropriate command (that has already been executed in the past)
     * @param c the MVCController object being passed to the command execution for updating purposes
     */
    public void Redo(MVCController c) {
        if (i<l.size()-1) {
            i++;
            l.get(i).doCommand(c);
            System.out.println("Redo command number "+i);
        }
    }

    /**
     * When user applies modifications (ApplyModificationDone function), a new lowerBound is being set so that modifications are saved and user can no longer undo the modifications that happened before the apply modification function
     * @param lowerBound the new lower bound
     */
    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }
}
