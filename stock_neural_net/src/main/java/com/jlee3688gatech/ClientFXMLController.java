package com.jlee3688gatech;
import java.io.IOException;

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

public class ClientFXMLController {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portNumTextField;
    @FXML
    private TextField nickNameTextField;
    @FXML
    private Button joinButton;
    @FXML
    private Button cancelButton;

    private int portNum;
    private String slash;

    @FXML
    private void initialize() {
        this.portNum = -1;
        this.slash = UtilMethods.slash;
        
    }

    public void userTypedPortNumTextField(KeyEvent keyEvent) throws IOException{
        String input = portNumTextField.getText();
        if (checkValidInteger(input)) {
            if (checkValidPortNumber(Integer.parseInt(input)) && ipTextField.getText().length() > 0
            && nickNameTextField.getText().length() > 0) {
                Platform.runLater(() -> {
                    joinButton.setDisable(false);
                    portNum = Integer.parseInt(input);
                });
                return;
            }
        }
        Platform.runLater(() -> {
            joinButton.setDisable(true);
        });
    }

    public void userClickJoinButton(ActionEvent actionEvent) throws IOException {
        Client client = new Client(ipTextField.getText(), Integer.parseInt(portNumTextField.getText()), nickNameTextField.getText());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "NetworkClient2.fxml"));
        Parent root = loader.load();
        ClientFXMLController2 controller = loader.<ClientFXMLController2>getController();
        controller.setClient(client);
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
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
