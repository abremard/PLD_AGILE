package objects;

import objects.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DepotTest {
    @Test
    public void testCreation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        Depot testedDepot = new Depot(new Intersection(1,2,3),dateDepot);
        Assert.assertNotEquals(testedDepot.toString(),"Depot{adresse=Intersection{latitude=, longitude=, id=}, departureTime=}");
    }

    @Test
    public void testGetAdresse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        Depot testedDepot = new Depot(new Intersection(1,2,3),dateDepot);
        Assert.assertEquals(testedDepot.getAdresse(),new Intersection(1,2,3));
    }

    @Test
    public void testSetAdresse() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        Depot testedDepot = new Depot(new Intersection(1,2,3),dateDepot);
        testedDepot.setAdresse(new Intersection(2,3,4));
        Assert.assertEquals(testedDepot.getAdresse(),new Intersection(2,3,4));
    }

    @Test
    public void testGetDepartureTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        Depot testedDepot = new Depot(new Intersection(1,2,3),dateDepot);
        Assert.assertEquals(testedDepot.getDepartureTime(),dateDepot);
    }

    @Test
    public void testSetDepartureTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        Depot testedDepot = new Depot(new Intersection(1,2,3),dateDepot);
        LocalTime dateDepotTest = LocalTime.parse("9:1:2", formatter);
        testedDepot.setDepartureTime(dateDepotTest);
        Assert.assertEquals(testedDepot.getDepartureTime(),dateDepotTest);
    }

    @Test
    public void testEquals() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:m:s", Locale.ENGLISH);
        LocalTime dateDepot = LocalTime.parse("8:0:0", formatter);
        LocalTime dateDepot2 = LocalTime.parse("3:2:0", formatter);
        Depot testedDepot1 = new Depot(new Intersection(1,2,3),dateDepot);
        Depot testedDepot2 = new Depot(new Intersection(1,2,3),dateDepot);
        Depot testedDepot3 = new Depot(new Intersection(0,2,3),dateDepot);
        Depot testedDepot4 = new Depot(new Intersection(0,2,3),dateDepot2);
        Assert.assertTrue(testedDepot1.equals(testedDepot2));
        Assert.assertTrue(testedDepot2.equals(testedDepot1));
        Assert.assertFalse(testedDepot1.equals(testedDepot3));
        Assert.assertFalse(testedDepot3.equals(testedDepot4));
        Assert.assertFalse(testedDepot1.equals(2));
    }
}
