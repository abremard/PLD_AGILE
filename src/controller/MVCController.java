package controller;

import command.ListOfCommands;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import javafx.stage.Window;
import objects.Map;
import objects.PlanningRequest;
import state.*;

public class MVCController {

    private ListOfCommands l;
    private Window window;
    private State currentState;
    private InitialState initialState;
    private MapState mapState;
    private RequestState requestState;
    private TourState tourState;

    public MVCController(Window window) {
        this.l = new ListOfCommands();
        this.window = window;
        this.initialState = new InitialState();
        this.currentState = this.initialState;
        this.mapState = new MapState();
        this.requestState = new RequestState();
        this.tourState = new TourState();
    }

    public ListOfCommands getL() {
        return l;
    }

    public void setL(ListOfCommands l) {
        this.l = l;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
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

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    public void LoadMap(String path) {
        currentState.loadMap(l,this, window, path);
    }

    public void LoadRequestPlan(String path) {
        currentState.loadRequestPlan(l, this, window, path);
    }

    public void ComputeTour(Map m, PlanningRequest p) {
        currentState.calculateTour(l, this, window, p, m);
    }

}
