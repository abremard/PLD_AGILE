package command;

import controller.MVCController;
import junit.framework.TestCase;
import objects.Map;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoadMapCommandTest{

    Map referenceMap;
    MVCController mvcController;
    LoadMapCommand loadMapCommand;

    @Before
    public void before() {
        referenceMap = new Map("data/smallMap.xml");
    }

    @After
    public void after() {

    }

    @Test
    public void testDoCommand() {
        String documentPath = "data/smallMap.xml";
        mvcController = new MVCController();
        loadMapCommand = new LoadMapCommand(documentPath);
        /* run command */
        loadMapCommand.doCommand(mvcController);

        Assert.assertEquals(referenceMap.getIntersectionList(), mvcController.getMap().getIntersectionList());
        Assert.assertEquals(referenceMap.getNoOfIntersections(), mvcController.getMap().getNoOfIntersections());
        Assert.assertEquals(referenceMap.getSegmentList(), mvcController.getMap().getSegmentList());
        Assert.assertEquals(referenceMap.getNoOfSegments(), mvcController.getMap().getNoOfSegments());

    }
}