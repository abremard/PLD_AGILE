package controller;

import objects.Map;
import objects.PlanningRequest;

public class Controller {
    private ListOfCommands l;
    // for now ignore states, undo & redos will be done on the 2nd iteration

    public void LoadMap(String path) {
        LoadMapCommand loadMapCommand = new LoadMapCommand(path);
        loadMapCommand.doCommand();
    }

    public void LoadRequestPlan(String path) {
        LoadRequestPlanCommand loadRequestPlanCommand = new LoadRequestPlanCommand(path);
        loadRequestPlanCommand.doCommand();
    }

    // Compute tour object ??

}
