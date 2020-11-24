package state;

import controller.MVCController;
import command.ListOfCommands;
import command.NewTourCommand;
import javafx.stage.Window;

public class TourState implements State {
    public void newTour(ListOfCommands l, MVCController c, Window w) {
        l.Add(new NewTourCommand());
        c.setCurrentState(c.getRequestState());
    }
}
