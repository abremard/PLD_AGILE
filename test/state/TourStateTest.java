package state;

import com.sothawo.mapjfx.Coordinate;
import command.AddRequestCommand;
import command.ListOfCommands;
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

public class TourStateTest{

    MVCController mvcController;

    @Before
    public void before(){
        mvcController = new MVCController();
        mvcController.setCurrentState(mvcController.getTourState());
    }

    @After
    public void after(){

    }

    @Test
    public void testNewTour() {
        mvcController.getCurrentState().newTour(this.mvcController.getL(), this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getRequestState());
    }

    @Test
    public void testModifyRequestList() {
        mvcController.getCurrentState().modifyRequestList(this.mvcController);
        Assert.assertEquals(mvcController.getCurrentState(), mvcController.getModifyState());
    }
}