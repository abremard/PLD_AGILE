package command;
import controller.MVCController;
import objects.Map;

/**
 * Commande qui nous permet de charger une carte sur l'IHM.
 */
public class LoadMapCommand implements Command {

    /** classe attributes **/
    private String path;

    /** constructor **/
    public LoadMapCommand(String p) {
        this.path = p;
    }

    @Override
    public void doCommand(MVCController c) {
        c.setMap(new Map(path));
    }

    @Override
    public void undoCommand(MVCController c) {}
}
