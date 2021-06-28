package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainScreenController {

    private static boolean runThread;
    private String slash;

    @FXML
    private ImageView neuralNetAnimation;

    @FXML
    private Image[] neuralNetImages;

    @FXML
    private ListView neuralNetSetListView;

    @FXML
    private ListView stocksListView;

    @FXML
    private TextArea informationTextArea;

    @FXML
    private Button clickAddNeuralNetButton;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        runThread = true;
        this.slash = UtilMethods.slash;

        neuralNetImages = new Image[8];
        for (int i = 1; i <= 8; i++) {
            String addr = slash + "Images" + slash + "NeuralNetAnimation" + slash + "NN0" + i + ".png";
            neuralNetImages[i - 1] = new Image(getClass().getResource(addr).toExternalForm());
        }
        ChangeNeuralNetImgClass thread0 = new ChangeNeuralNetImgClass();
        thread0.start();

        showNeuralNet();
        showStocksList();
    }

    private void showNeuralNet() {
        ArrayList<String> nameList = MainController.getNeuralNetSetsName();
        neuralNetSetListView.setItems(FXCollections.observableArrayList(nameList));
    }

    private void showStocksList() {
        ArrayList<String> nameAndTicker = MainController.getStockDatasNameAndTicker();
        stocksListView.setItems(FXCollections.observableArrayList(nameAndTicker));
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        stocksListView.getSelectionModel().clearSelection();
        if (!neuralNetSetListView.getSelectionModel().isEmpty()) {
            int idx = neuralNetSetListView.getSelectionModel().getSelectedIndex();
            informationTextArea.setText(MainController.neuralNetSetsList.get(idx).getInfoString());
        } else {
            informationTextArea.setText("");
        }
    }

    public void stocksMouseClicked(MouseEvent mouseEvent) throws IOException{
        neuralNetSetListView.getSelectionModel().clearSelection();
        if (!stocksListView.getSelectionModel().isEmpty()) {
            int idx = stocksListView.getSelectionModel().getSelectedIndex();
            informationTextArea.setText(MainController.neuralNetSetsList.get(idx).getInfoString());
        } else {
            informationTextArea.setText("");
        }
    }

    

    /**
     * Method for make animation of Neural Net picture.
     */
    private class ChangeNeuralNetImgClass extends Thread {
        @FXML
        public void run() {
            try {
                boolean isInc = true;
                int i = 0;
                while(getAndSetRunThreadVar(null)) {
                    neuralNetAnimation.setImage(neuralNetImages[i]);
                    if (isInc) {
                        i++;
                    } else {
                        i--;
                    }
                    if (i <= 0 || i >= 7) {
                        isInc = !isInc;
                    }
                    Thread.sleep(100);
                }
            }catch(Exception e) {
                getAndSetRunThreadVar(false);
            }
        }
    }

    /**
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    private synchronized static boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
    }
    
}
