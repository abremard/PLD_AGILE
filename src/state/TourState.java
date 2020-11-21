package state;

import controller.Controller;
import command.ListOfCommands;
import command.NewTourCommand;
import javafx.stage.Window;

public class TourState implements State {
    public void newTour(ListOfCommands l, Controller c, Window w) {
        l.Add(new NewTourCommand());
        c.setCurrentState(c.getInitialState());
    }
}
