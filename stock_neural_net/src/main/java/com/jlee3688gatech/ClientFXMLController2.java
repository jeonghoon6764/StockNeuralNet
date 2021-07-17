package com.jlee3688gatech;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ClientFXMLController2 {

    @FXML
    private TextField learningRateTextField;
    @FXML
    private TextField minErrorTextField;
    @FXML
    private TextField maxIterTextField;
    @FXML
    private Button finButton;
    @FXML
    private Button requestMoreThreadButton;
    @FXML
    private Button requestRemoveThreadButton;
    @FXML
    private ListView statusListView;
    @FXML
    private ListView joinedComputerListView;


    private Client client;
    private Double learningRate;
    private Double minError;
    private Integer maxIteration;
    private boolean successConnect;

    @FXML
    private void initialize() {
        successConnect = false;
        InitializeClass initializeClass = new InitializeClass();
        initializeClass.start();
    }

    public class InitializeTimerClass extends Thread {


        InitializeClass initializeClass;
        
        public InitializeTimerClass (InitializeClass initializeClass) {
            this.initializeClass = initializeClass;
        }

        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {}

            if (!successConnect) {
                client.closeAll();
            }

            Platform.runLater(() -> {
                finButton.setDisable(false);
            });
        }
    }

    public class InitializeClass extends Thread {

        boolean finish;

        public void run() {
            InitializeTimerClass initializeTimerClass = new InitializeTimerClass(this);
            initializeTimerClass.start();

            try {
                successConnect = client.requestJoin();
                learningRate = client.requestLearningRate();
                if (learningRate == null) {
                    Platform.runLater(() -> {
                        learningRateTextField.setText("N/A");
                        maxIterTextField.setText("N/A");
                        minErrorTextField.setText("N/A");
                    });
                    while(learningRate != null) {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {}
                        learningRate = client.requestLearningRate();
                    }
                }
                String learningRateStr = Double.toString(learningRate);
                Platform.runLater(() -> {
                    learningRateTextField.setText(learningRateStr);
                });
                minError = client.requestMinimumError();
                if (minError == null) {
                    Platform.runLater(() -> {
                        maxIterTextField.setText("N/A");
                        minErrorTextField.setText("N/A");
                    });
                    while(minError != null) {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {}
                        minError = client.requestMinimumError();
                    }
                }
                String minErrorString = Double.toString(minError);
                Platform.runLater(() -> {
                    minErrorTextField.setText(minErrorString);
                });
                if (maxIteration == null) {
                    Platform.runLater(() -> {
                        maxIterTextField.setText("N/A");
                    });
                    while(maxIteration != null) {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {}
                        maxIteration = client.requestMaxIteration();
                    }
                }
                String maxIterationString = Integer.toString(maxIteration);
                Platform.runLater(() -> {
                    maxIterTextField.setText(maxIterationString);
                    requestMoreThreadButton.setDisable(false);
                    requestRemoveThreadButton.setDisable(false);
                });
            } catch (IOException e) {}
        }
    }

    public class renewListViews extends Thread {

        public void run() {
            boolean runThread = true;
            while(runThread) {
                ArrayList<String> statusList = null;
                ArrayList<String> joinedList = null;
                try {
                    statusList = client.requestStatusList();
                    joinedList = client.requestJoinedComputerList();
                } catch (IOException e) {}
                if (statusList == null || joinedList == null) {
                    runThread = false;
                    Platform.runLater(() -> {
                        finButton.setDisable(false);
                    });
                    break;
                }
                ArrayList<String> fnlStatusList = statusList;
                ArrayList<String> fnlJoinedList = joinedList;
                Platform.runLater(() -> {
                    statusListView.setItems(FXCollections.observableArrayList(fnlStatusList));
                    joinedComputerListView.setItems(FXCollections.observableArrayList(fnlJoinedList));
                });

                try {
                    Thread.sleep(5000);
                } catch (Exception e) {}
            }
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
}
