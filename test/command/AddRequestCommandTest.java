package command;

import controller.MVCController;
import objects.Intersection;
import objects.PlanningRequest;
import objects.Request;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.Assert.*;
import sample.Controller;

import java.util.ArrayList;

/** 
* AddRequestCommand Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 8, 2020</pre> 
* @version 1.0 

public class AddRequestCommandTest {

    AddRequestCommand addRequestCommand;
    MVCController mvcController;

@Before
public void before() throws Exception {

    PlanningRequest oldPlanningRequest = new PlanningRequest();
    Request tmpRequest = new Request(new Intersection(45.750404, 4.8744674), new Intersection(45.754433, 4.8718023), 360, 240);
    oldPlanningRequest.addRequest(tmpRequest);
    ArrayList<Controller.LocationTagContent> ltcList = new ArrayList<>();
    Request newRequest = new Request(new Intersection(45.73108, 4.897999), new Intersection(45.755142, 4.871614), 200, 260);

    addRequestCommand = new AddRequestCommand(oldPlanningRequest, ltcList, newRequest);
    mvcController = new MVCController();

} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: setNewLtcList(ArrayList<Controller.LocationTagContent> newLtcList) 
*
@Test
public void testSetNewLtcList() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getNewPlanningRequest() 
*
@Test
public void testGetNewPlanningRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setNewPlanningRequest(PlanningRequest newPlanningRequest) 
*
@Test
public void testSetNewPlanningRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: doCommand(MVCController c) 
*
@Test
public void testDoCommand() throws Exception {

    addRequestCommand.doCommand(mvcController);
    //Assert.assertEquals("");

} 

/** 
* 
* Method: undoCommand(MVCController c) 
*
@Test
public void testUndoCommand() throws Exception { 
//TODO: Test goes here... 
} 


} 
*/