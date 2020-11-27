package command;
import objects.Map;

public class LoadMapCommand implements Command {

    /** classe attributes **/
    private Map map;
    private String path;

    /** getters & setters **/
    public Map getMap() {
        return map;
    }

    /** constructor **/
    public LoadMapCommand(String p) {
        this.path = p;
    }

    @Override
    public void doCommand() {
        map = new Map(path);
    }

    @Override
    public void undoCommand() { map = null; }
}
