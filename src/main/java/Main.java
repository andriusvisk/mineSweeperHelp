import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.opencv.core.Core;

public class Main extends Application {

    private static Logger log = LogManager.getLogger(Main.class);

    private ControllerMain controller;

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Configurator.initialize(new DefaultConfiguration());
        Configurator.setRootLevel(Level.TRACE);

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            BorderPane rootElement = new BorderPane();
            controller = new ControllerMain();
            controller.init(primaryStage, rootElement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}