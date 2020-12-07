package command;

import controller.MVCController;

import java.util.LinkedList;

/**
 * Cette classe permet d'implémenter le design pattern Command qui rend possible la fonctionnalité undo/redo
 */
public class ListOfCommands {

    /** Historique des commandes sous forme de liste chaînée **/
    private LinkedList<Command> l;
    /** Index de la dernière commande executée **/
    private int i;

    private int lowerBound;

    /** getters & setters **/
    public LinkedList<Command> getL() {
        return l;
    }
    public int getI() {
        return i;
    }

    /** constructor **/
    public ListOfCommands() {
        i = -1;
        l = new LinkedList<>();
        lowerBound = 0;
    }

    /**
     * Ajouter une nouvelle commande dans la liste des commandes
     *
     * @param cmd la command à ajouter
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
    public void Undo(MVCController c) {
        if (i>=lowerBound) {
            System.out.println("Undo command number "+i);
            l.get(i).undoCommand(c);
            i--;
        }
    }
    public void Redo(MVCController c) {
        if (i<l.size()-1) {
            i++;
            l.get(i).doCommand(c);
            System.out.println("Redo command number "+i);
        }
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }
}
