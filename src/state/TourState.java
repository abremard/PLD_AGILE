package state;

import command.SwapOrderCommand;
import controller.MVCController;
import command.ListOfCommands;
import command.NewTourCommand;
import objects.PlanningRequest;

public class TourState implements State {
    public void newTour(ListOfCommands l, MVCController c) {
        l.Add(new NewTourCommand());
        c.setCurrentState(c.getRequestState());
        if (debug) {
            System.out.print(l.getI());
            System.out.println(" - Adding New Tour Command from TourState to index ");
        }
    }

    public void modifyRequestList(MVCController c) {
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.print("Going from TourState to ModifyState ");
        }
    }

}
