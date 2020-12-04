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

    public void modifyRequestList(MVCController c) {
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Going from TourState to ModifyState ");
        }
    }
}
