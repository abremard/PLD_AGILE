package command;

// TODO : if same as compute tour command then delete this class...

import controller.MVCController;
import objects.Intersection;
import objects.Map;
import objects.PlanningRequest;
import objects.Tournee;
import processing.ComputeTour;
import processing.Heuristique;
import sample.Controller;

import java.util.ArrayList;

/**
 * Commande qui nous permet de valider les modifications de la tournée.
 */
public class ApplyModificationCommand implements Command {

    private Map map;
    private PlanningRequest planningRequest;
    private ArrayList<Controller.LocationTagContent> ltcList;

    public ApplyModificationCommand(Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList) {
        this.map = m;
        this.planningRequest = p;
        this.ltcList = ltcList;
    }

    @Override
    public void doCommand(MVCController c) {
        System.out.println(map.getIntersectionList().toString());
        System.out.println(map.getNoOfIntersections().toString());
        System.out.println(map.getNoOfSegments().toString());
        System.out.println(map.getSegmentList().toString());
        System.out.println(planningRequest.toString());
        System.out.println(ltcList.toString());
        c.setTour(ComputeTour.recreateTourneeWithOrder(map, planningRequest, ltcList));
    }

    @Override
    public void undoCommand(MVCController c) {}
}
