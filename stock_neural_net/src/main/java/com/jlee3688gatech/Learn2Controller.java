package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class Learn2Controller {

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

    private NeuralNetSet neuralNetSet;
    private ArrayList<StockDatas> stockDatasList;
    private ArrayList<Learning> learningList;
    
    @FXML
    private void initialize() {
        learningList = new ArrayList<>();
        showListView();
        Platform.runLater(() -> {
            showTextFields();
        });
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

    public void userClickGetExampleButton (ActionEvent actionEvent) throws IOException {

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
