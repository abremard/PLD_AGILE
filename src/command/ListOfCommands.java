package command;

import java.util.LinkedList;

/**
 * Cette classe permet d'implémenter le design pattern Command qui rend possible la fonctionnalité undo/redo
 */
public class ListOfCommands {

    /** Historique des commandes sous forme de liste chaînée **/
    private LinkedList<Command> l;
    /** Index du dernier élément ajouté **/
    private int i;

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
    }

    /**
     * Ajouter une nouvelle commande dans la liste des commandes
     *
     * @param cmd la command à ajouter
     */
    public void Add(Command cmd) {
        i++;
        l.add(i, cmd);
        cmd.doCommand();
    }
    public void Undo() {
        if (i>=0) {
            l.get(i).undoCommand();
            i--;
            if (i>=0) {
                l.get(i).doCommand();
            }
        }
    }
    public void Redo() {
        i++;
        l.get(i).doCommand();
    }

}
