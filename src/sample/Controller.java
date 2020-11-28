package sample;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.offline.OfflineCache;
import command.ComputeTourCommand;
import command.LoadMapCommand;
import command.LoadRequestPlanCommand;
import command.NewTourCommand;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
// import java.awt.*;
import java.io.File;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import processing.TupleRequete;

import java.util.LinkedList;

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
    private Button mapButton;

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

    private ArrayList<CoordinateLine> coordLines;
    private ArrayList<CoordinateLine> tourLines;
    private ArrayList<CoordinateLine> selectedLines;
    private ArrayList<Marker> markers;
    private ArrayList<LocationTagContent> cards;

    private Map map = null;
    private PlanningRequest planningRequest;
    private Tournee tour;

    private boolean isTimeline = false;

    private static final Coordinate coordKarlsruheHarbour = new Coordinate(45.77087932755228, 4.863621380475198);

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

    public Controller()
    {
        mvcController = new MVCController();
        coordLines = new ArrayList<CoordinateLine>();
        tourLines = new ArrayList<CoordinateLine>();
        markers = new ArrayList<Marker>();
        cards = new ArrayList<LocationTagContent>();
        selectedLines = new ArrayList<CoordinateLine>();
    }

    public void initMapAndControls(Projection projection) {
        logger.info("begin initialize");

        final FileChooser fileChooser = new FileChooser();

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";

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

        //Initialise the File chooser buttons with their handlers
        mapButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(new Stage());
                mapField.setText(file.getAbsolutePath());

                mvcController.LoadMap(file.getAbsolutePath());
                LoadMapCommand mapCommand = (LoadMapCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                map = mapCommand.getMap();
                coordLines.clear();
                displayMap();

                requestButton.setDisable(false);
                requestField.setDisable(false);

                //ADD METHOD TO DISPLAY ON MAP - DRAW LINES OF SEGMENTS
                //MAKE SURE TO CHANGE POSITION OF MAP TO DISPLAY AREA WHERE SEGMENTS WERE PLACED
                //use Coordinate to store all points of intersection - possibly add this to the model
                //View Aydins code - place line for the list of segments
                //Create Extent object and use Extent.forCoordinates(List<Coordinate>) to create the extent of coordinates that were placed on map - intersections
                //On the MapView object use mapView.setExtent(extent) to position the map to display all points
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
                LoadRequestPlanCommand requestCommand = (LoadRequestPlanCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                planningRequest = requestCommand.getPlanningRequest();
                markers.clear();
                displayRequests();

                mainButton.setDisable(false);
                //ADD METHOD TO DISPLAY ON MAP - PLACE POINTS OF REQUESTS AND DELIVERY POINTS
                //MAKE SURE TO CHANGE POSITION AND SCALE OF MAP TO DISPLAY AREA WHERE POINTS ARE PLACED
                // use Coordinate to store all points of intersection where location is - possibly add this to the model
                // View Aydins code - place appripriate marker (delivery or pickup point)
                // Create Extent object and use Extent.forCoordinates(List<Coordinate>) to create the extent of coordinates that were placed on map - intersections
                // On the MapView object use mapView.setExtent(extent) to position the map to display all points
            }
        });

        //Listener on Main button that switches between views
        mainButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //detect on which view we are - File Picker or Timeline
                if(isTimeline)
                {
                    //delete list of timeline
                    list.getChildren().remove(list.getChildren().size() - 1);

                    //show file picker elements
                    mapButton.setVisible(true);
                    requestButton.setVisible(true);
                    mapField.setVisible(true);
                    requestField.setVisible(true);
                    requestText.setVisible(true);
                    mapText.setVisible(true);

                    // reset MVC State
                    mvcController.Reset();

                    //clear everything from map
                    tourLines.clear();
                    markers.clear();
                    //coordLines.clear();

                    //change text of button
                    mainButton.setText("Calculate Tour");

                    isTimeline = false;
                } else
                {
                    //hide elements from file picker view
                    mapButton.setVisible(false);
                    requestButton.setVisible(false);
                    mapField.setVisible(false);
                    requestField.setVisible(false);
                    requestText.setVisible(false);
                    mapText.setVisible(false);

                    //change text of button
                    mainButton.setText("Change Files");

                    //get files, pass them to the algo, calculate path, get results
                    logger.info(map.toString());
                    logger.info(planningRequest.toString());
                    //call method that places results on the map
                    mvcController.ComputeTour(map, planningRequest);
                    ComputeTourCommand tourCommand = (ComputeTourCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                    tour = tourCommand.getTournee();
                    displayTour();
                    //call method that places results on timeline
                    initCardContent();

                    isTimeline = true;
                }

            }
        });



        logger.info("initialization finished");

    }

    private void displayMap() {

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
            cl.setVisible(true).setColor(Color.BLACK).setWidth(1);
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
            cl.setVisible(true).setColor(Color.RED).setWidth(2);
            tourLines.add(cl);
            mapView.addCoordinateLine(cl);
        }
    }

    public void displaySegmentTour(ArrayList<Segment> path)
    {
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
            cl.setVisible(true).setColor(Color.BLUE).setWidth(2);
            selectedLines.add(cl);
            mapView.addCoordinateLine(cl);
        }
    }

    public void displayRequests(){

        ArrayList<Intersection> listIntersection = map.getIntersectionList();

        Intersection delivery = null;
        Intersection pickup = null;
        Intersection depot = null;

        long idDepot = planningRequest.getDepot().getAdresse().getId();
        depot = map.matchIdToIntersection(idDepot);
        planningRequest.getDepot().setAdresse(depot);


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

    //CLASS THAT MODELS CONTENT OF CARDS TO SHOW IN TIMELINE
    private static class LocationTagContent {
        //ex. "Pickup 1", or "Delivery 3"
        private String name;
        //streets of the intersection
        private String streetOne;
        private String streetTwo;
        //arrival time
        private String arrivalTime;
        //coordinates of location
        private Coordinate coordLocation;
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
        public LocationTagContent(String name, String streetOne, String streetTwo, String arrivalTime, Coordinate coordLocation, ArrayList<Segment> chemin) {
            super();
            this.name = name;
            this.streetOne = streetOne;
            this.streetTwo = streetTwo;
            this.arrivalTime = arrivalTime;
            this.coordLocation = coordLocation;
            this.chemin = chemin;
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
                street1 = Double.toString(pt.getRequete().getPickup().getLatitude()) ;
                latitude = pt.getRequete().getPickup().getLatitude();
                street2 = Double.toString(pt.getRequete().getPickup().getLongitude());
                longitude = pt.getRequete().getPickup().getLatitude();
            } else
            {
                name = "Delivery "+nbDelivery;
                nbDelivery++;
                street1 = Double.toString(pt.getRequete().getDelivery().getLatitude());
                latitude = pt.getRequete().getDelivery().getLatitude();
                street2 = Double.toString(pt.getRequete().getDelivery().getLongitude());
                longitude = pt.getRequete().getDelivery().getLongitude();
            }

            LocalTime time  = pt.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            Coordinate location = new Coordinate(latitude, longitude);

            LocationTagContent item = new LocationTagContent(name, street1, street2, time.format(formatter), location, pt.getChemin());
            cards.add(item);
        }

        logger.info("creating data");
        ObservableList<LocationTagContent> data = FXCollections.observableArrayList();
        data.addAll(cards);
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
                return new CustomListCell();
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
               mapView.setCenter(lt.coordLocation);
               displaySegmentTour(lt.chemin);
            }
        });
    }


    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {
        logger.info("map intialized");
        logger.info("setting center and enabling controls...");
        // start at the harbour with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordKarlsruheHarbour);

    }

}
