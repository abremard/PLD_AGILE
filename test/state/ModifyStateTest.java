package state;

import com.sothawo.mapjfx.Coordinate;
import command.*;
import controller.MVCController;
import objects.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.xml.sax.SAXException;
import sample.Controller;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

public class ModifyStateTest{

    MVCController mvcController;

    @Before
    public void before(){
        mvcController = new MVCController();
        mvcController.setCurrentState(mvcController.getModifyState());
    }

    @After
    public void after(){

    }

    @Test
    public void testAddRequest() {
        mvcController.getCurrentState().addRequest(this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getAddState());
    }

    @Test
    public void testRemoveRequest() {
        mvcController.getCurrentState().removeRequest(this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getRemoveState());
    }

    @Test
    public void testModifyRequest() {
        mvcController.getCurrentState().modifyRequest(this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyRequestState());
    }

    @Test
    public void testSwapRequest() {
        Request request0 = new Request(0, new Intersection(45.884784, 4.8456121, 0), new Intersection(45.884451, 4.8715468, 0), 160, 200);
        Request request1 = new Request(1, new Intersection(45.750404, 4.8744674, 1), new Intersection(45.754433, 4.8718023, 1), 360, 240);
        Request request2 = new Request(2, new Intersection(45.731080, 4.8979990, 2), new Intersection(45.755142, 4.8716140, 2), 200, 260);
        Depot depot = new Depot(new Intersection(45.7845123, 4.7799845, 3), null);
        Controller.LocationTagContent newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Saint-Exupery", "Rue de la paix", "15:00", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        Controller.LocationTagContent newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Saint-Jean", "Part-Dieu", "15:10", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);
        Controller.LocationTagContent depotLtc =  new Controller.LocationTagContent("Depot", "Saint-Pierre", "Michelle", "15:20", new Coordinate(45.741886, 4.8938003), new ArrayList<>(), request1);

        mvcController.getPlanningRequest().setDepot(depot);
        mvcController.getPlanningRequest().addRequest(request0);
        mvcController.getPlanningRequest().addRequest(request1);

        mvcController.getLtcList().add(depotLtc);
        mvcController.getLtcList().add(newPickupLtc);
        mvcController.getLtcList().add(newDeliveryLtc);

        mvcController.getCurrentState().swapRequest(this.mvcController.getL(), this.mvcController, 0, 1, mvcController.getLtcList());
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), SwapOrderCommand.class);

    }

    @Test
    public void testApplyModificationDone() {

        Request request0 = new Request(0, new Intersection(45.760174, 4.877455, 208769457), new Intersection(45.760597, 4.87622, 208769499), 180, 240);
        Request request1 = new Request(1, new Intersection(45.76038, 4.8775625, 342873658), new Intersection(45.76038, 4.8775625, 342873658), 0, 0);
        Request request2 = new Request(0, new Intersection(45.760597, 4.87622, 208769499), new Intersection(45.760174, 4.877455, 208769457), 180, 240);
        Depot depot = new Depot(new Intersection(45.76038, 4.8775625, 342873658), LocalTime.of(8, 0, 0, 0));
        ArrayList<Segment> chemin = new ArrayList<>();
        chemin.add(new Segment(342873658, 208769457, Float.parseFloat("24.386381"), "Rue Richelieu"));
        chemin.add(new Segment(208769457, 208769499, Float.parseFloat("106.73056"), "Rue Frédéric Passy"));
        chemin.add(new Segment(208769499, 55475018, Float.parseFloat("96.57731"), "Rue Édouard Aynard"));
        chemin.add(new Segment(55475018, 55475025, Float.parseFloat("12.28458"), "Place Marengo"));
        chemin.add(new Segment(55475025, 208769039, Float.parseFloat("115.5685"), "Avenue Marc Sangnier"));
        Controller.LocationTagContent newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Avenue Marc Sangnier", "Rue Pascal", "08:04", new Coordinate(45.76069, 4.8749375), chemin, request0);
        newPickupLtc.setIsPickup(true);
        chemin = new ArrayList<>();
        chemin.add(new Segment(208769039, 208769499, Float.parseFloat("100.48389"), "Rue Frédéric Passy"));
        Controller.LocationTagContent newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Rue Édouard Aynard", "Rue Frédéric Passy", "08:08", new Coordinate(45.760597, 4.87622), chemin, request0);
        chemin = new ArrayList<>();
        chemin.add(new Segment(208769499, 208769457, Float.parseFloat("106.73056"), "Rue Frédéric Passy"));
        chemin.add(new Segment(208769457, 342873658, Float.parseFloat("24.386381"), "Rue Richelieu"));
        Controller.LocationTagContent depotLtc =  new Controller.LocationTagContent("Back to shop", "Rue Richelieu", "Impasse Richelieu", "08:09", new Coordinate(45.76038, 4.8775625), chemin, request1);

        Map map = new Map("data/smallMap.xml");
        PlanningRequest planningRequest = new PlanningRequest();
        ArrayList<Controller.LocationTagContent> ltcList = new ArrayList<>();
        planningRequest.addRequest(request2);
        planningRequest.setDepot(depot);
        ltcList.add(newPickupLtc);
        ltcList.add(newDeliveryLtc);
        ltcList.add(depotLtc);

        mvcController.setPlanningRequest(planningRequest);
        mvcController.setMap(map);
        mvcController.setLtcList(ltcList);

        mvcController.getCurrentState().applyModificationDone(this.mvcController.getL(), this.mvcController, mvcController.getMap(), mvcController.getPlanningRequest(),mvcController.getLtcList());
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getTourState());
        Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), ApplyModificationCommand.class);

    }
}
