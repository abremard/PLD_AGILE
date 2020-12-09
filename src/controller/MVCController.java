package controller;

import command.ListOfCommands;
import objects.*;
import sample.Controller;
import state.*;

import java.util.ArrayList;

public class MVCController {

    private ListOfCommands l;
    private Map map;
    private PlanningRequest planningRequest;
    private Tournee tour;
    private ArrayList<Controller.LocationTagContent> ltcList;

    private State currentState;
    private InitialState initialState;
    private MapState mapState;
    private RequestState requestState;
    private TourState tourState;
    private ModifyState modifyState;
    private AddState addState;
    private ModifyRequestState modifyRequestState;
    private RemoveState removeState;

    public MVCController() {
        this.l = new ListOfCommands();
        this.initialState = new InitialState();
        this.currentState = this.initialState;
        this.mapState = new MapState();
        this.requestState = new RequestState();
        this.tourState = new TourState();
        this.modifyState = new ModifyState();
        this.addState = new AddState();
        this.modifyRequestState = new ModifyRequestState();
        this.removeState = new RemoveState();
        this.map = new Map();
        this.planningRequest = new PlanningRequest();
        this.tour = new Tournee();
        this.ltcList = new ArrayList<>();
    }

    public ListOfCommands getL() {
        return l;
    }

    public void setL(ListOfCommands l) {
        this.l = l;
    }

    public State getCurrentState() {
        return currentState;
    }

    public InitialState getInitialState() {
        return initialState;
    }

    public void setInitialState(InitialState initialState) {
        this.initialState = initialState;
    }

    public MapState getMapState() {
        return mapState;
    }

    public void setMapState(MapState mapState) {
        this.mapState = mapState;
    }

    public RequestState getRequestState() {
        return requestState;
    }

    public void setRequestState(RequestState requestState) {
        this.requestState = requestState;
    }

    public TourState getTourState() {
        return tourState;
    }

    public void setTourState(TourState tourState) {
        this.tourState = tourState;
    }

    public ModifyState getModifyState() { return modifyState; }

    public void setModifyState(ModifyState modifyState) {this.modifyState = modifyState;}

    public AddState getAddState() {return addState;}

    public void setAddState(AddState addState) {this.addState = addState;    }

    public ModifyRequestState getModifyRequestState() {return modifyRequestState;}

    public void setModifyRequestState(ModifyRequestState modifyRequestState) {this.modifyRequestState = modifyRequestState;}

    public RemoveState getRemoveState() {return removeState;}

    public void setRemoveState(RemoveState removeState) {this.removeState = removeState;}

    public void setCurrentState(State state) {this.currentState = state;}

    public void LoadMap(String path) {currentState.loadMap(l,this, path);}

    public void LoadRequestPlan(String path) {currentState.loadRequestPlan(l, this, path);}

    public void ComputeTour(Map m, PlanningRequest p) { currentState.calculateTour(l, this, p, m); }

    public void ModifyRequestList(){currentState.modifyRequestList(this);}

    public void addRequest(){currentState.addRequest(this);}

    public void removeRequest(){currentState.removeRequest(this);}

    public void modifyRequest(){currentState.modifyRequest(this);}

    public void swapRequest(int a, int b, ArrayList<Controller.LocationTagContent> ltcList){currentState.swapRequest(l, this, a, b,ltcList);}

    public void addDone(PlanningRequest p, Map m, ArrayList<Controller.LocationTagContent> ltcList, Request newRequest, Controller.LocationTagContent newPickupLtc, Controller.LocationTagContent newDeliveryLtc){currentState.addDone(l, this, p, m, ltcList, newRequest, newPickupLtc, newDeliveryLtc);}

    public void removeDone(PlanningRequest oldPlanningRequest, ArrayList<Controller.LocationTagContent> ltcList, int removedRequestIndex, int removedCardIndex1, int removedCardIndex2){currentState.removeDone(l, this, oldPlanningRequest, ltcList, removedRequestIndex, removedCardIndex1, removedCardIndex2);}

    public void modifyRequestDone(Request oldRequest, Request newRequest, Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList){currentState.modifyRequestDone(l, this, oldRequest, newRequest, m, p, ltcList);}

    public void applyModificationDone(Map m, PlanningRequest p, ArrayList<Controller.LocationTagContent> ltcList){currentState.applyModificationDone(l, this, m, p, ltcList);}

    public void cancel(){currentState.cancel(this);}

    public void Reset() { currentState.newTour(l, this); }

    public void Undo() { currentState.undo(l, this); }

    public void Redo() { currentState.redo(l, this); }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public PlanningRequest getPlanningRequest() {
        return planningRequest;
    }

    public void setPlanningRequest(PlanningRequest planningRequest) {
        this.planningRequest = planningRequest;
    }

    public Tournee getTour() {
        return tour;
    }

    public void setTour(Tournee tour) {
        this.tour = tour;
    }

    public ArrayList<Controller.LocationTagContent> getLtcList() {
        return ltcList;
    }

    public void setLtcList(ArrayList<Controller.LocationTagContent> ltcList) {
        this.ltcList = ltcList;
    }
}
