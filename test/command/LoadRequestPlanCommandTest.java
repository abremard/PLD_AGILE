package command;

import controller.MVCController;
import objects.PlanningRequest;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class LoadRequestPlanCommandTest{

    LoadRequestPlanCommand loadRequestPlanCommand;
    PlanningRequest planningRequest;
    MVCController mvcController;

    @Before
    public void before(){
        String path = "data/requestsSmall1.xml";
        planningRequest = new PlanningRequest();
        try {
            planningRequest.parseRequest(path);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @After
    public void after(){

    }

    @Test
    public void testDoCommand() {
        String documentPath = "data/requestsSmall1.xml";
        mvcController = new MVCController();
        loadRequestPlanCommand = new LoadRequestPlanCommand(documentPath);
        /* run command */
        loadRequestPlanCommand.doCommand(mvcController);
        Assert.assertEquals(planningRequest.getDepot(), mvcController.getPlanningRequest().getDepot());
        Assert.assertEquals(planningRequest.getRequestList(), mvcController.getPlanningRequest().getRequestList());
    }

}