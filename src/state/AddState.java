package state;

import command.ListOfCommands;
import command.AddRequestCommand;
import command.SwapOrderCommand;
import controller.MVCController;
import objects.Map;
import objects.PlanningRequest;
import objects.Request;
import processing.ComputeTour;
import processing.Heuristique;
import sample.Controller;

import java.util.ArrayList;

public class AddState implements State {
    public void addDone(ListOfCommands l, MVCController c, Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc) {
        l.Add(new AddRequestCommand(newRequest, newPickupLtc, newDeliveryLtc), c);
        c.setCurrentState(c.getModifyState());

        if (debug) {
            System.out.println(l.getI()+" - Confirming addition of a new request to the request list ");
        }
    }

    public void cancel(MVCController c){
        c.setCurrentState(c.getModifyState());
        if (debug) {
            System.out.println("Going from AddState to ModifyState without having changed anything");
        }
    }
}
