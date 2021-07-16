package com.jlee3688gatech;
import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    private Client client;
    private Double learningRate;
    private Double minError;
    private Integer maxIteration;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            try {
                client.requestJoin();
            } catch (IOException e) {
                learningRateTextField.setText("ERROR");
                minErrorTextField.setText("ERROR");
                maxIterTextField.setText("ERROR");
            }
        });
        
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

            if (learningRate == null || minError == null || maxIterTextField == null) {
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

            try {
                client.requestJoin();
                learningRate = client.requestLearningRate();
                Platform.runLater(() -> {
                    learningRateTextField.setText(Double.toString(learningRate));
                });
                minError = client.requestMinimumError();
                Platform.runLater(() -> {
                    minErrorTextField.setText(Double.toString(minError));
                });
                maxIteration = client.requestMaxIteration();
                Platform.runLater(() -> {
                    maxIterTextField.setText(Integer.toString(maxIteration));
                });
            } catch (IOException e) {}
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
}
