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
 * <h1>Apply Modification Command Class</h1>
 * <p>The Apply Modification Command defines the command to be called when user confirms all the modifications and is willing to save current status</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class ApplyModificationCommand implements Command {

    /**
     * Map object used for new tournee computation
     */
    private Map map;
    /**
     * Planning request used for new tournee computation
     */
    private PlanningRequest planningRequest;
    /**
     * LtcList that contains the order of the delivery/pickup points, used for new tournee computation
     */
    private ArrayList<Controller.LocationTagContent> ltcList;

    /**
     * Constructor
     * @param m the Map object
     * @param p the PlanningRequest object
     * @param ltcList the list of ltc object
     */
    public ApplyModificationCommand(Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList) {
        this.map = m;
        this.planningRequest = p;
        this.ltcList = ltcList;
    }

    /**
     * Execute command : apply modification and set MVCController's new Tournee to the computed Tournee
     * @param c the MVCController pointer used to update tournee
     */
    @Override
    public void doCommand(MVCController c) {
        c.setTour(ComputeTour.recreateTourneeWithOrder(map, planningRequest, ltcList));
    }

    @Override
    public void undoCommand(MVCController c) {}
}
