package command;

import com.sothawo.mapjfx.Coordinate;
import command.ApplyModificationCommand;
import controller.MVCController;
import objects.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import processing.ComputeTour;
import processing.Heuristique;
import sample.Controller;

import java.time.LocalTime;
import java.util.ArrayList;

/** 
* ComputeTourCommand Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 12, 2020</pre> 
* @version 1.0 
*/ 
public class ComputeTourCommandTest {

    MVCController mvcController;
    ComputeTourCommand computeTourCommand;
    Map map;
    PlanningRequest planningRequest;
    Request request0, request1, request2;
    Depot depot;

@Before
public void before() {

    request0 = new Request(0, new Intersection(45.760174, 4.877455, 208769457), new Intersection(45.760597, 4.87622, 208769499), 180, 240);
    request1 = new Request(1, new Intersection(45.76038, 4.8775625, 342873658), new Intersection(45.76038, 4.8775625, 342873658), 0, 0);
    request2 = new Request(0, new Intersection(45.760597, 4.87622, 208769499), new Intersection(45.760174, 4.877455, 208769457), 180, 240);
    depot = new Depot(new Intersection(45.76038, 4.8775625, 342873658), LocalTime.of(8, 0, 0, 0));

} 

@After
public void after() {
}
/** 
* 
* Method: doCommand(MVCController c) 
* 
*/ 
@Test
public void testDoCommand() {

    /* prepare test sample */
    map = new Map("data/smallMap.xml");
    planningRequest = new PlanningRequest();
    mvcController = new MVCController();
    planningRequest.addRequest(request2);
    planningRequest.setDepot(depot);
    computeTourCommand = new ComputeTourCommand(map, planningRequest);
    /* prepare expected sample */
    Tournee expTournee = ComputeTour.planTour(map, planningRequest, Heuristique.DOUBLEINSERTION);
    /* run command */
    computeTourCommand.doCommand(mvcController);
    /* assert */
    Assert.assertEquals(expTournee, mvcController.getTour());}

} 
