package state;

import command.*;
import controller.MVCController;
import objects.Intersection;
import objects.PlanningRequest;
import objects.Map;
import processing.ComputeTour;
import processing.Heuristique;
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
        l.Add(new SwapOrderCommand(a, b, ltcList), c);
        if (debug) {
            System.out.println(l.getI()+" Calling Swap Order Command from ModifyState");
        }
    }

    public void applyModificationDone(ListOfCommands l, MVCController c, Map m, PlanningRequest p, ArrayList<Intersection> order) {
        l.Add(new ApplyModificationCommand(m, p, order), c);
        c.setCurrentState(c.getTourState());
        if (debug) {
            System.out.println(l.getI()+" - Adding ApplyModificationCommand from ModifyState to TourState");
        }
    }

}
