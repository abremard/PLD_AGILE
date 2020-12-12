package state;

import com.sothawo.mapjfx.Coordinate;
import command.AddRequestCommand;
import command.EditRequestCommand;
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

/** 
* ModifyRequestState Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 12, 2020</pre> 
* @version 1.0 
*/ 
public class ModifyRequestStateTest {

    MVCController mvcController;
    private int editedRequestIndex;
    private int editedCardIndex;
    private double oldDuration;
    private double newDuration;
    private boolean isPickup;
    private Request newRequest;
    private Request oldRequest;
    Depot depot;
    Controller.LocationTagContent newPickupLtc, newDeliveryLtc, depotLtc;

@Before
public void before() {
    mvcController = new MVCController();
    mvcController.setCurrentState(mvcController.getModifyRequestState());
    oldRequest = new Request(0, new Intersection(45.884784, 4.8456121, 0), new Intersection(45.884451, 4.8715468, 0), 200, 200);
    newRequest = new Request(1, new Intersection(45.750404, 4.8744674, 1), new Intersection(45.754433, 4.8718023, 1), 100, 240);
    depot = new Depot(new Intersection(45.7845123, 4.7799845, 3), null);
    newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Saint-Exupery", "Rue de la paix", "15:00", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), newRequest);
    newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Saint-Jean", "Part-Dieu", "15:10", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), newRequest);
    depotLtc =  new Controller.LocationTagContent("Depot", "Saint-Pierre", "Michelle", "15:20", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), newRequest);
    oldDuration = 200.0;
    newDuration = 100.0;
    isPickup = true;
    editedCardIndex = 0;
    editedRequestIndex = 0;
    mvcController.getPlanningRequest().addRequest(oldRequest);
    mvcController.getPlanningRequest().setDepot(depot);
    mvcController.getLtcList().add(newPickupLtc);
    mvcController.getLtcList().add(newDeliveryLtc);
    mvcController.getLtcList().add(depotLtc);
} 

@After
public void after() {
} 

/** 
* 
* Method: modifyRequestDone(ListOfCommands l, MVCController c, Request oldRequest, Request newRequest, int editedRequestIndex, int editedCardIndex, double oldDuration, double newDuration, boolean isPickup) 
* 
*/ 
@Test
public void testModifyRequestDone() {
    mvcController.getCurrentState().modifyRequestDone(mvcController.getL(), mvcController, oldRequest, newRequest, editedRequestIndex, editedCardIndex, oldDuration, newDuration, isPickup);
    Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
    Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), EditRequestCommand.class);
}

/** 
* 
* Method: cancel(MVCController c) 
* 
*/ 
@Test
public void testCancel() {
    mvcController.getCurrentState().cancel(this.mvcController);
    Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
}


} 
