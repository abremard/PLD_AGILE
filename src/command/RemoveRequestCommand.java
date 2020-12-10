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

    private int removedRequestIndex;
    private int removedCardIndex1;
    private int removedCardIndex2;
    private Controller.LocationTagContent removedCard1;
    private Controller.LocationTagContent removedCard2;
    private ArrayList<Request> oldRequestList;

    public int getRemovedRequestIndex() {
        return removedRequestIndex;
    }

    public void setRemovedRequestIndex(int removedRequestIndex) {
        this.removedRequestIndex = removedRequestIndex;
    }

    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2) {
        this.removedRequestIndex = removedRequestIndex;
        this.oldRequestList = new ArrayList<>(oldPlanningRequest.getRequestList());
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
        c.getPlanningRequest().removeRequest(removedRequestIndex);
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
        c.getPlanningRequest().setRequestList(oldRequestList);
    }

    public Controller.LocationTagContent getRemovedCard1() {
        return removedCard1;
    }

    public void setRemovedCard1(Controller.LocationTagContent removedCard1) {
        this.removedCard1 = removedCard1;
    }

    public Controller.LocationTagContent getRemovedCard2() {
        return removedCard2;
    }

    public void setRemovedCard2(Controller.LocationTagContent removedCard2) {
        this.removedCard2 = removedCard2;
    }

}
