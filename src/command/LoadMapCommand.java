package command;
import controller.MVCController;
import objects.Map;

/**
 * <h1>Load Map Command Class</h1>
 * <p>The Load Map Command defines the command to be called when user wants to load a map onto the application</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class LoadMapCommand implements Command {

    /**
     * The path to the file in the File Explorer (it's relative to the project's root)
     */
    private String path;

    /**
     * constructor
     * @param p The path to the file
     */
    public LoadMapCommand(String p) {
        this.path = p;
    }

    /**
     * Execute Command : Set the MvcController's map as the one corresponding to the path
     * @param c the MVCController pointer used to update its map
     */
    @Override
    public void doCommand(MVCController c) {
        c.setMap(new Map(path));
    }

    @Override
    public void undoCommand(MVCController c) {}
}
