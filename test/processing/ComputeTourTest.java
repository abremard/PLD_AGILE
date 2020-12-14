
package processing;

import objects.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ComputeTour Tester.
 *
 * @author H3402
 * @see ComputeTour
 */
public class ComputeTourTest {

    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    /**
     * Method : public static Tournee planTour(Map map, PlanningRequest planning, Heuristique heuristique)
     */
    @Test
    public void testComputeTour() {
        String mapPath = "data/map_test.xml";
        String reqPath = "data/requests_test.xml";

        // ------------ chargement & parsing des données de test
        Map map = new Map(mapPath);
        PlanningRequest planning = new PlanningRequest();
        try {
            planning.parseRequest(reqPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tournee tournee = ComputeTour.planTour(map, planning, Heuristique.DOUBLEINSERTION);

        // ------------ construction de la reponse attendue
        Segment s01 = new Segment(12, 123, 3f, "S01");
        Segment s12 = new Segment(123, 234, 4f, "S12");
        Segment s25 = new Segment(234, 567, 2f, "S25");
        Segment s51 = new Segment(567, 123, 8f, "S51");
        Segment s23 = new Segment(234, 345, 5f, "S23");
        Segment s30 = new Segment(345, 12, 6f, "S30");
        Intersection i012 = new Intersection(45.75406, 4.857418, 12);
        Intersection i123 = new Intersection(45.75406, 4.857418, 123);
        Intersection i234 = new Intersection(45.750404, 4.8744674, 234);
        Intersection i345 = new Intersection(45.75871, 4.8704023, 345);
        Intersection i456 = new Intersection(45.75871, 4.8718166, 456);
        Intersection i567 = new Intersection(45.750896, 4.859119, 567);
        Request r1 = new Request(1, i123, i567, 420, 600);
        Request r2 = new Request(2, i123, i345, 200, 300);
        Segment[] seg1 = {s01};
        ArrayList<Segment> segL1 = new ArrayList<>(Arrays.asList(seg1));
        TupleRequete t1 = new TupleRequete(r1, true, LocalTime.of(8, 7), segL1);
        TupleRequete t2 = new TupleRequete(r2, true, LocalTime.of(8, 10, 20), segL1);
        Segment[] seg3 = {s12, s25};
        ArrayList<Segment> segL3 = new ArrayList<>(Arrays.asList(seg1));
        TupleRequete t3 = new TupleRequete(r1, false, LocalTime.of(8, 20, 20), segL3);
        Segment[] seg4 = {s51, s12, s23};
        ArrayList<Segment> segL4 = new ArrayList<>(Arrays.asList(seg1));
        TupleRequete t4 = new TupleRequete(r2, false, LocalTime.of(8, 25, 22), segL4);
        Segment[] seg5 = {s30};
        ArrayList<Segment> segL5 = new ArrayList<>(Arrays.asList(seg1));
        TupleRequete t5 = new TupleRequete(null, false, LocalTime.of(8, 25, 23), segL5);

        Segment[] segments = {s01, s12, s25, s51, s12, s23, s30};
        ArrayList<Segment> segmentList = new ArrayList<>(Arrays.asList(segments));
        TupleRequete[] treqs = {t1, t2, t3, t4, t5};
        ArrayList<TupleRequete> treqList = new ArrayList<>(Arrays.asList(treqs));

        Tournee tourneeRef = new Tournee(segmentList, planning.getRequestList(), treqList);

        Assert.assertEquals(tournee.getSegmentList(), tourneeRef.getSegmentList());
        Assert.assertEquals(tournee.getRequestList(), tourneeRef.getRequestList());
        Assert.assertEquals(tournee.getPtsPassage().get(tournee.getPtsPassage().size()-1).time, tourneeRef.getPtsPassage().get(tourneeRef.getPtsPassage().size()-1).time);
    }

}
