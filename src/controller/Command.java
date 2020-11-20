package controller;

public interface Command {
    void doCommand();
    void undoCommand();
}
