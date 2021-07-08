package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RunController {
    @FXML
    ListView neuralListView;
    @FXML
    ListView resultListView;
    @FXML
    ListView stockListView;
    @FXML
    TextField dateEndToTextField;
    @FXML
    TextField periodStartTextField;
    @FXML
    TextField periodEndTextField;
    @FXML
    TextField numOfDaysIncTextField;
    @FXML
    TextField degreeOfIncStockTextField;
    @FXML
    ImageView imageView;
    @FXML
    Button backButton;

    private Image baseImg;
    private Image[] downloadImg;
    private Image failImg;
    private Image[] runningImg;
    private String slash;

    private void initialize() {
        this.slash = UtilMethods.slash;
        downloadImg = new Image[3];
        runningImg = new Image[8];

        String addr = slash + "Images" + slash + "Run" + slash + "Base.png";
        baseImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < downloadImg.length; i++) {
            addr = slash + "Images" + slash + "Run" + slash + "Down" + i + ".png";
            downloadImg[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        addr = slash + "Images" + slash + "Run" + slash + "Fail.png";
        failImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < runningImg.length; i++) {
            addr = slash + "Images" + slash + "Run" + slash + "Run" + i + ".png";
            runningImg[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        imageView.setImage(baseImg);
        showNeuralNet();
    }

    private void showNeuralNet() {
        ArrayList<String> nameList = MainController.getNeuralNetSetsName();
        neuralListView.setItems(FXCollections.observableArrayList(nameList));
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        if (!neuralListView.getSelectionModel().isEmpty()) {
            int idx = neuralListView.getSelectionModel().getSelectedIndex();
            ArrayList<String> orderList = MainController.getAndSetNeuralNetSetsList(null).get(idx).getOrders();
            Platform.runLater(() -> {
                stockListView.setItems(FXCollections.observableArrayList(orderList));
            });
        } else {
            Platform.runLater(() -> {
                stockListView.setItems(null);
            });
        }
    }





}
