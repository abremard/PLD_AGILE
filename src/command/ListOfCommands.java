package command;

import java.util.LinkedList;

public class ListOfCommands {

    private LinkedList<Command> l;
    private int i;

    public LinkedList<Command> getL() {
        return l;
    }

    public void setL(LinkedList<Command> l) {
        this.l = l;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public ListOfCommands() {
        i = -1;
        l = new LinkedList<>();
    }
    public void Add(Command cmd) {
        i++;
        l.add(i, cmd);
        cmd.doCommand();
    }
    public void Undo() {
        if (i>=0) {
            l.get(i).undoCommand();
            i--;
        }
    }
    public void Redo() {
        i++;
        l.get(i).doCommand();
    }

}
