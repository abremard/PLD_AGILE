package command;

import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

/**
 * Commande qui nous permet de supprimer une requête de la tournée.
 */
public class RemoveRequestCommand implements Command {

    private Request requestToRemove;
    private int removedCardIndex1;
    private int removedCardIndex2;
    private Controller.LocationTagContent removedCard1;
    private Controller.LocationTagContent removedCard2;
    private ArrayList<Request> oldRequestList;
    private PlanningRequest oldPlanningRequest;
    private PlanningRequest newPlanningRequest;

    public Request getRemovedRequestIndex() {
        return requestToRemove;
    }

    public void setRemovedRequestIndex(Request request) {
        this.requestToRemove = request;
    }

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
