package com.example.dtdevelopertestpspurhiash.controllers;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;

public class ImageViewerController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private BorderPane stackPane;

    @FXML
    public ImageView image;

    @FXML
    private CheckMenuItem reflectionMenuItem;

    @FXML
    private CheckMenuItem fullscreenMenuItem;

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private RotateTransition rotator;

    @FXML
    private void reflectionClicked() {
        if (reflectionMenuItem.isSelected()) {
            image.setEffect(new Reflection());
        } else {
            image.setEffect(null);
        }
    }

    @FXML
    private void aboutClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("https://github.com/pavel-spurhiash");
        alert.setContentText("OS name: " + System.getProperty("os.name") + "\nJava version: " + System.getProperty("java.version"));
        alert.showAndWait();
    }

    @FXML
    private void fullscreenClicked() {
        update();
    }

    public void changeImage(Image img) {
        if (rotator.getStatus() != Animation.Status.PAUSED) {
            rotator.jumpTo(Duration.ZERO);
            rotator.pause();
        }
        image.setImage(img);
        update();
    }

    private void update() {
        double sceneWidth = stackPane.getWidth();
        double sceneHeight = stackPane.getHeight() - menuBar.getHeight();
        double imageWidth = image.getImage().getWidth();
        double imageHeight = image.getImage().getHeight();

        if (fullscreenMenuItem.isSelected() || (imageWidth > sceneWidth || imageHeight > sceneHeight)) {
            image.setFitWidth(sceneWidth);
            image.setFitHeight(sceneHeight);
        } else {
            image.setFitWidth(imageWidth);
            image.setFitHeight(imageHeight);
        }
    }

    private RotateTransition createRotator(Node card) {
        rotator = new RotateTransition(Duration.millis(10000), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360);
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(-1);

        return rotator;
    }

    @FXML
    private void initialize() {
        logger.info("ImageViewerController initialize");
        //window resize event handler
        stackPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> update());
        stackPane.heightProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> update());
        //add some pretty animation if no image is selected
        rotator = createRotator(image);
        rotator.play();
    }

}