package objects;

import objects.*;
import org.junit.Assert;
import org.junit.Test;
public class SegmentTest {
    @Test
    public void testSetDestination() {
        Segment segmentTest = new Segment(1,2,3f,"test");
        segmentTest.setDestination(3);
        Assert.assertEquals(3, segmentTest.getDestination(),0);
    }
    @Test
    public void testSetOrigin() {
        Segment segmentTest = new Segment(1,2,3f,"test");
        segmentTest.setOrigin(3);
        Assert.assertEquals(3, segmentTest.getOrigin(),0);
    }
    @Test
    public void testSetLength() {
        Segment segmentTest = new Segment(1,2,3f,"test");
        segmentTest.setLength(8f);
        Assert.assertEquals(2.0, segmentTest.getDestination(),0);
    }
    @Test
    public void testSetName() {
        Segment segmentTest = new Segment(1,2,3f,"test");
        segmentTest.setName("test2");
        Assert.assertEquals(segmentTest.getName(),"test2");
    }

    @Test
    public void testEquals() {
        Segment segmentTest = new Segment(1,2,3f,"test");
        Segment segmentTest2 = new Segment(1,2,3f,"test2");
        Assert.assertTrue(segmentTest.equals(segmentTest));
        Assert.assertFalse(segmentTest.equals(segmentTest2));
        Assert.assertFalse(segmentTest.equals(2));
    }
    @Test
    public void testToString() {
        Segment segmentTest = new Segment(1, 2, 3f, "test");
        Assert.assertEquals(segmentTest.toString(), "Segment{" + "origin=1" + ", destination=2" + ", length=3.0" + ", name='test" + '\'' + '}');
    }
}
