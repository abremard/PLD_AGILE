package state;

import command.SwapOrderCommand;
import controller.MVCController;
import command.ListOfCommands;
import command.NewTourCommand;
import objects.PlanningRequest;

public class TourState implements State {
    public void newTour(ListOfCommands l, MVCController c) {
        l.Add(new NewTourCommand(), c);
        c.setCurrentState(c.getRequestState());
        if (debug) {
            System.out.println(l.getI()+" - Adding New Tour Command from TourState to RequestState");
        }
    }

    public void modifyRequestList(MVCController c) {
        c.setCurrentState(c.getModifyState());
        c.getL().setLowerBound(c.getL().getI()+1);
        if (debug) {
            System.out.println("Going from TourState to ModifyState");
            System.out.println("Lower bound is set to "+(c.getL().getI()+1));
        }
    }

}
