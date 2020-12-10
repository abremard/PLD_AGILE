package sample;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import command.*;
import controller.MVCController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import processing.TupleRequete;

/**
 * <h1>Controller Class</h1>
 * The JavaFX controller helps display all the UI elements, and defines the interaction between the user and these
 * elements.
 * <p>
 */

public class Controller {

    /**
     * Marker for Pickup Location
     */
    String pickupImageFile = "/images/pickupMarker.png";
    /**
     * Marker for Delivery location
     */
    String deliveryImageFile = "/images/deliveryMarker.png";
    /**
     * Marker for Starting location
     */
    String depotImageFile = "/images/depotMarker.png";

    /**
     * Logger for testing and debugging pursposes
     */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    /**
     * The Map View displayed on screen
     */
    @FXML
    private MapView mapView;

    /**
     * The List view of the side panel
     */
    @FXML
    private ListView listView;

    /**
     * The side panel container
     */
    @FXML
    private AnchorPane list;

    /**
     * First button of side panel
     */
    @FXML
    private Button mainButton;

    /**
     * Second button of side panel
     */
    @FXML
    private Button secondButton;

    /**
     * Button to load map file
     */
    @FXML
    private Button mapButton;

    /**
     * Undo button
     */
    @FXML
    private Button undoButton;

    /**
     * Redo button
     */
    @FXML
    private Button redoButton;

    /**
     * Button to add request file
     */
    @FXML
    private Button requestButton;

    /**
     * First text field in side panel
     */
    @FXML
    private TextField mapField;

    /**
     * Second text field in side panel
     */
    @FXML
    private TextField requestField;

    /**
     * First Static Text
     */
    @FXML
    private Text mapText;

    /**
     * Second Static Text
     */
    @FXML
    private Text requestText;

    /**
     * Third Static Text
     */
    @FXML
    private Text infoText;

    /**
     * status indicator: are we adding a request
     */
    private boolean addingRequest;
    /**
     * status indicator: are we editing a request
     */
    private boolean editingRequest = false;
    /**
     * status indicator: how many locations are added in request
     */
    private int addedReqCount;
    /**
     * temporary request
     */
    private Request tempRequest;
    private LocationTagContent tempItem;
    private LocationTagContent NewPickupLtc;
    private LocationTagContent NewDeliveryLtc;
    /**
     * List of coordinate lines of the map - MapView
     */
    private ArrayList<CoordinateLine> coordLines;
    /**
     * List of coordinate lines of the tour - MapView
     */
    private ArrayList<CoordinateLine> tourLines;
    /**
     * List of coordinate lines of the selected path of tour - MapView
     */
    private ArrayList<CoordinateLine> selectedLines;
    /**
     * List of markers of the requests - MapView
     */
    private ArrayList<Marker> markers;
    /**
     * List of cards of the side panel
     */
    private ArrayList<LocationTagContent> cards;

    /**
     * Map to be displayed
     */
    private Map map = null;
    /**
     * Planning of the requests to be added in the tour
     */
    private PlanningRequest planningRequest;
    /**
     * Calculated tour
     */
    private Tournee tour;

    /**
     * Color of map lines
     */
    private Color mapColor = new Color(0.40,0.40,0.40, 1.0);
    /**
     * Color of tour lines
     */
    private Color pathColor = new Color(0.2,0.6,1.0, 1.0);
    /**
     * Color of highlighted lines of tour
     */
    private Color selectionColor = new Color(1.0,0.4,0.0, 1.0);

    /**
     * status indicator: timeline view
     */
    private boolean isTimeline = false;
    /**
     * status indicator: modify view
     */
    private boolean isModify = false;
    /**
     * status indicator: add request view
     */
    private boolean isAddRequest = false;
    /**
     * status indicator: edit request view
     */
    private boolean isEdit = false;

    /**
     * coordinates of Lyon for map initialisation
     */
    private static final Coordinate coordLyon = new Coordinate(45.77087932755228, 4.863621380475198);

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;

    /** params for the WMS server. */
    private WMSParam wmsParam = new WMSParam()
            .setUrl("http://ows.terrestris.de/osm/service?")
            .addParam("layers", "OSM-WMS");

    private XYZParam xyzParams = new XYZParam()
            .withUrl("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x})")
            .withAttributions(
                    "'Tiles &copy; <a href=\"https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer\">ArcGIS</a>'");

    /**
     * MVC controller of the app
     */
    private MVCController mvcController;

    /**
     * Constructor: initialises attributes of class to initial values
     */
    public Controller()
    {
        mvcController = new MVCController();
        coordLines = new ArrayList<>();
        tourLines = new ArrayList<CoordinateLine>();
        markers = new ArrayList<Marker>();
        cards = new ArrayList<LocationTagContent>();
        selectedLines = new ArrayList<CoordinateLine>();

        addingRequest = false;
        addedReqCount = 0;
    }



    public LocationTagContent convertRequestToLTC(Request request, boolean isPickup) {
        String name = "";
        String street1 = "";
        String street2 = "";
        double latitude = 0.0;
        double longitude = 0.0;
        if (isPickup == true)
        {
            int index = ((cards.size() - 1)/2)+1;
            name = "Pickup "+ Integer.toString(index);
            ArrayList<String> street = map.getSegmentNameFromIntersectionId(request.getPickup().getId());
            logger.info(street.toString());
            street1 = street.get(0);
            if (street1.length()>20) {
                street1 = street1.substring(0, 19) + "..";
            }
            if (street.size() >= 2) {
                street2 = street.get(1);
                if (street2.length()>20) {
                    street2 = street2.substring(0, 19) + "..";
                }
            }
            latitude = request.getPickup().getLatitude();
            longitude = request.getPickup().getLongitude();
        } else
        {
            int index = ((cards.size() - 1)/2)+1;
            name = "Delivery "+ Integer.toString(index);
            ArrayList<String> street = map.getSegmentNameFromIntersectionId(request.getDelivery().getId());
            logger.info(street.toString());
            street1 = street.get(0);
            if (street1.length()>20) {
                street1 = street1.substring(0, 19) + "..";
            }
            if (street.size() >= 2) {
                street2 = street.get(1);
                if (street2.length()>20) {
                    street2 = street2.substring(0, 19) + "..";
                }
            }
            latitude = request.getDelivery().getLatitude();
            longitude = request.getDelivery().getLongitude();
        }

        LocalTime time  = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        Coordinate location = new Coordinate(latitude, longitude);

        logger.info("jusque lÃ ?");
        LocationTagContent item = new LocationTagContent(name, street1, street2, null, location, null, request);
        logger.info("aprÃ¨s la crÃ©ation du LTC");
        item.setIsPickup(isPickup);
        logger.info("fin de la transformation");
        return item;
    }
    /**
     * Initialise the Map and all of the buttons
     * @param projection Projection of the window
     */
    public void initMapAndControls(Projection projection) {
        logger.info("begin initialize");

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";

        undoButton.setGraphic(new ImageView("sample/undoButton.png"));
        redoButton.setGraphic(new ImageView("sample/redoButton.png"));
        undoButton.setVisible(false);
        redoButton.setVisible(false);

        initEventHandlers();

        mapView.setAnimationDuration(0);

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        mapView.setMapType(MapType.OSM);

        logger.info("options and labels done");

        logger.info("start map initialization");
        // finally initialize the map view
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());

        //disable buttons for request as map is not loaded yet and main button as well
        requestButton.setDisable(true);
        requestField.setDisable(true);
        mainButton.setDisable(true);
        secondButton.setVisible(false);

        secondButton.setText("Modify");



        logger.info("initialization finished");

    }

    /**
     * Initialise the Button handlers
     *
     */
    public void initEventHandlers(){

        logger.info("Setting up event handlers");

        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume(); // not sure what this does tbh

            if( addingRequest ){
                handleNewRequestClick(event);
            }
            else if( editingRequest ){
                handleEditRequestClick(event);
            }
        });

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            logger.info(" clicked on a marker ");
        });

        final FileChooser fileChooser = new FileChooser();

        //Initialise the File chooser buttons with their handlers
        mapButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(new Stage());
                mapField.setText(file.getAbsolutePath());


                mvcController.LoadMap(file.getAbsolutePath());
                refreshModel();

                displayMap();

                if (coordLines.isEmpty())
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Attention!");
                        alert.setHeaderText(null);
                        alert.setContentText("There seems to be a problem with your Map file. Please make sure it is properly formatted");

                        alert.showAndWait();
                } else {
                    requestButton.setDisable(false);
                    requestField.setDisable(false);
                    secondButton.setDisable(false);
                }
            }

        });

        //Initialise the File chooser buttons with their handlers
        requestButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(new Stage());
                requestField.setText(file.getAbsolutePath());
                logger.info(file.getAbsolutePath());
                System.out.println(file.getAbsolutePath());

                try {
                    mvcController.LoadRequestPlan(file.getAbsolutePath());
                    refreshModel();

                    displayRequests(true);
                    mainButton.setDisable(false);
                } catch (NullPointerException nullPointerException) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Attention!");
                    alert.setHeaderText(null);
                    alert.setContentText("There seems to be a problem with your Requests file. Please make sure it is properly formatted.");

                    alert.showAndWait();
                }

            }
        });

        //Listener on Main button that switches between views
        mainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (isModify)
                {
                    undoButton.setVisible(false);
                    redoButton.setVisible(false);
                    //DONE WITH MODIFICATION
                    removeFromMap( selectedLines);
                    removeFromMap( tourLines);
                    //PASS NEW ORDER AND SETUP TO CALCULATE TOUR

                    //remove previous timeline
                    list.getChildren().remove(list.getChildren().size() -1);

                    //change text of buttons
                    mainButton.setText("New Tour");
                    secondButton.setText("Modify");
                    secondButton.setVisible(true);

                    ArrayList<Intersection> order = new ArrayList<>(); // TODO : MODIFY ORDER
                    // mvcController.applyModificationDone(map, planningRequest, order);
                    System.out.println(planningRequest.toString());
                    // logger.info("planningRequest ->" + planningRequest.toString());
                    logger.info("cards ->" + cards.toString());
                    mvcController.applyModificationDone(map, planningRequest, cards);
                    refreshModel();
                    displayRequests(false);
                    displayTour();

                    //call method that places results on timeline
                    initCardContent();

                    undoButton.setVisible(false);
                    redoButton.setVisible(false);
                    //change text of buttons

                    //update button position
                    isTimeline = true;
                    isAddRequest = false;
                    isModify = false;
                }
                //detect on which view we are - File Picker or Timeline
                else if (isAddRequest) {
                    //CANCEL ADD REQUEST OPERATION, BACK TO MODIFY VIEW
                    mvcController.cancel();
                    displayRequests(false);
                    modifySetup(false);
                }
                else if(isEdit){
                    mvcController.cancel();
                    modifySetup(false);
                    editingRequest = false;
                }
                else if(isTimeline)
                {
                    undoButton.setVisible(false);
                    redoButton.setVisible(false);
                    //delete list of timeline
                    logger.info(String.valueOf(list.getChildren().remove(list.getChildren().size() -1)));
                    //remove selected path from timeline
                    removeFromMap(selectedLines);

                    //show file picker elements
                    mapText.setText("Import Map File");
                    requestText.setText("Import Request File");
                    mapButton.setVisible(true);
                    requestButton.setVisible(true);
                    mapField.setVisible(true);
                    requestField.setVisible(true);
                    requestText.setVisible(true);
                    mapText.setVisible(true);

                    // reset MVC State
                    mvcController.Reset();

                    //change text of button
                    mainButton.setText("Calculate Tour");

                    isTimeline = false;
                } else
                {
                    undoButton.setVisible(false);
                    redoButton.setVisible(false);
                    //hide elements from file picker view
                    mapButton.setVisible(false);
                    requestButton.setVisible(false);
                    mapField.setVisible(false);
                    requestField.setVisible(false);
                    requestText.setVisible(false);
                    mapText.setVisible(false);

                    //change text of buttons
                    mainButton.setText("New Tour");
                    secondButton.setText("Modify");
                    secondButton.setVisible(true);

                    //get files, pass them to the algo, calculate path, get results
                    //logger.info(map.toString());
                    //logger.info(planningRequest.toString());
                    //call method that places results on the map
                    mvcController.ComputeTour(map, planningRequest);
                    refreshModel();
                    displayTour();
                    //call method that places results on timeline
                    initCardContent();

                    isTimeline = true;
                }

            }
        });

        secondButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isAddRequest) {
                    System.out.println("Clicked on add ");
                    // clicked on add in adding request phase
                    tempRequest.setId(planningRequest.getRequestList().size());
                    tempRequest.setPickupDur(60*Double.parseDouble(mapField.getText()));
                    tempRequest.setDeliveryDur(60*Double.parseDouble(requestField.getText()));

                    // planningRequest.addRequest(planningRequest.getRequestList().size(), tempRequest);
                    // System.out.println(planningRequest.toString());

                    // displayTour();
                    //call method that places results on timeline
                    // initCardContent();
                    // cmd.setNewLtcList(new ArrayList<>(cards));

                    mvcController.addDone(tempRequest, NewPickupLtc, NewDeliveryLtc);
                    AddRequestCommand cmd = (AddRequestCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                    refreshModel();
                    list.getChildren().remove(list.getChildren().size() -1);
                    //compute tour
                    //back to modify
                    modifySetup(false);
                }
                else if (isModify) {
                    addingRequest = true;
                    addedReqCount = 0;
                    addRequestSetup();
                    tempRequest = new Request(new Intersection(0,0), new Intersection(0,0), 0,0);
                    mvcController.addRequest();
                }
                else if ( isEdit){

                    int editedCardIndex = cards.indexOf(tempItem);
                    double newDuration = Double.parseDouble(mapField.getText());
                    boolean isPickup = false;

                    if( mapText.getText().equals("Pickup Duration (min)")){
                        isPickup = true;
                    }

                    int editedRequestIndex = planningRequest.getRequestList().indexOf(tempRequest);
                    mvcController.modifyRequestDone(planningRequest, editedRequestIndex, editedCardIndex, newDuration, isPickup);
                    refreshModel();
                    list.getChildren().remove(list.getChildren().size() -1);
                    modifySetup(false);

                } else {

                    modifySetup(true);
                    mvcController.ModifyRequestList();

                }
            }
        });

        logger.info("Finished setting up event handlers");

    }

    /**
     * Setup the view for adding a new request
     */
    private void addRequestSetup() {
        undoButton.setVisible(false);
        redoButton.setVisible(false);

        // infoText.setText("Click on the map to place the Pickup location");

        mainButton.setText("Cancel");
        secondButton.setText("Add");
        list.getChildren().remove(list.getChildren().size() -1);
        mapText.setText("Pickup Duration (min)");
        requestText.setText("Delivery Duration (min)");
        mapField.setText("0");
        requestField.setText("0");
        mapText.setVisible(true);
        requestText.setVisible(true);
        requestField.setVisible(true);
        mapField.setVisible(true);

        isAddRequest = true;
        isModify = false;
        isTimeline = false;
        isEdit = false;
    }

    /**
     * Setup the view for editing a request
     */
    private void editSetup( LocationTagContent item ) {

        mvcController.modifyRequest();

        undoButton.setVisible(false);
        redoButton.setVisible(false);

        String type = item.getName().split(" ")[0];

        tempRequest = item.request;
        tempItem = item;

        if( type.equals("Pickup") ){
            mapText.setText("Pickup Duration (min)");
            requestText.setText("Click on the map to change the Pickup location");
            mapField.setText(Double.toString(item.request.getPickupDur()));
        } else if( type.equals("Delivery") ){
            mapText.setText("Delivery Duration (min)");
            requestText.setText("Click on the map to change the Delivery location");
            mapField.setText(Double.toString(item.request.getDeliveryDur()));
        }

        mainButton.setText("Cancel");
        secondButton.setText("Done");
        list.getChildren().remove(list.getChildren().size() -1);
        mapText.setVisible(true);
        mapField.setVisible(true);
        requestText.setVisible(true);


        editingRequest = true;
        isEdit = true;
        isAddRequest = false;
        isModify = false;
        isTimeline = false;
    }

    /**
     * Setup the view to modify the timeline
     */
    private void modifySetup(boolean toDelete) {
        undoButton.setVisible(true);
        redoButton.setVisible(true);
        //make sure text box elements are not shown
        mapField.setVisible(false);
        requestField.setVisible(false);
        requestText.setVisible(false);
        mapText.setVisible(false);

        mainButton.setText("Done");
        secondButton.setText("New Request");
        if (toDelete)
        {
            list.getChildren().remove(list.getChildren().size() -1);
        }
        addCardsToScreen(true);
        isModify = true;
        isTimeline = false;
        isAddRequest = false;
        isEdit = false;
    }

    /**
     * Display the loaded Map file on the Map View
     */
    private void displayMap() {
        removeFromMap(coordLines);
        removeFromMap(selectedLines);
        removeFromMap(tourLines);
        coordLines.clear();
        selectedLines.clear();
        tourLines.clear();

        ArrayList<Segment> listSegments = map.getSegmentList();
        ArrayList<Intersection> listIntersection = map.getIntersectionList();
        // logger.info(listSegments.toString());
        // logger.info(listIntersection.toString());
        for (Segment segment: listSegments) {
            long idOrigin = segment.getOrigin();
            long idDestination = segment.getDestination();
            Intersection ptOrigin = map.matchIdToIntersection(idOrigin);
            Intersection ptDestination = map.matchIdToIntersection(idDestination);

            Coordinate coordOrigin = new Coordinate(ptOrigin.getLatitude(), ptOrigin.getLongitude());
            Coordinate coordDestination = new Coordinate(ptDestination.getLatitude(), ptDestination.getLongitude());
            // Extent extent = Extent.forCoordinates();
            CoordinateLine cl = new CoordinateLine(coordOrigin,coordDestination);
            cl.setVisible(true).setColor(mapColor).setWidth(1);
            coordLines.add(cl);
            mapView.addCoordinateLine(cl);
        }
    }

    /**
     * Display the calculated tour path on the Map View
     */
    public void displayTour() {
        ArrayList<Segment> listSegments = tour.getSegmentList();
        ArrayList<Intersection> listIntersection = map.getIntersectionList();
        //logger.info(listSegments.toString());
        //logger.info(listIntersection.toString());
        for (Segment segment : listSegments) {
            long idOrigin = segment.getOrigin();
            long idDestination = segment.getDestination();
            Intersection ptOrigin = map.matchIdToIntersection(idOrigin);
            Intersection ptDestination = map.matchIdToIntersection(idDestination);
            Coordinate coordOrigin = new Coordinate(ptOrigin.getLatitude(), ptOrigin.getLongitude());
            Coordinate coordDestination = new Coordinate(ptDestination.getLatitude(), ptDestination.getLongitude());
            // Extent extent = Extent.forCoordinates();
            CoordinateLine cl = new CoordinateLine(coordOrigin, coordDestination);
            cl.setVisible(true).setColor(pathColor).setWidth(4);
            tourLines.add(cl);
            mapView.addCoordinateLine(cl);
        }
    }

    /**
     * Display a highlighted path on the MapView
     */
    public void displaySegmentTour(ArrayList<Segment> path)
    {
        removeFromMap(selectedLines);
        selectedLines.clear();
        ArrayList<Intersection> listIntersection = map.getIntersectionList();
        //logger.info(listSegments.toString());
        //logger.info(listIntersection.toString());
        for (Segment segment : path) {
            long idOrigin = segment.getOrigin();
            long idDestination = segment.getDestination();
            Intersection ptOrigin = map.matchIdToIntersection(idOrigin);
            Intersection ptDestination = map.matchIdToIntersection(idDestination);
            Coordinate coordOrigin = new Coordinate(ptOrigin.getLatitude(), ptOrigin.getLongitude());
            Coordinate coordDestination = new Coordinate(ptDestination.getLatitude(), ptDestination.getLongitude());
            // Extent extent = Extent.forCoordinates();
            CoordinateLine cl = new CoordinateLine(coordOrigin, coordDestination);
            cl.setVisible(true).setColor(selectionColor).setWidth(6);
            selectedLines.add(cl);
            mapView.addCoordinateLine(cl);
        }
    }

    /**
     * remove Cooddinate Lines from the Map View
     */
    public void removeFromMap(ArrayList<CoordinateLine> list)
    {
        for (CoordinateLine cl: list) {
            mapView.removeCoordinateLine(cl);
        }
    }

    /**
     * Remove location markers from Map View
     */
    public void removeFromMapMarker(ArrayList<Marker> list)
    {
        for (Marker m: list) {
            mapView.removeMarker(m);
        }
    }

    /**
     * Display the location markers from the requests file
     */
    public void displayRequests(boolean toRemoveTour){

        removeFromMap(selectedLines);
        if (toRemoveTour)
        {
            removeFromMap(tourLines);
            tourLines.clear();
        }
        removeFromMapMarker(markers);
        selectedLines.clear();

        markers.clear();

        ArrayList<Intersection> listIntersection = map.getIntersectionList();

        Intersection delivery = null;
        Intersection pickup = null;
        Intersection depot = null;

        long idDepot = planningRequest.getDepot().getAdresse().getId();
        depot = map.matchIdToIntersection(idDepot);
        planningRequest.getDepot().setAdresse(depot);
        mvcController.setPlanningRequest(planningRequest);


        for( Request request : planningRequest.getRequestList()  ){
            long idPickup = request.getPickup().getId();
            long idDelivery = request.getDelivery().getId();

            boolean foundDel = false;
            boolean foundPick = false;
            boolean foundDepot = false;

            pickup = map.matchIdToIntersection(idPickup);
            request.setPickup(pickup);
            delivery = map.matchIdToIntersection(idDelivery);
            request.setDelivery(delivery);
            Coordinate coordPickup = new Coordinate(pickup.getLatitude(), pickup.getLongitude());
            Marker markerPickup = new Marker( getClass().getResource(pickupImageFile),-12,-12).setPosition(coordPickup).setVisible(true);
            request.getPickup().setMarkerId(markerPickup.getId());
            Coordinate coordDelivery = new Coordinate(delivery.getLatitude(),delivery.getLongitude());
            Marker markerDelivery = new Marker( getClass().getResource(deliveryImageFile),-11,-25).setPosition(coordDelivery).setVisible(true);
            request.getDelivery().setMarkerId(markerDelivery.getId());
            markers.add(markerPickup);
            markers.add(markerDelivery);
            mapView.addMarker(markerPickup);
            mapView.addMarker(markerDelivery);
        }

        Coordinate coordDepot = new Coordinate(depot.getLatitude(), depot.getLongitude());
        Marker markerDepot = new Marker( getClass().getResource(depotImageFile),-12,-12).setPosition(coordDepot).setVisible(true);
        markers.add(markerDepot);
        mapView.addMarker(markerDepot);

    }

    /**
     * Event handler for a click on the Map View for adding a request
     */
    public void handleNewRequestClick(MapViewEvent event){

        if( addedReqCount > 1){
            return;
        }

        System.out.println("added request count "+addedReqCount);
        Intersection newIntersection = new Intersection( event.getCoordinate().getLatitude(), event.getCoordinate().getLongitude());
        newIntersection = map.findClosestIntersection(newIntersection);
        String file = ( addedReqCount%2 == 0)? pickupImageFile : deliveryImageFile;
        Coordinate coordIntersection = new Coordinate(newIntersection.getLatitude(), newIntersection.getLongitude());
        Marker newMarker = new Marker( getClass().getResource(file),-12,-12).setPosition(coordIntersection).setVisible(true);
        markers.add(newMarker);
        mapView.addMarker(newMarker);

        if( addedReqCount == 0){
            tempRequest.setPickup(newIntersection);
            tempRequest.getPickup().setMarkerId(newMarker.getId());
            NewPickupLtc = convertRequestToLTC(tempRequest, true);
            // infoText.setText("Click on the map to place the Delivery location");
            //tempRequest.setPickupDur(5);
        } else {
            tempRequest.setDelivery(newIntersection);
            tempRequest.getDelivery().setMarkerId(newMarker.getId());
            NewDeliveryLtc = convertRequestToLTC(tempRequest, false);
            infoText.setText(" ");
            //tempRequest.setDelivery_dur(5);
        }

        addedReqCount++;

        if( addedReqCount == 2 ){
            //secondButton.setDisable(false);
        }
    }

    /**
     * Event handler for click on the map view for editing a request
     */
    public void handleEditRequestClick(MapViewEvent event){

        Intersection newIntersection = new Intersection( event.getCoordinate().getLatitude(), event.getCoordinate().getLongitude());
        newIntersection = map.findClosestIntersection(newIntersection);
        Coordinate coordIntersection = new Coordinate(newIntersection.getLatitude(), newIntersection.getLongitude());

        logger.info(" adding new intersection @" + newIntersection.toString());

        Marker newMarker = null;

        String file = "";
        if(mapText.getText().equals("Pickup Duration (min)")){
            for( Marker m : markers){
                if( m.getId().equals(tempRequest.getPickup().getMarkerId()) ){
                    mapView.removeMarker(m);
                    markers.remove(m);
                    break;
                }
            }
            file = pickupImageFile;

            tempRequest.setPickup(newIntersection);
            newMarker = new Marker( getClass().getResource(file),-12,-12).setPosition(coordIntersection).setVisible(true);
            tempRequest.getPickup().setMarkerId(newMarker.getId());
        } else if (mapText.getText().equals("Delivery Duration (min)")){
            for( Marker m : markers){
                if( m.getId().equals(tempRequest.getDelivery().getMarkerId()) ){
                    mapView.removeMarker(m);
                    markers.remove(m);
                    break;
                }
            }
            file = deliveryImageFile;
            tempRequest.setDelivery(newIntersection);
            newMarker = new Marker( getClass().getResource(file),-12,-12).setPosition(coordIntersection).setVisible(true);
            tempRequest.getDelivery().setMarkerId(newMarker.getId());
        }
        markers.add(newMarker);
        mapView.addMarker(newMarker);
        //editingRequest = false;
    }

    /**
     * Creates LocationTagContent objects from the results of a calculated tour
     */
    public void initCardContent() {
        cards.clear();
        int nbDelivery = 1;
        ArrayList<TupleRequete> points = tour.getPtsPassage();
        if(points == null) logger.info("Retrouve un objet nulllllllllll");
        for (TupleRequete pt: points) {
            String name = "";
            String street1 = "";
            String street2 = "";
            double latitude = 0.0;
            double longitude = 0.0;
            boolean isPickup = false;
            if (pt.isDepart())
            {
                name = "Pickup "+pt.getRequete().getId();
                ArrayList<String> street = map.getSegmentNameFromIntersectionId(pt.getRequete().getPickup().getId());
                logger.info(street.toString());
                street1 = street.get(0);
                if (street1.length()>20) {
                    street1 = street1.substring(0, 19) + "..";
                }
                if (street.size() >= 2) {
                    street2 = street.get(1);
                    if (street2.length()>20) {
                        street2 = street2.substring(0, 19) + "..";
                    }
                }
                latitude = pt.getRequete().getPickup().getLatitude();
                longitude = pt.getRequete().getPickup().getLongitude();
                isPickup = true;
            } else
            {
                name = "Delivery "+pt.getRequete().getId();
                if(nbDelivery == (int)((points.size()+1)/2)) { name = "Back to shop"; }
                nbDelivery++;
                ArrayList<String> street = map.getSegmentNameFromIntersectionId(pt.getRequete().getDelivery().getId());
                logger.info(street.toString());
                street1 = street.get(0);
                if (street1.length()>20) {
                    street1 = street1.substring(0, 19) + "..";
                }
                if (street.size() >= 2) {
                    street2 = street.get(1);
                    if (street2.length()>20) {
                        street2 = street2.substring(0, 19) + "..";
                    }
                }
                latitude = pt.getRequete().getDelivery().getLatitude();
                longitude = pt.getRequete().getDelivery().getLongitude();
                isPickup = false;
            }

            LocalTime time  = pt.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            Coordinate location = new Coordinate(latitude, longitude);

            LocationTagContent item = new LocationTagContent(name, street1, street2, time.format(formatter), location, pt.getChemin(), pt.getRequete());
            item.setIsPickup(isPickup);
            cards.add(item);
        }

        addCardsToScreen(false);
        mvcController.setLtcList(cards);

    }

    /**
     * Displays the timeline cards on the screen
     * @param isModify the format of the cards depending on the view (timeline or modify)
     */
    public void addCardsToScreen(boolean isModify) {
        logger.info(list.getChildren().toString());

        logger.info("creating data");
        ObservableList<LocationTagContent> data = FXCollections.observableArrayList();
        data.addAll(cards);
        if (isModify) {
            data.remove(cards.size() -1);
        }
        logger.info("data added");
        final ListView<LocationTagContent> l= new ListView<LocationTagContent>(data);

        l.setMinWidth(350.0);
        l.setMaxWidth(350.0);
        l.setPrefHeight(586.0);
        l.setPrefWidth(350.0);
        l.setStyle("-fx-background-color: transparent");
        l.getStylesheets().add(getClass().getResource("styleList.css").toExternalForm());

        l.setCellFactory(new Callback<ListView<LocationTagContent>, ListCell<LocationTagContent>>() {
            @Override
            public ListCell<LocationTagContent> call(ListView<LocationTagContent> listView) {

                if (isModify)
                {
                    return new CustomModifyListCell();
                } else {
                    return new CustomListCell();
                }

            }
        });
        logger.info("cell factory added");

        list.setTopAnchor(l, 0.0);
        list.setBottomAnchor(l, 0.0);

        list.getChildren().add(l);

        l.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                LocationTagContent lt = l.getSelectionModel().getSelectedItem();
                //mapView.setCenter(lt.coordLocation);
                displaySegmentTour(lt.chemin);
            }
        });
    }

    /**
     * finishes setup after the map is initialised
     */
    private void afterMapIsInitialized() {
        logger.info("map intialized");
        logger.info("setting center and enabling controls...");
        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordLyon);

    }

    /**
     * <h1>Location Tag Content</h1>
     * This class allows to format and prepare the Locations of a Request (Pickup or Delivery)
     * into textual raw data to be displayed on the Timeline listview
     *
     */
    public static class LocationTagContent {
        /**
         * Name of the Location (ex. "Pickup 1" or "Delivery 3"
         */
        private String name;
        /**
         * First street of the intersection of the Pickup/Delivery request.
         */
        private String streetOne;
        /**
         * Second street of the intersection of the Pickup/Delivery request.
         */
        private String streetTwo;
        /**
         * Estimated Time of arrival at location
         */
        private String arrivalTime;
        /**
         * Coordinates of the Pickup/Delivery location
         */
        private Coordinate coordLocation;
        /**
         * The request that refers to the Pickup/Delivery Location
         */
        private Request request;
        /**
         *  The path in the tour to get to this location from the previous location in the tour
         */
        private ArrayList<Segment> chemin;
        private boolean isPickup;

        public Request getRequest() { return request; }
        /**
         * Get the name of the LTC
         * @return name Name of the LTC
         */
        public String getName() {
            return name;
        }
        /**
         * Get the intersection of the location
         * @return intersectionName the names of the two streets of the intersection
         */
        public String getIntersection() {
            return streetOne + "\n" + streetTwo;
        }
        /**
         * Get the Arrival time to the location
         * @return arrivalTime
         */
        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setIsPickup(boolean state) {
            this.isPickup = state;
        }

        public Intersection getDestination() {
            if(isPickup) return request.getPickup();
            else return request.getDelivery();
        }

        /**
         * Constructor
         * @param name Name of location (Pickup/Delivery)
         * @param streetOne Street 1 of Intersection
         * @param streetTwo Street 2 of Intersection
         * @param arrivalTime Arrival Time of Intersection
         * @param coordLocation Coordinates of location
         * @param chemin Path to the location
         * @param req The request of the location
         */
        public LocationTagContent(String name, String streetOne, String streetTwo, String arrivalTime, Coordinate coordLocation, ArrayList<Segment> chemin, Request req) {
            super();
            this.name = name;
            this.streetOne = streetOne;
            this.streetTwo = streetTwo;
            this.arrivalTime = arrivalTime;
            this.coordLocation = coordLocation;
            this.chemin = chemin;
            this.request = req;
            this.isPickup = false;
        }

        /**
         * Prints the name of the LTC
         * @return name Name of LTC
         */
        public String toString() {
            return name;
        }
    }

    /**
     * <h1>Custom List Cell</h1>
     * A Location Tag Content needs to be properly formatted as a JavaFX object
     * in order to be displayed as cards on the timeline in the side panel of the app.
     * Custom List cell creates the layout of the cards in the Timeline View
     */
    private class CustomListCell extends ListCell<LocationTagContent> {
        /**
         *  Container of LTC content
         */
        private HBox content;
        /**
         *  name of location
         */
        private Text name;
        /**
         *  formatted address of location (Intersection)
         */
        private Text address;
        /**
         *  formatted text for estimated arrival time
         */
        private Text arrivalText;
        /**
         *  arrival time of the location
         */
        private Text arrivalTime;

        /**
         *  Constructor of the Cell
         */
        public CustomListCell() {
            super();
            name = new Text();
            name.setStyle("-fx-fill: #595959");
            name.setFont(Font.font("SF Pro Display", 20.0));
            address = new Text();
            address.setStyle("-fx-fill: #595959");
            address.setFont(Font.font("SF Pro Display", 12.0));
            arrivalText = new Text("arrive by");
            arrivalText.setStyle("-fx-fill: #595959");
            arrivalText.setFont(Font.font("SF Pro Display", 12.0));
            arrivalText.setTextAlignment(TextAlignment.RIGHT);
            arrivalTime = new Text();
            arrivalTime.setStyle("-fx-fill: #595959");
            arrivalTime.setFont(Font.font("SF Pro Display", 25.0));
            arrivalTime.setTextAlignment(TextAlignment.RIGHT);
            VBox vBox = new VBox(name, address);
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            VBox vBox2 = new VBox(arrivalText, arrivalTime);
            vBox2.setAlignment(Pos.CENTER_RIGHT);
            content = new HBox(vBox, region, vBox2);
            content.setSpacing(10);
        }

        /**
         *  Sets the content of a LocationTagContent in the card
         * @param item The Location Tag Content to be formatte in the card
         * @param empty is parameter empty
         */
        @Override
        protected void updateItem(LocationTagContent item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                name.setText(item.getName());
                address.setText(item.getIntersection());
                arrivalTime.setText(item.getArrivalTime());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * <h1>Custom Modify List Cell</h1>
     * A Location Tag Content needs to be properly formatted as a JavaFX object
     * in order to be displayed as cards on the timeline in the side panel of the app.
     * Custom List Modify Cell creates the layout of the cards in the Modify View
     */
    private class CustomModifyListCell extends ListCell<LocationTagContent> {
        /**
         *  Container of LTC content
         */
        private HBox content;
        /**
         *  name of location
         */
        private Text name;
        /**
         *  formatted address of location (Intersection)
         */
        private Text address;
        /**
         *  button to edit a LTC (change duration and choose location on map)
         */
        private Button editButton;
        /**
         *  button to delete request from timeline
         */
        private Button deleteButton;
        /**
         *  move the location up in the list
         */
        private Button upButton;
        /**
         *  move the location down in the list
         */
        private Button downButton;

        /**
         * Constructor for a Cell of the Custom Modify List View.
         */
        public CustomModifyListCell() {
            super();
            name = new Text();
            name.setStyle("-fx-fill: #595959");
            name.setFont(Font.font("SF Pro Display", 20.0));
            address = new Text();
            address.setStyle("-fx-fill: #595959");
            address.setFont(Font.font("SF Pro Display", 12.0));
            editButton = new Button("");
            editButton.setGraphic(new ImageView("sample/edit.png"));
            deleteButton = new Button("");
            deleteButton.setGraphic(new ImageView("sample/delete.png"));
            upButton = new Button("");
            upButton.setGraphic(new ImageView("sample/up.png"));
            downButton = new Button("");
            downButton.setGraphic(new ImageView("sample/down.png"));


            editButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler fot Editing a location
                 */
                @Override
                public void handle(ActionEvent event) {
                    //call function to edit an item
                    //you can pass the related LocationItemContent to edit the contents of the list (cards) with .getItem()
                    editSetup( getItem());

                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler fot Deleting a location
                 */
                @Override
                public void handle(ActionEvent event) {
                    if (cards.size()>3)
                    {
                        logger.info(cards.toString());
                        int requestIndex = planningRequest.getRequestList().indexOf(getItem().request);
                        int removedCardIndex1 = cards.indexOf(getItem());
                        int cursor = 0;

                        for( LocationTagContent ltc: cards ){
                            if( Integer.parseInt(ltc.name.split(" ")[1]) == Integer.parseInt(getItem().name.split(" ")[1])
                                    && ltc.getName() != getItem().getName() ){
                                //cards.remove(ltc);
                                break;
                            }
                            cursor++;
                        }

                        // Change from ModifyState to RemoveState
                        mvcController.removeRequest();
                        mvcController.removeDone(planningRequest, cards, requestIndex, removedCardIndex1, cursor);
                        refreshModel();
                        logger.info(cards.toString());
                        list.getChildren().remove(list.getChildren().size() -1);
                        displayRequests(false);
                        addCardsToScreen(true);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Attention!");
                        alert.setHeaderText(null);
                        alert.setContentText("You must have at least one request in your timeline.");

                        alert.showAndWait();
                    }


                }
            });

            upButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler fot Moving Up a location
                 */
                @Override
                public void handle(ActionEvent event) {

                    int index = cards.indexOf(getItem());
                    if(index > 0)
                    {

                        if (Integer.parseInt(cards.get(index).getName().split(" ")[1]) == Integer.parseInt(cards.get(index-1).getName().split(" ")[1]))
                        {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Attention!");
                            alert.setHeaderText(null);
                            alert.setContentText("You cannot place a delivery before the pickup for a request");

                            alert.showAndWait();
                        } else {
                            logger.info(cards.toString());
                            //call to refresh the content?
                            mvcController.swapRequest(index, index - 1, cards);
                            refreshModel();
                            logger.info(cards.toString());
                            list.getChildren().remove(list.getChildren().size() - 1);
                            addCardsToScreen(true);
                        }
                    }
                }
            });

            downButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler fot Moving Down a location
                 */
                @Override
                public void handle(ActionEvent event) {

                    int index = cards.indexOf(getItem());

                    if(index < cards.size() -1) //or just size?
                    {
                        if (Integer.parseInt(cards.get(index).getName().split(" ")[1]) == Integer.parseInt(cards.get(index+1).getName().split(" ")[1]))
                        {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Attention!");
                            alert.setHeaderText(null);
                            alert.setContentText("You cannot place a delivery before the pickup for a request");

                            alert.showAndWait();
                        } else {
                            logger.info(cards.toString());
                            //call to refresh the content?
                            mvcController.swapRequest(index, index+1, cards);
                            refreshModel();
                            logger.info(cards.toString());
                            list.getChildren().remove(list.getChildren().size() -1);
                            addCardsToScreen(true);
                        }

                    }
                }
            });

            undoButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler fot Undo an Action
                 */
                @Override
                public void handle(ActionEvent event) {
                    logger.info("undo is clicked");
                    mvcController.Undo();
                    refreshModel();
                    logger.info(cards.toString());
                    list.getChildren().remove(list.getChildren().size() -1);
                    addCardsToScreen(true);
                    displayRequests(false);
                }
            });

            redoButton.setOnAction(new EventHandler<ActionEvent>() {
                /**
                 * Event handler for Redo an Action
                 */
                @Override
                public void handle(ActionEvent event) {
                    logger.info("redo is clicked");
                    mvcController.Redo();
                    refreshModel();
                    logger.info(cards.toString());
                    list.getChildren().remove(list.getChildren().size() -1);
                    addCardsToScreen(true);
                    displayRequests(false);
                }
            });

            VBox vBox = new VBox(name, address);
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            VBox vBox2 = new VBox(upButton, downButton);
            vBox2.setAlignment(Pos.CENTER_RIGHT);
            content = new HBox(vBox, region, editButton, deleteButton, vBox2);
            content.setAlignment(Pos.CENTER);
            content.setSpacing(10);
        }

        /**
         * Updates the contents of a LocationTagCOntent
         * @param item the item to modify
         * @param empty see if the paraeters are empty
         */
        @Override
        protected void updateItem(LocationTagContent item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) { // <== test for null item and empty parameter
                name.setText(item.getName());
                address.setText(item.getIntersection());
                setGraphic(content);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * Refresh the Model - show the new calculated tour on the map
     *
     */
    protected void refreshModel() {
        map = mvcController.getMap();
        planningRequest = mvcController.getPlanningRequest();
        tour = mvcController.getTour();
        cards = mvcController.getLtcList();
    }

}