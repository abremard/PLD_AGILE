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

public class Controller {

    /** custom images*/
    String pickupImageFile = "/images/pickupMarker.png";
    String deliveryImageFile = "/images/deliveryMarker.png";
    String depotImageFile = "/images/depotMarker.png";

    /** logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML
    private MapView mapView;

    @FXML
    private ListView listView;

    @FXML
    private AnchorPane list;

    @FXML
    private Button mainButton;

    @FXML
    private Button secondButton;

    @FXML
    private Button mapButton;

    @FXML
    private Button undoButton;

    @FXML
    private Button redoButton;

    @FXML
    private Button requestButton;

    @FXML
    private TextField mapField;

    @FXML
    private TextField requestField;

    @FXML
    private Text mapText;

    @FXML
    private Text requestText;

    private boolean addingRequest;
    private int addedReqCount;
    private Request tempRequest;

    private ArrayList<CoordinateLine> coordLines;
    private ArrayList<CoordinateLine> tourLines;
    private ArrayList<CoordinateLine> selectedLines;
    private ArrayList<Marker> markers;
    private ArrayList<LocationTagContent> cards;

    private Map map = null;
    private PlanningRequest planningRequest;
    private Tournee tour;

    private Color mapColor = new Color(0.40,0.40,0.40, 1.0);
    private Color pathColor = new Color(0.2,0.6,1.0, 1.0);
    private Color selectionColor = new Color(1.0,0.4,0.0, 1.0);

    private boolean isTimeline = false;
    private boolean isModify = false;
    private boolean isAddRequest = false;

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

    private MVCController mvcController;

    // CONSTRUCTOR
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
        tempRequest = new Request( new Intersection(0,0), new Intersection(0,0), 0,0);
    }

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

    public void initEventHandlers(){

        logger.info("Setting up event handlers");

        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume(); // not sure what this does tbh

            if( addingRequest ){
                handleNewRequestClick(event);
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

                requestButton.setDisable(false);
                requestField.setDisable(false);
                secondButton.setDisable(false);
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

                mvcController.LoadRequestPlan(file.getAbsolutePath());
                refreshModel();

                displayRequests();

                mainButton.setDisable(false);

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
                    mvcController.applyModificationDone(map, planningRequest, order);
                    refreshModel();
                    displayTour();
                    //call method that places results on timeline
                    initCardContent();

                    //update button position
                    isTimeline = false; //--> will execute the else of isTimeline, and will pas to isTimeine true
                    isAddRequest = false;
                    isModify = false;
                }
                //detect on which view we are - File Picker or Timeline
                else if (isAddRequest) {
                    //CANCEL ADD REQUEST OPERATION, BACK TO MODIFY VIEW
                    modifySetup(false);
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

                    //clear everything from map
                    //tourLines.clear();
                    //markers.clear();
                    //coordLines.clear();

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
                    logger.info(map.toString());
                    logger.info(planningRequest.toString());
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
                    tempRequest.setPickupDur(60*Double.parseDouble(mapField.getText()));
                    tempRequest.setDeliveryDur(60*Double.parseDouble(requestField.getText()));

                    mvcController.addDone(planningRequest, map, cards, tempRequest);
                    AddRequestCommand cmd = (AddRequestCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                    refreshModel();

                    // planningRequest.addRequest(planningRequest.getRequestList().size(), tempRequest);
                    // System.out.println(planningRequest.toString());

                    displayTour();
                    //call method that places results on timeline
                    initCardContent();
                    cmd.setNewLtcList(new ArrayList<>(cards));
                    list.getChildren().remove(list.getChildren().size() -1);
                    //compute tour
                    //back to modify
                    modifySetup(false);
                }
                else if (isModify) {
                    addingRequest = true;
                    addedReqCount = 0;
                    addRequestSetup();
                    mvcController.addRequest();
                } else {
                    modifySetup(true);
                    mvcController.ModifyRequestList();
                }
            }
        });

        logger.info("Finished setting up event handlers");

    }

    private void addRequestSetup() {
        undoButton.setVisible(false);
        redoButton.setVisible(false);

        mainButton.setText("Cancel");
        secondButton.setText("Add");
        list.getChildren().remove(list.getChildren().size() -1);
        mapText.setText("Pickup Duration");
        requestText.setText("Delivery Duration");
        mapField.setText("0");
        requestField.setText("0");
        mapText.setVisible(true);
        requestText.setVisible(true);
        requestField.setVisible(true);
        mapField.setVisible(true);

        isAddRequest = true;
        isModify = false;
        isTimeline = false;
    }

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
    }

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

    public void removeFromMap(ArrayList<CoordinateLine> list)
    {
        for (CoordinateLine cl: list) {
            mapView.removeCoordinateLine(cl);
        }
    }

    public void removeFromMapMarker(ArrayList<Marker> list)
    {
        for (Marker m: list) {
            mapView.removeMarker(m);
        }
    }

    public void displayRequests(){

        removeFromMap(selectedLines);
        removeFromMap(tourLines);
        removeFromMapMarker(markers);
        selectedLines.clear();
        tourLines.clear();
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
            Coordinate coordDelivery = new Coordinate(delivery.getLatitude(),delivery.getLongitude());
            Marker markerDelivery = new Marker( getClass().getResource(deliveryImageFile),-11,-25).setPosition(coordDelivery).setVisible(true);
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

    public void handleNewRequestClick(MapViewEvent event){

        if( addedReqCount > 1){
            return;
        }


        Intersection newIntersection = new Intersection( event.getCoordinate().getLatitude(), event.getCoordinate().getLongitude());
        newIntersection = map.findClosestIntersection(newIntersection);
        logger.info(" adding new intersection @" + newIntersection.toString());

        String file = ( addedReqCount%2 == 0)? pickupImageFile : deliveryImageFile;
        Coordinate coordIntersection = new Coordinate(newIntersection.getLatitude(), newIntersection.getLongitude());
        Marker newMarker = new Marker( getClass().getResource(file),-12,-12).setPosition(coordIntersection).setVisible(true);
        markers.add(newMarker);
        mapView.addMarker(newMarker);

        if( addedReqCount%2 == 0){
            tempRequest.setPickup(newIntersection);
            //tempRequest.setPickupDur(5);
        } else if ( addedReqCount%2 == 1){
            tempRequest.setDelivery(newIntersection);
            //tempRequest.setDelivery_dur(5);
        }


        addedReqCount++;
        if( addedReqCount == 2 ){

            //secondButton.setDisable(false);
        }

    }

    //METHOD THAT CREATES CARDS
    public void initCardContent() {
        cards.clear();
        int nbPickup = 1;
        int nbDelivery = 1;
        ArrayList<TupleRequete> points = tour.getPtsPassage();
        if(points != null) logger.info("Retrouve un objet nulllllllllll");
        for (TupleRequete pt: points) {
            String name = "";
            String street1 = "";
            String street2 = "";
            double latitude = 0.0;
            double longitude = 0.0;
            if (pt.isDepart())
            {
                name = "Pickup "+nbPickup;
                nbPickup++;
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
            } else
            {
                name = "Delivery "+nbDelivery;
                if(nbDelivery == (int)((points.size()+1)/2)) { name = "Back to shop"; }
                nbDelivery++;
                ArrayList<String> street = map.getSegmentNameFromIntersectionId(pt.getRequete().getDelivery().getId());
                logger.info(street.toString());
                street1 = street.get(0);
                if (street.size() >= 2) {
                    street2 = street.get(1);
                }
                latitude = pt.getRequete().getDelivery().getLatitude();
                longitude = pt.getRequete().getDelivery().getLongitude();
            }

            LocalTime time  = pt.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            Coordinate location = new Coordinate(latitude, longitude);

            LocationTagContent item = new LocationTagContent(name, street1, street2, time.format(formatter), location, pt.getChemin(), pt.getRequete());
            cards.add(item);
        }

        addCardsToScreen(false);
        mvcController.setLtcList(cards);

    }

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

    //finishes setup after the mpa is initialzed
    private void afterMapIsInitialized() {
        logger.info("map intialized");
        logger.info("setting center and enabling controls...");
        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordLyon);

    }

    //CLASS THAT MODELS CONTENT OF CARDS TO SHOW IN TIMELINE
    public static class LocationTagContent {
        //ex. "Pickup 1", or "Delivery 3"
        private String name;
        //streets of the intersection
        private String streetOne;
        private String streetTwo;
        //arrival time
        private String arrivalTime;
        //coordinates of location
        private Coordinate coordLocation;
        private Request request;
        private ArrayList<Segment> chemin;

        public String getName() {
            return name;
        }
        public String getIntersection() {
            return streetOne + "\n" + streetTwo;
        }
        public String getArrivalTime() {
            return arrivalTime;
        }
        public LocationTagContent(String name, String streetOne, String streetTwo, String arrivalTime, Coordinate coordLocation, ArrayList<Segment> chemin, Request req) {
            super();
            this.name = name;
            this.streetOne = streetOne;
            this.streetTwo = streetTwo;
            this.arrivalTime = arrivalTime;
            this.coordLocation = coordLocation;
            this.chemin = chemin;
            this.request = req;
        }

        public String toString() {
            return name;
        }
    }

    //CLASS THAT STYLES CONTENT OF CARD INTO A LIST CELL
    private class CustomListCell extends ListCell<LocationTagContent> {
        private HBox content;
        private Text name;
        private Text address;
        private Text arrivalText;
        private Text arrivalTime;

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

    //CLASS THAT STYLES CONTENT OF CARD INTO A LIST CELL
    private class CustomModifyListCell extends ListCell<LocationTagContent> {
        private HBox content;
        private Text name;
        private Text address;
        private Button editButton;
        private Button deleteButton;
        private Button upButton;
        private Button downButton;

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
                @Override
                public void handle(ActionEvent event) {
                    //call function to edit an item
                    //you can pass the related LocationItemContent to edit the contents of the list (cards) with .getItem()
                }
            });

            deleteButton.setOnAction(new EventHandler<ActionEvent>() {
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

                        // TODO : call to refresh the content?
                        mvcController.removeRequest();
                        mvcController.removeDone(planningRequest, cards, requestIndex, removedCardIndex1, cursor);
                        refreshModel();
                        logger.info(cards.toString());
                        list.getChildren().remove(list.getChildren().size() -1);
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
                @Override
                public void handle(ActionEvent event) {
                    // TODO : verify pickup before delivery
                    int index = cards.indexOf(getItem());
                    if(index > 0)
                    {
                        // LocationTagContent temp = cards.get(index);
                        // cards.set(index, cards.get(index-1));
                        // cards.set(index-1, temp);
                        logger.info(cards.toString());
                        //call to refresh the content?
                        mvcController.swapRequest(index, index-1, cards);
                        refreshModel();
                        logger.info(cards.toString());
                        list.getChildren().remove(list.getChildren().size() -1);
                        addCardsToScreen(true);
                    }
                }
            });

            downButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // TODO : verify pickup before delivery
                    int index = cards.indexOf(getItem());
                    if(index < cards.size() -1) //or just size?
                    {
                        // LocationTagContent temp = cards.get(index);
                        // cards.set(index, cards.get(index+1));
                        // cards.set(index+1, temp);
                        logger.info(cards.toString());
                        //call to refresh the content?
                        mvcController.swapRequest(index, index+1, cards);
                        refreshModel();
                        logger.info(cards.toString());
                        list.getChildren().remove(list.getChildren().size() -1);
                        addCardsToScreen(true);
                    }
                }
            });

            undoButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    logger.info("undo is clicked");
                    mvcController.Undo();
                    refreshModel();
                    logger.info(cards.toString());
                    list.getChildren().remove(list.getChildren().size() -1);
                    addCardsToScreen(true);
                }
            });

            redoButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    logger.info("redo is clicked");
                    mvcController.Redo();
                    refreshModel();
                    logger.info(cards.toString());
                    list.getChildren().remove(list.getChildren().size() -1);
                    addCardsToScreen(true);
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

    protected void refreshModel() {
        map = mvcController.getMap();
        planningRequest = mvcController.getPlanningRequest();
        tour = mvcController.getTour();
        cards = mvcController.getLtcList();
    }

}
