package com.jlee3688gatech;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerFXMLController2 {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portNumTextField;
    
    private NeuralNetSet neuralNetSet;
    private ArrayList<Learning> learningSet;
    private Server server;
    private String localAddr;
    private int portNum;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            ipTextField.setText(localAddr);
            portNumTextField.setText(Integer.toString(portNum));
        });
    }

    public void setNeuralNetSet(NeuralNetSet neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }

    public void setLearningSet(ArrayList<Learning> learningSet) {
        this.learningSet = learningSet;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }
}
