package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class LoadController {
    @FXML
    private ListView listView;
    @FXML
    private ChoiceBox choiceBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button loadButton;

    private SaveAndLoad saveAndLoad;

    private int currentSelected;
    private int doubleClickSpeed;
    private Pane mainPane;
    private MainScreenController mainScreenController;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        currentSelected = -1;
        doubleClickSpeed = UtilMethods.doubleClickSpeed;
        removeButton.setDisable(true);
        loadButton.setDisable(true);
        this.doubleClickSpeed = UtilMethods.doubleClickSpeed;
        saveAndLoad = new SaveAndLoad();
        showListView();
        showCurrentLocation();
    }

    private void showListView() {
        ArrayList<String> fileNames = saveAndLoad.getFileNamesInSaveDirectory();
        Platform.runLater(() -> {
            listView.setItems(FXCollections.observableArrayList(fileNames));
        });
    }

    private void showCurrentLocation() {
        ArrayList<String> addressList = new ArrayList<>();
        String baseSaveFileAddress = saveAndLoad.getSaveFileAddr();
        addressList.add(baseSaveFileAddress);

        ArrayList<String> directoryPathNameList = saveAndLoad.getCurrAddrDirectories();
        String slash = UtilMethods.slash;

        String tempStr = baseSaveFileAddress;
        for (int i = 0; i < directoryPathNameList.size(); i++) {
            tempStr += slash + directoryPathNameList.get(i);
            addressList.add(tempStr);
        }

        Platform.runLater(() -> {
            choiceBox.setItems(FXCollections.observableArrayList(addressList));
            choiceBox.getSelectionModel().select(addressList.size() - 1);
        });
    }

    public synchronized int getAndSetCurrentSelected(Integer val) {
        if (val != null) {
            this.currentSelected = val;
        }
        return currentSelected;
    }

    public void setMainPane(Pane mainPane) {
        this.mainPane = mainPane;
    }

    public void setMainScreenController(MainScreenController mainScreenController) {
        this.mainScreenController = mainScreenController;
    }
    


    public class DoubleClickDetector extends Thread{

        private int idx;

        public DoubleClickDetector(int idx) {
            this.idx = idx;
        }

        public void run() {
            if (getAndSetCurrentSelected(null) != idx) {
                getAndSetCurrentSelected(idx);

                try {
                    Thread.sleep(doubleClickSpeed);
                } catch (Exception e) {}

                getAndSetCurrentSelected(-1);
            } else {
                if (idx < 0) {
                    return;
                }
                if (saveAndLoad.isDirectory(saveAndLoad.getFileNamesInSaveDirectory().get(idx))) {
                    saveAndLoad.addCurrAddr(saveAndLoad.getFileNamesInSaveDirectory().get(idx));
                    showListView();
                    showCurrentLocation();
                    Platform.runLater(() -> {
                        removeButton.setDisable(true);
                        loadButton.setDisable(true);
                    });
                }
            }
        }

    }

    public void userClickedRemoveButton(ActionEvent actionEvent) throws IOException {
        int removeIdx = listView.getSelectionModel().getSelectedIndex();
        saveAndLoad.removeFile(saveAndLoad.getFileNamesInSaveDirectory().get(removeIdx));
        showListView();
        showCurrentLocation();
        Platform.runLater(() -> {
            removeButton.setDisable(true);
            loadButton.setDisable(true);
        });
    }
    
    public void userClickedListView(MouseEvent mouseEvent) throws IOException {
        DoubleClickDetector doubleClickDetector = new DoubleClickDetector(listView.getSelectionModel().getSelectedIndex());
        doubleClickDetector.start();

        if (listView.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                removeButton.setDisable(true);
                loadButton.setDisable(true);
            });
        } else {
            Platform.runLater(() -> {
                if (!saveAndLoad.isDirectory(saveAndLoad.getFileNamesInSaveDirectory().get(listView.getSelectionModel().getSelectedIndex()))) {
                    loadButton.setDisable(false);
                }
                removeButton.setDisable(false);
                
            });
        }
    }

    public void userClickedMove(ActionEvent actionEvent) throws IOException {
        if (choiceBox.getSelectionModel().getSelectedIndex() == choiceBox.getItems().size() - 1) {
            return;
        }

        int outNum = (choiceBox.getItems().size() - 1) - choiceBox.getSelectionModel().getSelectedIndex();

        for (int i = 0; i < outNum; i++) {
            saveAndLoad.removeLastAddr();
        }
        showListView();
        showCurrentLocation();
        Platform.runLater(() -> {
            removeButton.setDisable(true);
            loadButton.setDisable(true);
        });
    }


    /**
     * ActionEvent method. when user click cancel button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickedLoad(ActionEvent actionEvent) throws IOException {

        SaveFileStructure loadFile = (SaveFileStructure)saveAndLoad.loadFile(
            saveAndLoad.getFileNamesInSaveDirectory().get(listView.getSelectionModel().getSelectedIndex()));

        MainController.getAndSetNeuralNetSetsList(loadFile.getNeuralNetSetList());
        MainController.getAndSetStockDatasList(loadFile.getStockDatasList());
        mainScreenController.getAndSetRunThreadVar(false);
        Stage thisStage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        thisStage.close();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + UtilMethods.slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage mainStage = (Stage)mainPane.getScene().getWindow();
        mainStage.setResizable(false);
        mainStage.setScene(scene);
    }

    

    /**
     * ActionEvent method. when user click cancel button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.close();
        mainPane.setDisable(false);
    }
}

