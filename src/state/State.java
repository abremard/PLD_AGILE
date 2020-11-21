package state;

import controller.Controller;
import command.ListOfCommands;
import javafx.stage.Window;
import objects.Map;
import objects.PlanningRequest;

public interface State {
    default void loadMap(ListOfCommands l, Controller c, Window w, String p) {}
    default void loadRequestPlan(ListOfCommands l, Controller c, Window w, String p) {}
    default void calculateTour(ListOfCommands l, Controller c, Window w, PlanningRequest p, Map m) {}
    default void newTour(ListOfCommands l, Controller c, Window w) {}
    default void edit(ListOfCommands l, Controller c, Window w) {}
    default void add(ListOfCommands l, Controller c, Window w) {}
    default void undo(ListOfCommands l) {}
    default void redo(ListOfCommands l) {}
    default void confirmDelete(ListOfCommands l, Controller c, Window w) {}
    default void confirmAdd(ListOfCommands l, Controller c, Window w) {}
    // default void leftClick(ListOfCommands l, Controller c, Window w, Point p) {}
    default void rightClick(ListOfCommands l, Controller c, Window w) {
        c.setCurrentState(c.getTourState());
        // w.displayMessage("Command cancelled");
    }
}
