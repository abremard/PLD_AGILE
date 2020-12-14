package command;

import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

/**
 * <h1>Remove Request Command Class</h1>
 * <p>The Remove Request Command defines the command to be called when user confirms the removal of a request</p>
 *
 * @author H4302
 * @see Command
 * @see MVCController
 */
public class RemoveRequestCommand implements Command {

    /**
     * The request to be removed
     */
    private Request requestToRemove;

    /**
     * The index of the first LocationTagContent to be removed in the MvcController's LtcList
     */
    private int removedCardIndex1;

    /**
     * The index of the second LocationTagContent to be removed in the MvcController's LtcList
     */
    private int removedCardIndex2;

    /**
     * The first LocationTagContent to be removed in the MvcController's LtcList
     */
    private Controller.LocationTagContent removedCard1;

    /**
     * The second LocationTagContent to be removed in the MvcController's LtcList
     */
    private Controller.LocationTagContent removedCard2;

    /**
     * The request list before the removal
     */
    private ArrayList<Request> oldRequestList;

    /**
     * The requests' planning before the removal
     */
    private PlanningRequest oldPlanningRequest;

    /**
     * The requests' planning after the removal
     */
    private PlanningRequest newPlanningRequest;

    public Request getRemovedRequestIndex() {
        return requestToRemove;
    }

    public void setRemovedRequestIndex(Request request) {
        this.requestToRemove = request;
    }

    /**
     * constructor
     * @param oldPlanningRequest The requests' planning before the removal
     * @param ltcList The LocationTagContents' list before the removal
     * @param requestToRemove The request to be removed
     * @param removedCardIndex1 The index of the first LocationTagContent to be removed in the MvcController's LtcList
     * @param removedCardIndex2 The index of the second LocationTagContent to be removed in the MvcController's LtcList
     */
    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, Request requestToRemove, int removedCardIndex1, int removedCardIndex2) {
        this.requestToRemove = requestToRemove;
        this.oldRequestList = new ArrayList<>(oldPlanningRequest.getRequestList());
        this.oldPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.removedCardIndex1 = removedCardIndex1;
        this.removedCardIndex2 = removedCardIndex2;
        this.removedCard1 = ltcList.get(removedCardIndex1);
        this.removedCard2 = ltcList.get(removedCardIndex2);
    }

    /**
     * Execute Command : remove the request we want to be removed from the MvcController's planning request
     *      and the corresponding cards in its ltcList
     * @param c the MVCController pointer used to update its map
     */
    @Override
    public void doCommand(MVCController c) {
        if (removedCardIndex1<removedCardIndex2) {
            c.getLtcList().remove(removedCardIndex2);
            c.getLtcList().remove(removedCardIndex1);
        }
        else {
            c.getLtcList().remove(removedCardIndex1);
            c.getLtcList().remove(removedCardIndex2);
        }
        this.newPlanningRequest.removeRequest(requestToRemove);
        c.setPlanningRequest(this.newPlanningRequest);
        c.getPlanningRequest().resetIndexOfRequestList();
    }

    /**
     * Undo command : revert this command's previous execution, it adds the previously removed request
     *      to the MvcController's List and the corresponding cards in its ltcList
     * @param c the MVCController pointer used to update its planning request and ltcList
     */
    @Override
    public void undoCommand(MVCController c) {
        if (removedCardIndex1<removedCardIndex2) {
            c.getLtcList().add(removedCardIndex1, removedCard1);
            c.getLtcList().add(removedCardIndex2, removedCard2);
        }
        else {
            c.getLtcList().add(removedCardIndex2, removedCard2);
            c.getLtcList().add(removedCardIndex1, removedCard1);
        }
        c.setPlanningRequest(this.oldPlanningRequest);
    }
}
