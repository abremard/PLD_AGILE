package controller;

import command.ListOfCommands;
import objects.Map;
import objects.PlanningRequest;
import state.*;

public class MVCController {

    private ListOfCommands l;

    private State currentState;
    private InitialState initialState;
    private MapState mapState;
    private RequestState requestState;
    private TourState tourState;
    private ModifyState modifyState;

    public MVCController() {
        this.l = new ListOfCommands();
        this.initialState = new InitialState();
        this.currentState = this.initialState;
        this.mapState = new MapState();
        this.requestState = new RequestState();
        this.tourState = new TourState();
        this.modifyState = new ModifyState();
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

    public ModifyState getModifyState() {return modifyState;}

    public void setModifyState(ModifyState modifyState) {this.modifyState = modifyState;}

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    public void LoadMap(String path) {
        currentState.loadMap(l,this, path);
    }

    public void LoadRequestPlan(String path) {
        currentState.loadRequestPlan(l, this, path);
    }

    public void ComputeTour(Map m, PlanningRequest p) { currentState.calculateTour(l, this, p, m); }

    public void Reset() { currentState.newTour(l, this); }

    public void Undo() { currentState.undo(l); }

    public void Redo() { currentState.redo(l); }
}
