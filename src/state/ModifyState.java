package state;

import command.*;
import controller.MVCController;
import objects.PlanningRequest;
import objects.Map;
import sample.Controller;

import java.util.ArrayList;

//TODO littéralement tout sauf la transition entre ModifyState et AddState

public class ModifyState implements State {
    public void addRequest(MVCController c){
        c.setCurrentState(c.getAddState());
        if (debug) {
            System.out.println("Going from ModifyState to AddState");
        }
    }

    public void removeRequest(MVCController c){
        c.setCurrentState(c.getRemoveState());
        if (debug) {
            System.out.println("Going from ModifyState to RemoveState");
        }
    }

    public void modifyRequest(MVCController c){
        c.setCurrentState(c.getModifyRequestState());
        if (debug) {
            System.out.println("Going from ModifyState to ModifyRequestState");
        }
    }

    public void swapRequest(ListOfCommands l, MVCController c, int a, int b, ArrayList<Controller.LocationTagContent> ltcList){
        l.Add(new SwapOrderCommand(a, b, ltcList));
        SwapOrderCommand swapOrderCommand = (SwapOrderCommand) l.getL().get(l.getI());
        c.setLtcList(swapOrderCommand.getLtcList());
        if (debug) {
            System.out.print("Calling Swap Order Command");
            System.out.println(l.getI());
        }
    }

    public void applyModificationDone(ListOfCommands l, MVCController c, Map m, PlanningRequest p) {
        l.Add(new ApplyModificationCommand(m, p));
        c.setCurrentState(c.getTourState());
        if (debug) {
            System.out.print(l.getI());
            System.out.println(" - Confirming all the modification to the request list ");
        }
    }

    public void calculateTour(ListOfCommands l, MVCController c, PlanningRequest p, Map m) {
        if (p != null && m != null) {
            l.Add(new ComputeTourCommand(m, p));
            ComputeTourCommand computeTourCommand = (ComputeTourCommand) l.getL().get(l.getI());
            c.setTour(computeTourCommand.getTournee());
            if (debug) {
                System.out.print(l.getI());
                System.out.println(" - Adding Calculate Tour Command from Modify State to index ");
            }
        }
    }
}
