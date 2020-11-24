package state;

import controller.MVCController;
import command.ListOfCommands;
import javafx.stage.Window;
import objects.Map;
import objects.PlanningRequest;

public interface State {
    default void loadMap(ListOfCommands l, MVCController c, Window w, String p) {}
    default void loadRequestPlan(ListOfCommands l, MVCController c, Window w, String p) {}
    default void calculateTour(ListOfCommands l, MVCController c, Window w, PlanningRequest p, Map m) {}
    default void newTour(ListOfCommands l, MVCController c, Window w) {}
    default void edit(ListOfCommands l, MVCController c, Window w) {}
    default void add(ListOfCommands l, MVCController c, Window w) {}
    default void undo(ListOfCommands l) {}
    default void redo(ListOfCommands l) {}
    default void confirmDelete(ListOfCommands l, MVCController c, Window w) {}
    default void confirmAdd(ListOfCommands l, MVCController c, Window w) {}
    // default void leftClick(ListOfCommands l, Controller c, Window w, Point p) {}
    default void rightClick(ListOfCommands l, MVCController c, Window w) {
        c.setCurrentState(c.getTourState());
        // w.displayMessage("Command cancelled");
    }
}
