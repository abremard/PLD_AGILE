package sample;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.offline.OfflineCache;
import command.LoadMapCommand;
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
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.stage.FileChooser;
import objects.Intersection;
import objects.Map;
import objects.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
// import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import java.util.LinkedList;

public class Controller {

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

        //Initialise the File chooser buttons with their handlers
        mapButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(new Stage());
                mapField.setText(file.getAbsolutePath());

                mvcController.LoadMap(file.getAbsolutePath());
                LoadMapCommand mapCommand = (LoadMapCommand) mvcController.getL().getL().get(mvcController.getL().getI());
                Map myMap = mapCommand.getMap();
                displayMap(myMap);
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

                    //clear everything from map

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

                    //call method that places results on the map

                    //call method that places results on timeline
                    initCardContent();

                    isTimeline = true;
                }

            }
        });

        logger.info("initialization finished");

    }

    private void displayMap(Map map) {
        ArrayList<Segment> listSegments = map.getSegmentList();
        ArrayList<Intersection> listIntersection = map.getIntersectionList();
        logger.info(listSegments.toString());
        logger.info(listIntersection.toString());
        for (Segment segment: listSegments) {
            long idOrigin = segment.getOrigin();
            long idDestination = segment.getDestination();
            Intersection ptOrigin = null;
            Intersection ptDestination = null;
            for (Intersection intersection: listIntersection) {
                long idIntersection = intersection.getId();
                if (idIntersection == idOrigin) {
                    ptOrigin = intersection;
                }
            }
            for (Intersection intersection: listIntersection) {
                long idIntersection = intersection.getId();
                if (idIntersection == idDestination) {
                    ptDestination = intersection;
                }
            }
            Coordinate coordOrigin = new Coordinate(ptOrigin.getLatitude(), ptOrigin.getLongitude());
            Coordinate coordDestination = new Coordinate(ptDestination.getLatitude(), ptDestination.getLongitude());
            // Extent extent = Extent.forCoordinates();
            CoordinateLine coordLine = new CoordinateLine(coordOrigin, coordDestination);
            coordLine.setVisible(true).setColor(Color.BLACK).setWidth(1);
            mapView.addCoordinateLine(coordLine);
        }
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

        public String getName() {
            return name;
        }
        public String getIntersection() {
            return streetOne + "\n" + streetTwo;
        }
        public String getArrivalTime() {
            return arrivalTime;
        }
        public LocationTagContent(String name, String streetOne, String streetTwo, String arrivalTime) {
            super();
            this.name = name;
            this.streetOne = streetOne;
            this.streetTwo = streetTwo;
            this.arrivalTime = arrivalTime;
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
            name.setFont(Font.font("Arial", 20.0));
            address = new Text();
            address.setStyle("-fx-fill: #595959");
            address.setFont(Font.font("Arial", 12.0));
            arrivalText = new Text("arrive by");
            arrivalText.setStyle("-fx-fill: #595959");
            arrivalText.setFont(Font.font("Arial", 12.0));
            arrivalText.setTextAlignment(TextAlignment.RIGHT);
            arrivalTime = new Text();
            arrivalTime.setStyle("-fx-fill: #595959");
            arrivalTime.setFont(Font.font("Arial Black", 25.0));
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
        logger.info("creating data");
        ObservableList<LocationTagContent> data = FXCollections.observableArrayList();
        data.addAll(new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"),new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"), new LocationTagContent("Pickup 1", "Rue Philomène Magnin", "Avenue Lacassagne", "8:12"));
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
