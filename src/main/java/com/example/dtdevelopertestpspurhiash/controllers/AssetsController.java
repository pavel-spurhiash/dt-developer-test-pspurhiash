package com.example.dtdevelopertestpspurhiash.controllers;

import com.example.dtdevelopertestpspurhiash.PropertyUtil;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssetsController {

    private final ArrayList<File> listFiles = new ArrayList<>();
    private final ArrayList<Label> labels = new ArrayList<>();
    private final String IMAGE_EXTENSION_FILTER = "*.png";

    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private ImageViewerController imageViewerController;
    private Stage primaryStage;

    private double IMAGE_PREVIEW_SIZE;
    private Integer IMAGES_PER_PAGE;
    private String WORK_FOLDER;


    public void setImageViewerController(ImageViewerController imageViewerController) {
        this.imageViewerController = imageViewerController;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private ListView<Node> listView;

    @FXML
    private TextField searchTextField;

    @FXML
    private Pagination pagination;

    @FXML
    private void searchButtonClicked() {
        inputChanged();
    }

    @FXML
    private void addFileClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(IMAGE_EXTENSION_FILTER, IMAGE_EXTENSION_FILTER));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            File assetsFolder = new File(WORK_FOLDER);

            if (!assetsFolder.exists()) {
                assetsFolder.mkdir();
                logger.warn("Created empty assets folder.");
            }

            Path from = Paths.get(file.toURI());
            Path to = Paths.get(new File(WORK_FOLDER + file.getName()).toURI());

            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            };

            try {
                Files.copy(from, to, options);
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateListFile();
            updateUI();
        }
    }

    @FXML
    private void inputChanged() {
        String searchString = searchTextField.getText();
        updateListFile();
        logger.info("Search: " + searchString);
        if (!searchString.isEmpty()) {
            listFiles.removeIf(file -> !file.getName().toUpperCase().contains(searchString.toUpperCase()));
        }
        updateUI();
    }

    private void updateUI() {
        int pageCount = (int) Math.ceil((double) listFiles.size() / IMAGES_PER_PAGE);
        if (pageCount == 0) {
            pageCount++;
        }
        pagination.setPageCount(pageCount);
        showPage(pagination.getCurrentPageIndex());
    }

    private void showPage(Integer page) {
        logger.info("Page " + page);
        int fromIndex = IMAGES_PER_PAGE * page;
        int toIndex = Math.min(IMAGES_PER_PAGE * (page + 1), listFiles.size());
        List<File> output = listFiles.subList(fromIndex, toIndex);
        listView.getItems().clear();

        for (int i = 0; i < labels.size(); i++) {
            Label label = labels.get(i);
            if (output.size() > i) {
                ImageView imageView = (ImageView) label.getGraphic();
                Image img = new Image(output.get(i).toURI().toString(), IMAGE_PREVIEW_SIZE, IMAGE_PREVIEW_SIZE, false, true, true);
                imageView.setImage(img);
                label.setGraphic(imageView);
                label.setText(output.get(i).getName());
                listView.getItems().add(label);
            }
        }
    }

    private void updateListFile() {
        File folder = new File(WORK_FOLDER);
        if (folder.exists() && folder.isDirectory()) {
            File[] list = folder.listFiles();
            listFiles.clear();

            if (list != null) {
                for (File file : list) {
                    if (!file.isDirectory()) {
                        listFiles.add(file);
                    }
                }
            }
        }
    }

    @FXML
    private void initialize() {
        logger.info("AssetsController initialize");

        PropertyUtil propertyUtil = new PropertyUtil();
        IMAGES_PER_PAGE = Integer.parseInt(propertyUtil.getProperty("IMAGES_PER_PAGE"));
        IMAGE_PREVIEW_SIZE = Double.parseDouble(propertyUtil.getProperty("IMAGE_PREVIEW_SIZE"));
        WORK_FOLDER = propertyUtil.getProperty("WORK_FOLDER");

        for (int i = 0; i < IMAGES_PER_PAGE; i++) {
            ImageView imageView = new ImageView();
            imageView.setViewport(new Rectangle2D(0, 0, IMAGE_PREVIEW_SIZE, IMAGE_PREVIEW_SIZE));
            Label label = new Label(null, imageView);
            labels.add(label);
        }
        listView.getItems().addAll(labels);
        //preview image clicked event
        listView.setOnMouseClicked(event -> {
            int itemIndex = listView.getSelectionModel().getSelectedIndex();
            if (itemIndex >= 0) {
                int currentPage = pagination.getCurrentPageIndex();
                int id = (currentPage * IMAGES_PER_PAGE) + itemIndex;

                Image img = new Image(listFiles.get(id).toURI().toString());
                imageViewerController.changeImage(img);
            }
        });
        //pagination changed event
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> showPage((Integer) newIndex));

        updateListFile();
        updateUI();
    }
}