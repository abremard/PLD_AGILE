package state;

import controller.MVCController;
import command.ListOfCommands;
import command.NewTourCommand;

public class TourState implements State {
    public void newTour(ListOfCommands l, MVCController c) {
        l.Add(new NewTourCommand());
        c.setCurrentState(c.getRequestState());
        if (debug) {
            System.out.print("Adding New Tour Command from TourState to index ");
            System.out.println(l.getI());
        }
    }
}
