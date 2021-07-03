package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Node;

public class SaveController {
    @FXML
    private ListView listView;
    @FXML
    private ChoiceBox choiceBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button makeFolderButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField textField;

    private SaveAndLoad saveAndLoad;

    private int currentSelected;
    private int doubleClickSpeed;
    private Pane mainPane;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        currentSelected = -1;
        doubleClickSpeed = UtilMethods.doubleClickSpeed;
        removeButton.setDisable(true);
        makeFolderButton.setDisable(true);
        saveButton.setDisable(true);
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
        });
    }

    public void userTypeTextField(KeyEvent keyEvent) throws IOException {
        if (textField.getText().length() != 0) {
            Platform.runLater(() -> {
                makeFolderButton.setDisable(false);
                saveButton.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                makeFolderButton.setDisable(true);
                saveButton.setDisable(true);
            });
        }
    }

    
    public void userClickedListView(MouseEvent mouseEvent) throws IOException {
        DoubleClickDetector doubleClickDetector = new DoubleClickDetector(listView.getSelectionModel().getSelectedIndex());
        doubleClickDetector.start();

        if (listView.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                removeButton.setDisable(true);
            });
        } else {
            Platform.runLater(() -> {
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
        });
    }

    public void userClickMakeFolder(ActionEvent actionEvent) throws IOException {
        String directoryName = textField.getText();
        int numOfFile = 0;
        String tempDirectoryName = directoryName;

        while (saveAndLoad.getFileNamesInSaveDirectory().contains(tempDirectoryName)) {
            numOfFile++;
            tempDirectoryName = directoryName + numOfFile;
        }

        directoryName = tempDirectoryName;

        saveAndLoad.makeDirectory(directoryName);
        showListView();
        showCurrentLocation();

        final String fnlDirectoryName = directoryName;

        Platform.runLater(() -> {
            int directoryIdx = 0;
            for (int i = 0; i < saveAndLoad.getFileNamesInSaveDirectory().size(); i++) {
                if (saveAndLoad.getFileNamesInSaveDirectory().get(i).equals(fnlDirectoryName)) {
                    directoryIdx = i;
                    break;
                }
            }
            listView.getSelectionModel().select(directoryIdx);
            textField.setText("");
            makeFolderButton.setDisable(true);
            saveButton.setDisable(true);
        });
    }

    public class SaveFile extends Thread {

        public void run() {
            SaveFileStructure saveFileStructure = new SaveFileStructure(MainController.getAndSetNeuralNetSetsList(null), MainController.getAndSetStockDatasList(null));

            String fileName = textField.getText();
            int numOfFile = 0;
            String tempFileName = fileName;
    
            while (saveAndLoad.getFileNamesInSaveDirectory().contains(tempFileName)) {
                numOfFile++;
                tempFileName = fileName + numOfFile;
            }
    
            fileName = tempFileName;
            saveAndLoad.saveFile(fileName + ".ser", saveFileStructure);
        }
    }

    /**
     * ActionEvent method. when user click cancel button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickedSave(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.close();
        mainPane.setDisable(false);
        SaveFile saveFile = new SaveFile();
        saveFile.start();
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
