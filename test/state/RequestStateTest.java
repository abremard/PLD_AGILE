package state;

import com.sothawo.mapjfx.Coordinate;
import command.*;
import controller.MVCController;
import objects.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import sample.Controller;

import java.time.LocalTime;
import java.util.ArrayList;

public class RequestStateTest{

    MVCController mvcController;

    @Before
    public void before(){
        mvcController = new MVCController();
        mvcController.setCurrentState(mvcController.getRequestState());
    }

    @After
    public void after(){

    }

    @Test
    public void testLoadMap() {
        String p = "data/smallMap.xml";
        mvcController.getCurrentState().loadMap(mvcController.getL(), this.mvcController, p);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getRequestState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), LoadMapCommand.class);
    }

    @Test
    public void testLoadRequestPlan() {
        String p = "data/requestsSmall1.xml";
        mvcController.getCurrentState().loadRequestPlan(mvcController.getL(), this.mvcController, p);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getRequestState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), LoadRequestPlanCommand.class);
}

    @Test
    public void testCalculateTour() {
        Request request0 = new Request(0, new Intersection(45.760174, 4.877455, 208769457), new Intersection(45.760597, 4.87622, 208769499), 180, 240);
        Request request1 = new Request(1, new Intersection(45.76038, 4.8775625, 342873658), new Intersection(45.76038, 4.8775625, 342873658), 0, 0);
        Request request2 = new Request(0, new Intersection(45.760597, 4.87622, 208769499), new Intersection(45.760174, 4.877455, 208769457), 180, 240);
        Depot depot = new Depot(new Intersection(45.76038, 4.8775625, 342873658), LocalTime.of(8, 0, 0, 0));

        Map map = new Map("data/smallMap.xml");
        PlanningRequest planningRequest = new PlanningRequest();
        planningRequest.addRequest(request2);
        planningRequest.setDepot(depot);

        mvcController.setMap(map);
        mvcController.setPlanningRequest(planningRequest);

        mvcController.getCurrentState().calculateTour(mvcController.getL(), this.mvcController, mvcController.getPlanningRequest(), mvcController.getMap());
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getTourState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), ComputeTourCommand.class);

    }
}