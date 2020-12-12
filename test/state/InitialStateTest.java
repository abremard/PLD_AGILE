package state;

import command.AddRequestCommand;
import command.LoadMapCommand;
import controller.MVCController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* InitialState Tester. 
* 
* @author <Authors name> 
* @since <pre>Dec 12, 2020</pre> 
* @version 1.0 
*/ 
public class InitialStateTest {

    MVCController mvcController;

@Before
public void before() {
    mvcController = new MVCController();
    mvcController.setCurrentState(mvcController.getInitialState());
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


} 
