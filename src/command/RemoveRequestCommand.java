package command;

import controller.MVCController;
import objects.PlanningRequest;
import objects.Request;
import sample.Controller;

import java.util.ArrayList;

public class RemoveRequestCommand implements Command {

    private int removedRequestIndex;
    private Request removedRequest;
    private int removedCardIndex1;
    private int removedCardIndex2;
    private Controller.LocationTagContent removedCard1;
    private Controller.LocationTagContent removedCard2;
    private PlanningRequest newPlanningRequest;
    private ArrayList<Controller.LocationTagContent> newLtcList;

    public int getRemovedRequestIndex() {
        return removedRequestIndex;
    }

    public void setRemovedRequestIndex(int removedRequestIndex) {
        this.removedRequestIndex = removedRequestIndex;
    }

    public Request getRemovedRequest() {
        return removedRequest;
    }

    public void setRemovedRequest(Request removedRequest) {
        this.removedRequest = removedRequest;
    }

    public PlanningRequest getNewPlanningRequest() {
        return newPlanningRequest;
    }

    public void setNewPlanningRequest(PlanningRequest newPlanningRequest) {
        this.newPlanningRequest = newPlanningRequest;
    }

    public RemoveRequestCommand(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2) {
        this.removedRequestIndex = removedRequestIndex;
        this.removedRequest = oldPlanningRequest.getRequestList().get(removedRequestIndex);
        this.newPlanningRequest = new PlanningRequest(oldPlanningRequest);
        this.newLtcList = ltcList;
        this.removedCardIndex1 = removedCardIndex1;
        this.removedCardIndex2 = removedCardIndex2;
        this.removedCard1 = ltcList.get(removedCardIndex1);
        this.removedCard2 = ltcList.get(removedCardIndex2);
    }

    @Override
    public void doCommand(MVCController c) {
        this.newPlanningRequest.removeRequest(removedRequestIndex);
        if (removedCardIndex1<removedCardIndex2) {
            this.newLtcList.remove(removedCardIndex2);
            this.newLtcList.remove(removedCardIndex1);
        }
        else {
            this.newLtcList.remove(removedCardIndex1);
            this.newLtcList.remove(removedCardIndex2);
        }
        c.setLtcList(newLtcList);
        c.setPlanningRequest(newPlanningRequest);
    }

    @Override
    public void undoCommand(MVCController c) {
        this.newPlanningRequest.addRequest(removedRequestIndex, removedRequest);
        if (removedCardIndex1<removedCardIndex2) {
            this.newLtcList.add(removedCardIndex1, removedCard1);
            this.newLtcList.add(removedCardIndex2, removedCard2);
        }
        else {
            this.newLtcList.add(removedCardIndex2, removedCard2);
            this.newLtcList.add(removedCardIndex1, removedCard1);
        }
        c.setLtcList(newLtcList);
        c.setPlanningRequest(newPlanningRequest);
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

    public ArrayList<Controller.LocationTagContent> getNewLtcList() {
        return newLtcList;
    }

    public void setNewLtcList(ArrayList<Controller.LocationTagContent> newLtcList) {
        this.newLtcList = newLtcList;
    }
}
