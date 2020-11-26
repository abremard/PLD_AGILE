package state;

import controller.MVCController;
import command.ListOfCommands;
import javafx.stage.Window;
import objects.Map;
import objects.PlanningRequest;

public interface State {
    default void loadMap(ListOfCommands l, MVCController c, String p) {}
    default void loadRequestPlan(ListOfCommands l, MVCController c, String p) {}
    default void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {}
    default void newTour(ListOfCommands l, MVCController c) {}
    default void edit(ListOfCommands l, MVCController c) {}
    default void add(ListOfCommands l, MVCController c) {}
    default void undo(ListOfCommands l) { l.Undo(); }
    default void redo(ListOfCommands l) { l.Redo(); }
    default void confirmDelete(ListOfCommands l, MVCController c) {}
    default void confirmAdd(ListOfCommands l, MVCController c) {}
    // default void leftClick(ListOfCommands l, Controller c, Point p) {}
    default void rightClick(ListOfCommands l, MVCController c) {
        c.setCurrentState(c.getTourState());
        // w.displayMessage("Command cancelled");
    }
}
