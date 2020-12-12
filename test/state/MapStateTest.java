package state;

import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import controller.MVCController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* MapState Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 12, 2020</pre> 
* @version 1.0 
*/ 
public class MapStateTest {

    MVCController mvcController;

@Before
public void before() {
    mvcController = new MVCController();
    mvcController.setCurrentState(mvcController.getMapState());
} 

@After
public void after() {
} 

/** 
* 
* Method: loadMap(ListOfCommands l, MVCController c, String p) 
* 
*/ 
@Test
public void testLoadMap() {
    String p = "data/smallMap.xml";
    mvcController.getCurrentState().loadMap(mvcController.getL(), this.mvcController, p);
    Assert.assertEquals(mvcController.getCurrentState(), mvcController.getMapState());
    Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), LoadMapCommand.class);
}

/** 
* 
* Method: loadRequestPlan(ListOfCommands l, MVCController c, String p) 
* 
*/ 
@Test
public void testLoadRequestPlan() {
    String p = "data/requestsSmall1.xml";
    mvcController.getCurrentState().loadRequestPlan(mvcController.getL(), this.mvcController, p);
    Assert.assertEquals(mvcController.getCurrentState(), mvcController.getRequestState());
    Assert.assertEquals(mvcController.getL().getL().get(mvcController.getL().getI()).getClass(), LoadRequestPlanCommand.class);}
} 
