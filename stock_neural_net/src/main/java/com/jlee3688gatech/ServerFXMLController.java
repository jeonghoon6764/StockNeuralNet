package com.jlee3688gatech;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ServerFXMLController {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portNumberTextField;
    @FXML
    private Button startButton;

    private int portNum;
    private String slash;

    private ArrayList<Learning> learningList;
    private NeuralNetSet neuralNetSet;
    private String localAddr;
    
    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        this.portNum = -1;
        localAddr = new String();
        try {
            localAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            localAddr = "unknown/error";
        }
        String fnlLocalAddr = localAddr;
        
        Platform.runLater(() -> {
            ipTextField.setText(fnlLocalAddr);
            startButton.setDisable(true);
        });
    }

    public void setLearningList(ArrayList<Learning> learningList) {
        this.learningList = learningList;
    }

    public void setNeuralNetSet(NeuralNetSet neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }

    public void userClickStartButton(ActionEvent actionEvent) throws IOException {
        Server server = new Server(portNum, 100);
        if(server.getInitFail()) {
            Platform.runLater(() -> {
                portNumberTextField.clear();
                startButton.setDisable(true);
            });
            return;
        } else {
            server.start();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "NetworkServer2.fxml"));
            Parent root = loader.load();
            ServerFXMLController2 controller = loader.<ServerFXMLController2>getController();
            controller.setNeuralNetSet(this.neuralNetSet);
            controller.setLearningSet(this.learningList);
            controller.setServer(server);
            controller.setLocalAddr(this.localAddr);
            controller.setPortNum(portNum);
            Scene scene = new Scene(root, 600, 400);
            Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
            stage.setResizable(false);
            stage.setScene(scene);
        }
    }

    public void userClickBackButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    public void userTypedPortNumTextField(KeyEvent keyEvent) throws IOException{
        String input = portNumberTextField.getText();
        if (checkValidInteger(input)) {
            if (checkValidPortNumber(Integer.parseInt(input))) {
                Platform.runLater(() -> {
                    startButton.setDisable(false);
                    portNum = Integer.parseInt(input);
                });
                return;
            }
        }
        Platform.runLater(() -> {
            startButton.setDisable(true);
        });
    }

    private boolean checkValidPortNumber(int portNum) {
        if (portNum >= 1024 && portNum <= 49151) {
            return true;
        }
        return false;
    }

    private boolean checkValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
