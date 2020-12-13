package command;

import com.sothawo.mapjfx.Coordinate;
import controller.MVCController;
import objects.Depot;
import objects.Intersection;
import objects.PlanningRequest;
import objects.Request;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sample.Controller;

import java.util.ArrayList;

public class RemoveRequestCommandTest{

    RemoveRequestCommand removeRequestCommand;
    MVCController mvcController;
    Request request0, request1, request2;
    Depot depot;
    Controller.LocationTagContent newPickupLtc, newDeliveryLtc, depotLtc;

    @Before
    public void before(){

        request0 = new Request(0, new Intersection(45.884784, 4.8456121, 0), new Intersection(45.884451, 4.8715468, 0), 160, 200);
        request1 = new Request(1, new Intersection(45.750404, 4.8744674, 1), new Intersection(45.754433, 4.8718023, 1), 360, 240);
        request2 = new Request(2, new Intersection(45.731080, 4.8979990, 2), new Intersection(45.755142, 4.8716140, 2), 200, 260);
        depot = new Depot(new Intersection(45.7845123, 4.7799845, 3), null);
        newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Saint-Exupery", "Rue de la paix", "15:00", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Saint-Jean", "Part-Dieu", "15:10", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        depotLtc =  new Controller.LocationTagContent("Depot", "Saint-Pierre", "Michelle", "15:20", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);

    }

    @After
    public void after(){

    }

    @Test
    public void testDoCommand() {
        /* prepare expected sample */
        PlanningRequest expPlanningRequest = new PlanningRequest();
        ArrayList<Controller.LocationTagContent> expLtcList = new ArrayList<>();
        expPlanningRequest.addRequest(request0);
        expPlanningRequest.setDepot(depot);
        expLtcList.add(depotLtc);
        /* prepare test sample */
        mvcController = new MVCController();
        mvcController.getPlanningRequest().addRequest(request0);
        mvcController.getPlanningRequest().setDepot(depot);
        mvcController.getPlanningRequest().addRequest(request1);
        mvcController.getLtcList().add(newPickupLtc);
        mvcController.getLtcList().add(newDeliveryLtc);
        mvcController.getLtcList().add(depotLtc);
        /* run command */
        removeRequestCommand = new RemoveRequestCommand(mvcController.getPlanningRequest(), mvcController.getLtcList(), request1, mvcController.getLtcList().size()-3, mvcController.getLtcList().size()-2);
        removeRequestCommand.doCommand(mvcController);
        /* assert */
        Assert.assertEquals(expPlanningRequest, mvcController.getPlanningRequest());
        Assert.assertEquals(expLtcList, mvcController.getLtcList());
    }

    @Test
    public void testUndoCommand() {
        PlanningRequest expPlanningRequest = new PlanningRequest();
        ArrayList<Controller.LocationTagContent> expLtcList = new ArrayList<>();
        expPlanningRequest.addRequest(request0);
        expPlanningRequest.addRequest(request1);
        expPlanningRequest.setDepot(depot);
        expLtcList.add(newPickupLtc);
        expLtcList.add(newDeliveryLtc);
        expLtcList.add(depotLtc);
        /* prepare test sample */
        mvcController = new MVCController();
        mvcController.getPlanningRequest().addRequest(request0);
        mvcController.getPlanningRequest().setDepot(depot);
        mvcController.getLtcList().add(depotLtc);
        removeRequestCommand = new RemoveRequestCommand(expPlanningRequest, expLtcList, request1, expLtcList.size()-3, expLtcList.size()-2);
        /* run command */
        removeRequestCommand.undoCommand(mvcController);
        /* assert */
        Assert.assertEquals(expPlanningRequest, mvcController.getPlanningRequest());
        Assert.assertEquals(expLtcList, mvcController.getLtcList());
    }
}