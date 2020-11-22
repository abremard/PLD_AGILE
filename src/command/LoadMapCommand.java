package command;
import objects.Map;

public class LoadMapCommand implements Command {
    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Map map;
    private String path;

    public LoadMapCommand(String p) {
        this.path = p;
    }

    @Override
    public void doCommand() {
        map = new Map(path);
    }

    @Override
    public void undoCommand() {
        // TODO
    }
}
