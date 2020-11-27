package sample;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    /** Logger for the class */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception{

        logger.info("starting DemoApp");
        String fxmlFile = "/sample/sample3_Fields.fxml";
        logger.info("loading fxml file {}");

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        logger.info("stage loaded");

        final Controller controller = fxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        controller.initMapAndControls(projection);

        //controller.initCardContent();

        Scene scene = new Scene(rootNode);
        logger.info("scene created");



        primaryStage.setTitle("Delivery UI Demo");
        primaryStage.setScene(scene);
        logger.info("showing scene");
        primaryStage.show();
        logger.info("application start method finished.");
    }


    public static void main(String[] args) {
        logger.info("begin main");
        launch(args);
        logger.info("end main");
        System.exit(0);
    }
}
