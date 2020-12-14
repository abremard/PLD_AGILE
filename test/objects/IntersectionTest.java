package objects;

import objects.*;
import org.junit.Assert;
import org.junit.Test;


public class IntersectionTest {
    @Test
    public void testCreation() {
        Intersection intersectionTest = new Intersection(1, 2);
        Assert.assertNotEquals(intersectionTest.toString(), "Intersection{latitude=, longitude=, id=}");
    }

    @Test
    public void testSetLatitudeAndLongitude() {
        Intersection intersectionTest = new Intersection(1, 2);
        intersectionTest.setLatitude(2);
        intersectionTest.setLongitude(1);
        Assert.assertEquals(2, intersectionTest.getLatitude(),0);
        Assert.assertEquals(1, intersectionTest.getLongitude(),0);
    }

    @Test
    public void testId() {
        Intersection intersectionTest = new Intersection(1,2,3);
        intersectionTest.setId(4);
        intersectionTest.setLongitude(1);
        intersectionTest.setMarkerId("0");
        Assert.assertEquals(4, intersectionTest.getId(),0);
        Assert.assertEquals("0",intersectionTest.getMarkerId());
    }

    @Test
    public void testEquals() {
        Intersection intersectionTest = new Intersection(1,2,3);
        Intersection intersectionTest2 = new Intersection(1,2,3);
        Intersection intersectionTest3 = new Intersection(2,3,4);
        Assert.assertFalse(intersectionTest.equals(2));
        Assert.assertFalse(intersectionTest.equals(intersectionTest3));
        Assert.assertTrue(intersectionTest.equals(intersectionTest2));
    }
}