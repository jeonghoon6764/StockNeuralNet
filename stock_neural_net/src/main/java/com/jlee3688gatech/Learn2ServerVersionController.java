package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Learn2ServerVersionController {

    @FXML
    private ListView stocksListView;
    @FXML
    private TextField overlapFromTextField;
    @FXML
    private TextField overlapToTextField;
    @FXML
    private TextField numOfDatasTextField;
    @FXML
    private TextField trueExistanceRateTextField;
    @FXML
    private Button getExampleButton;
    @FXML
    private Button proceedButton;
    @FXML
    private Pane mainPane;
    @FXML
    private ImageView calculatorImageView;
    

    private NeuralNetSet neuralNetSet;
    private ArrayList<StockDatas> stockDatasList;
    private ArrayList<Learning> learningList;
    private String slash;
    private Image[] calculatorImages;
    private boolean runThread;
    
    
    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        calculatorImages = new Image[9];
        for (int i = 0; i < calculatorImages.length; i++) {
            String addr = UtilMethods.perOSStartAddress + "Images" + slash + "Calculator" + slash + "Calculator" + i + ".png";
            calculatorImages[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        learningList = new ArrayList<>();
        showListView();
        Platform.runLater(() -> {
            showTextFields();
        });
    }

    public void userClickProceedButton(ActionEvent actionEvent) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "NetworkServer1.fxml"));
        Parent root = loader.load();
        ServerFXMLController controller = loader.<ServerFXMLController>getController();
        controller.setLearningList(learningList);
        controller.setNeuralNetSet(neuralNetSet);
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * thread for change calculator image.
     */
    public class CalculatorImageChangeClass extends Thread {
        @FXML
        public void run() {
            Random rd = new Random();
            try {
                while(getAndSetRunThreadVar(null)) {
                    Platform.runLater(() -> {
                        calculatorImageView.setImage(calculatorImages[rd.nextInt(9)]);
                    });
                    Thread.sleep(100);                    
                }
            } catch (Exception e) {
                getAndSetRunThreadVar(false);
            }
            calculatorImageView.setImage(null);
        }
    }

    /**
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    private synchronized boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
    }

    public void setNeuralNetSet(NeuralNetSet neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }

    public void setStockDatasList(ArrayList<StockDatas> stockDatasList) {
        this.stockDatasList = stockDatasList;
    }

    private void showListView() {
        ArrayList<String> stockInfoList = new ArrayList<>();
        Platform.runLater(() -> {
            for (int i = 0; i < stockDatasList.size(); i++) {
                String str = new String();
                StockDatas stockDatas = stockDatasList.get(i);
                str += stockDatas.getName();
                str += " : " + stockDatas.getTicker();
                str += "    " + UtilMethods.CalendarToString(stockDatas.getFromDate());
                str += " ~ " + UtilMethods.CalendarToString(stockDatas.getToDate());
                stockInfoList.add(str);
            }
        });

        Platform.runLater(() -> {
            stocksListView.setItems(FXCollections.observableArrayList(stockInfoList));
        });
    }

    private void showTextFields() {
        Calendar maxFrom = stockDatasList.get(0).getFromDate();
        Calendar minTo = stockDatasList.get(0).getToDate();

        for (int i = 1; i < stockDatasList.size(); i++) {
            if (maxFrom.getTimeInMillis() < stockDatasList.get(i).getFromDate().getTimeInMillis()) {
                maxFrom = stockDatasList.get(i).getFromDate();
            }
            if (minTo.getTimeInMillis() > stockDatasList.get(i).getToDate().getTimeInMillis()) {
                minTo = stockDatasList.get(i).getToDate();
            }
        }

        if (minTo.getTimeInMillis() < maxFrom.getTimeInMillis()) {
            Platform.runLater(() -> {
                overlapFromTextField.setText("-");
                overlapToTextField.setText("-");
                getExampleButton.setDisable(true);
            });
        } else {
            Calendar fnlMaxFrom = maxFrom;
            Calendar fnlMinTo = minTo;
            Platform.runLater(() -> {
                overlapFromTextField.setText(UtilMethods.CalendarToString(fnlMaxFrom));
                overlapToTextField.setText(UtilMethods.CalendarToString(fnlMinTo));
                getExampleButton.setDisable(false);
            });
        }
    }

    public class GetExampleClass extends Thread{

        public void run() {
            getAndSetRunThreadVar(true);
            CalculatorImageChangeClass calculatorImageChangeClass = new CalculatorImageChangeClass();
            calculatorImageChangeClass.start();
            
            int totalNumOfExamples = 0;
            double truthRate = 0.0;

            for (int i = 0; i < neuralNetSet.getNeuralNets().size(); i++) {
                Learning learning = new Learning(neuralNetSet.getNeuralNets().get(i), stockDatasList);
                int targetNumOfInc = neuralNetSet.getMinIncreaseDate();
                int targetDataCountFrom = neuralNetSet.getNumOfInputDate();
                int targetDataCountTo = neuralNetSet.getNumOfInputDate() + neuralNetSet.getNumOfDateOutput();
                double incRate = neuralNetSet.getRateOfTotalIncrease();
                ArrayList<String> inputDataTypes = neuralNetSet.getReferenceList();
                int numOfDataFromCounter = neuralNetSet.getNumOfInputDate();
                truthRate += learning.makeExamples(targetNumOfInc, "adjClosed", targetDataCountFrom, targetDataCountTo, incRate, inputDataTypes, numOfDataFromCounter);
                learningList.add(learning);
                totalNumOfExamples += learning.getNumOfExample();
            }

            truthRate /= neuralNetSet.getNeuralNets().size();


            int fnlTotalNumOfExamples = totalNumOfExamples;
            double fnlTruthRate = truthRate;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
            getAndSetRunThreadVar(false);

            if (fnlTotalNumOfExamples > 0 && fnlTruthRate > 0) {
                Platform.runLater(() -> {
                    proceedButton.setDisable(false);
                });
            } else {
                Platform.runLater(() -> {
                    proceedButton.setDisable(true);
                });
            }
            Platform.runLater(() -> {
                numOfDatasTextField.setText(Integer.toString(fnlTotalNumOfExamples));
                trueExistanceRateTextField.setText(Double.toString(fnlTruthRate));
            });
        }
    }

    public void userClickGetExampleButton (ActionEvent actionEvent) throws IOException {
        GetExampleClass getExampleClass = new GetExampleClass();
        getExampleClass.start();
    }

    public void userClickBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Learn1.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

}

