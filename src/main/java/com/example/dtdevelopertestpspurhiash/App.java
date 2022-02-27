package com.example.dtdevelopertestpspurhiash;

import com.example.dtdevelopertestpspurhiash.controllers.AssetsController;
import com.example.dtdevelopertestpspurhiash.controllers.ImageViewerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class App extends Application {

    private AssetsController assetsController;

    private final double IMAGE_VIEWER_SCENE_MIN_HEIGHT = 480.;
    private final double IMAGE_VIEWER_SCENE_MIN_WIDTH = 640.;
    private final String IMAGE_VIEWER_STAGE_TITLE = "Image Viewer";
    private final String GALLERY_STAGE_TITLE = "Gallery";
    private final String IMAGE_VIEWER_FXML_PATH = "image-viewer.fxml";
    private final String GALLERY_FXML_PATH = "assets.fxml";

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void start(Stage primaryStage) throws IOException {
        //ImageView stage
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(IMAGE_VIEWER_FXML_PATH));
        Scene scene = new Scene(fxmlLoader.load(), IMAGE_VIEWER_SCENE_MIN_WIDTH, IMAGE_VIEWER_SCENE_MIN_HEIGHT, false, SceneAntialiasing.DISABLED);
        scene.setCamera(new PerspectiveCamera());
        primaryStage.setTitle(IMAGE_VIEWER_STAGE_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(IMAGE_VIEWER_SCENE_MIN_HEIGHT);
        primaryStage.setMinWidth(IMAGE_VIEWER_SCENE_MIN_WIDTH);
        primaryStage.show();

        ImageViewerController imageViewerController = fxmlLoader.getController();

        //Gallery stage
        fxmlLoader = new FXMLLoader(getClass().getResource(GALLERY_FXML_PATH));
        scene = new Scene(fxmlLoader.load());
        logger.info("FXML loaded, application started.");
        Stage childStage = new Stage();
        childStage.setTitle(GALLERY_STAGE_TITLE);
        childStage.setScene(scene);

        childStage.setAlwaysOnTop(true);
        childStage.show();
        assetsController = fxmlLoader.getController();
        assetsController.setImageViewerController(imageViewerController);
        assetsController.setPrimaryStage(primaryStage);

        //add some space between stages
        childStage.setX(primaryStage.getX() + primaryStage.getWidth() + 50.);
        childStage.setY(primaryStage.getY());

        //on close event
        childStage.setOnCloseRequest(event -> {
            primaryStage.close();
        });
        primaryStage.setOnCloseRequest(event -> {
            childStage.close();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}