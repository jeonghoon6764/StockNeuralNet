package com.jlee3688gatech;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;


public class NeuralNetSetAdd1Controller {

    @FXML
    private ListView<String> indicatorsListView;
    @FXML
    private ListView<String> targetsListView;
    @FXML
    private TextField neuralSetNameTextField;
    @FXML
    private TextField numOfInputTextField;
    @FXML
    private TextField numOfHiddenLayerTextField;
    @FXML
    private CheckBox adjClosedChkBox;
    @FXML
    private CheckBox highChkBox;
    @FXML
    private CheckBox openChkBox;
    @FXML
    private CheckBox closedChkBox;
    @FXML
    private CheckBox lowChkBox;
    @FXML
    private CheckBox volumeChkBox;
    @FXML
    private Button continueButton;

    private ArrayList<String> nameAndTicker;
    private String slash;


    /**
     * FXML initializer
     */
    @FXML
    private void initialize() {
        continueButton.setDisable(true);
        this.slash = UtilMethods.slash;
        nameAndTicker = MainController.getStockDatasNameAndTicker();
        showIndicatorsInListView();
        indicatorsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        targetsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * method for showing indicators.
     */
    private void showIndicatorsInListView() {
        indicatorsListView.setItems(FXCollections.observableArrayList(nameAndTicker));
    }

    /**
     * MouseEvent method. when user click proceed button
     * @param mouseEvent Mouse event
     * @throws IOException IO Exceprion
     */
    public void clickMouseInIndicatorsListView(MouseEvent mouseEvent) throws IOException {
        checkContinueButtonWithMouseEvent(mouseEvent);
        ObservableList<Integer> selectedList = indicatorsListView.getSelectionModel().getSelectedIndices();
        ArrayList<String> copy = new ArrayList<>();
        for (int i = 0; i < selectedList.size(); i++) {
            copy.add(nameAndTicker.get(selectedList.get(i)));
        }

        Platform.runLater(() -> {
            targetsListView.setItems(FXCollections.observableArrayList(copy));
        });
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickBack(ActionEvent actionEvent) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickContinue(ActionEvent actionEvent) throws IOException {

        ObservableList<Integer> indicatorList = indicatorsListView.getSelectionModel().getSelectedIndices();
        ObservableList<Integer> targetList = targetsListView.getSelectionModel().getSelectedIndices();

        ArrayList<String> indicatorTickerList = new ArrayList<>();
        ArrayList<String> targetTickerList = new ArrayList<>();
        ArrayList<String> referenceList = new ArrayList<>();

        for (int i = 0; i < indicatorList.size(); i++) {
            indicatorTickerList.add(MainController.getAndSetStockDatasList(null).get(indicatorList.get(i)).getTicker());
        }
        for (int i = 0; i < targetList.size(); i++) {
            targetTickerList.add(indicatorTickerList.get(targetList.get(i)));
        }

        if (adjClosedChkBox.isSelected()) {
            referenceList.add("adjClosed");
        }
        if (closedChkBox.isSelected()) {
            referenceList.add("close");
        }
        if (highChkBox.isSelected()) {
            referenceList.add("high");
        }
        if (lowChkBox.isSelected()) {
            referenceList.add("low");
        }
        if (openChkBox.isSelected()) {
            referenceList.add("open");
        }
        if (volumeChkBox.isSelected()) {
            referenceList.add("volume");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "NeuralNetSetAdd2.fxml"));
        Parent root = loader.load();
        NeuralNetSetAdd2Controller controller = loader.<NeuralNetSetAdd2Controller>getController();
        controller.setIndicatorStocksList(indicatorTickerList);
        controller.setIndicatorStocksTargetList(targetTickerList);
        controller.setReferenceList(referenceList);
        controller.setNeuralNetName(neuralSetNameTextField.getText());
        controller.setNeuralNetNumOfInput(Integer.parseInt(numOfInputTextField.getText()));
        controller.setNeuralNetNumOfHiddenLayers(Integer.parseInt(numOfHiddenLayerTextField.getText()));
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * actionEvent method. when user type any button in the textField
     * @param actionEvent actionEvent
     * @throws IOException IO Exception
     */
    public void checkContinueButtonWithActionEvent(ActionEvent actionEvent) throws IOException {
        enableContinueButton();
    }

    /**
     * keyEvent method. when user type any button in the textField
     * @param keyEvent keyEvent
     * @throws IOException IO Exception
     */
    public void checkContinueButtonWithKeyEvent(KeyEvent keyEvent) throws IOException {
        enableContinueButton();
    }

    /**
     * mouseEvent method. when user type any button in the textField
     * @param mouseEvent mouseEvent
     * @throws IOException IO Exception
     */
    public void checkContinueButtonWithMouseEvent(MouseEvent mouseEvent) throws IOException {
        enableContinueButton();
    }

    /**
     * method for enabling continue button.
     */
    private void enableContinueButton() {
        if (checkListView() && checkCheckBox() && checkTextField()) {
            Platform.runLater(() -> {
                continueButton.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                continueButton.setDisable(true);
            });
        }
    }

    /**
     * Method for check LiistViews.
     * @return
     */
    private boolean checkListView() {
        if (indicatorsListView.getSelectionModel().getSelectedIndices().size() != 0
         && targetsListView.getSelectionModel().getSelectedIndices().size() != 0) {
             return true;
         }
         return false;
    }
    
    /**
     * Method for check checkbox.
     * @return
     */
    private boolean checkCheckBox() {
        if (adjClosedChkBox.isSelected() || closedChkBox.isSelected() || highChkBox.isSelected()
         || lowChkBox.isSelected() || openChkBox.isSelected() || volumeChkBox.isSelected()) {
            return true;
        }
        return false;
    }

    /**
     * method for checking TextFields has valid input. 
     * @return true if valid, false otherwise.
     */
    private boolean checkTextField() {
        if (!neuralSetNameTextField.getText().isEmpty() &&
         !numOfInputTextField.getText().isEmpty() && !numOfHiddenLayerTextField.getText().isEmpty()) {
            if (validNumberTextFieldChecker(numOfInputTextField.getText()) && validNumberTextFieldChecker(numOfHiddenLayerTextField.getText())) {
                return true;
            }
        }
        return false;
    }

    /**
     * method for valid String checker.
     * input should be a String which is represent a integer that bigger than 0.
     * @param str inputString
     * @return true if it is valid otherwise false
     */
    private boolean validNumberTextFieldChecker(String str) {
        int i = 0;
        try {
            i = Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        if (i <= 0) {
            return false;
        }
        return true;
    }


}
