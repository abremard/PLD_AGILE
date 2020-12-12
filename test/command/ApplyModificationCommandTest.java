package command;

import com.sothawo.mapjfx.Coordinate;
import controller.MVCController;
import objects.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import processing.ComputeTour;
import sample.Controller;

import java.time.LocalTime;
import java.util.ArrayList;

/** 
* ApplyModificationCommand Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 12, 2020</pre> 
* @version 1.0 
*/ 
public class ApplyModificationCommandTest {

    MVCController mvcController;
    ApplyModificationCommand applyModificationCommand;
    Map map;
    PlanningRequest planningRequest;
    ArrayList<Controller.LocationTagContent> ltcList;
    Request request0, request1, request2;
    Depot depot;
    Controller.LocationTagContent newPickupLtc, newDeliveryLtc, depotLtc;

    @Before
public void before() throws Exception {

    request0 = new Request(0, new Intersection(45.760174, 4.877455, 208769457), new Intersection(45.760597, 4.87622, 208769499), 180, 240);
    request1 = new Request(1, new Intersection(45.76038, 4.8775625, 342873658), new Intersection(45.76038, 4.8775625, 342873658), 0, 0);
    request2 = new Request(0, new Intersection(45.760597, 4.87622, 208769499), new Intersection(45.760174, 4.877455, 208769457), 180, 240);
    depot = new Depot(new Intersection(45.76038, 4.8775625, 342873658), LocalTime.of(8, 0, 0, 0));
    ArrayList<Segment> chemin = new ArrayList<>();
    chemin.add(new Segment(342873658, 208769457, Float.parseFloat("24.386381"), "Rue Richelieu"));
    chemin.add(new Segment(208769457, 208769499, Float.parseFloat("106.73056"), "Rue Frédéric Passy"));
    chemin.add(new Segment(208769499, 55475018, Float.parseFloat("96.57731"), "Rue Édouard Aynard"));
    chemin.add(new Segment(55475018, 55475025, Float.parseFloat("12.28458"), "Place Marengo"));
    chemin.add(new Segment(55475025, 208769039, Float.parseFloat("115.5685"), "Avenue Marc Sangnier"));
    newPickupLtc = new Controller.LocationTagContent("Pickup 1", "Avenue Marc Sangnier", "Rue Pascal", "08:04", new Coordinate(45.76069, 4.8749375), chemin, request0);
    newPickupLtc.setIsPickup(true);
    chemin = new ArrayList<>();
    chemin.add(new Segment(208769039, 208769499, Float.parseFloat("100.48389"), "Rue Frédéric Passy"));
    newDeliveryLtc = new Controller.LocationTagContent("Delivery 1", "Rue Édouard Aynard", "Rue Frédéric Passy", "08:08", new Coordinate(45.760597, 4.87622), chemin, request0);
    chemin = new ArrayList<>();
    chemin.add(new Segment(208769499, 208769457, Float.parseFloat("106.73056"), "Rue Frédéric Passy"));
    chemin.add(new Segment(208769457, 342873658, Float.parseFloat("24.386381"), "Rue Richelieu"));
    depotLtc =  new Controller.LocationTagContent("Back to shop", "Rue Richelieu", "Impasse Richelieu", "08:09", new Coordinate(45.76038, 4.8775625), chemin, request1);

}

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: doCommand(MVCController c) 
* 
*/ 
@Test
public void testDoCommand() throws Exception {

    /* prepare test sample */
    map = new Map("data/smallMap.xml");
    planningRequest = new PlanningRequest();
    ltcList = new ArrayList<>();
    mvcController = new MVCController();
    planningRequest.addRequest(request2);
    planningRequest.setDepot(depot);
    ltcList.add(newPickupLtc);
    ltcList.add(newDeliveryLtc);
    ltcList.add(depotLtc);
    System.out.println(planningRequest.toString());
    System.out.println(ltcList.toString());
    ApplyModificationCommand appl = new ApplyModificationCommand(map, planningRequest, ltcList);
    /* prepare expected sample */
    Tournee expTournee = ComputeTour.recreateTourneeWithOrder(map, planningRequest, ltcList);
    /* run command */
    appl.doCommand(mvcController);
    /* assert */
    Assert.assertEquals(expTournee, mvcController.getTour());

}

} 
