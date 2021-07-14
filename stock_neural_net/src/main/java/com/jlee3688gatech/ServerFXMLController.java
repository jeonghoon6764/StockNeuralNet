package com.jlee3688gatech;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ServerFXMLController {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portNumberTextField;
    @FXML
    private Button startButton;

    
    @FXML
    private void initialize() {
        String localAddr = new String();
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

    public void userTypedPortNumTextField(KeyEvent keyEvent) throws IOException{
        String input = portNumberTextField.getText();
        if (checkValidInteger(input)) {
            if (checkValidPortNumber(Integer.parseInt(input))) {
                Platform.runLater(() -> {
                    startButton.setDisable(false);
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
