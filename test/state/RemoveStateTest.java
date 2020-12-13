package state;

import com.sothawo.mapjfx.Coordinate;
import command.AddRequestCommand;
import command.ListOfCommands;
import command.RemoveRequestCommand;
import controller.MVCController;
import objects.Depot;
import objects.Intersection;
import objects.Request;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import sample.Controller;

import java.util.ArrayList;

public class RemoveStateTest{

    MVCController mvcController;

    @Before
    public void before(){
        mvcController = new MVCController();
        mvcController.setCurrentState(mvcController.getRemoveState());
    }

    @After
    public void after(){

    }

    @Test
    public void testRemoveDone() {
        Depot depot = new Depot(new Intersection(45.7845123, 4.7799845, 3), null);
        Request request0 = new Request(0, new Intersection(45.884784, 4.8456121, 0), new Intersection(45.884451, 4.8715468, 0), 160, 200);
        Request request1 = new Request(1, new Intersection(45.750404, 4.8744674, 1), new Intersection(45.754433, 4.8718023, 1), 360, 240);
        mvcController.getPlanningRequest().addRequest(request0);
        mvcController.getPlanningRequest().addRequest(request1);
        mvcController.getPlanningRequest().setDepot(depot);
        Controller.LocationTagContent depotLtc =  new Controller.LocationTagContent("Depot", "Saint-Pierre", "Michelle", "15:20", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        Controller.LocationTagContent newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Saint-Exupery", "Rue de la paix", "15:00", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        Controller.LocationTagContent newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Saint-Jean", "Part-Dieu", "15:10", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);

        mvcController.getLtcList().add(newPickupLtc);
        mvcController.getLtcList().add(newDeliveryLtc);
        mvcController.getLtcList().add(depotLtc);
        mvcController.getCurrentState().removeDone(this.mvcController.getL(), this.mvcController, mvcController.getPlanningRequest(), mvcController.getLtcList(), request1, mvcController.getLtcList().size()-3, mvcController.getLtcList().size()-2);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), RemoveRequestCommand.class);
    }

    @Test
    public void testCancel() {
        mvcController.getCurrentState().cancel(this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
    }
}