package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;

public class MainScreenController {

    private static boolean runThread;
    private String slash;

    @FXML
    private ImageView neuralNetAnimation;

    @FXML
    private Image[] neuralNetImages;

    @FXML
    private ListView<String> neuralNetSetListView;

    @FXML
    private ListView<String> stocksListView;

    @FXML
    private TextArea informationTextArea;
    
    @FXML
    private Pane mainPane;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        runThread = true;
        this.slash = UtilMethods.slash;

        neuralNetImages = new Image[8];
        for (int i = 1; i <= 8; i++) {
            String addr = UtilMethods.perOSStartAddress + "Images" + slash + "NeuralNetAnimation" + slash + "NN0" + i + ".png";
            neuralNetImages[i - 1] = new Image(getClass().getResource(addr).toExternalForm());
        }
        ChangeNeuralNetImgClass thread0 = new ChangeNeuralNetImgClass();
        thread0.start();

        showNeuralNet();
        showStocksList();
    }

    private void showNeuralNet() {
        ArrayList<String> nameList = MainController.getNeuralNetSetsName();
        neuralNetSetListView.setItems(FXCollections.observableArrayList(nameList));
    }

    private void showStocksList() {
        ArrayList<String> nameAndTicker = MainController.getStockDatasNameAndTicker();
        stocksListView.setItems(FXCollections.observableArrayList(nameAndTicker));
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        stocksListView.getSelectionModel().clearSelection();
        if (!neuralNetSetListView.getSelectionModel().isEmpty()) {
            int idx = neuralNetSetListView.getSelectionModel().getSelectedIndex();
            informationTextArea.setText(MainController.getAndSetNeuralNetSetsList(null).get(idx).getInfoString());
        } else {
            informationTextArea.setText("");
        }
    }

    public void stocksMouseClicked(MouseEvent mouseEvent) throws IOException{
        neuralNetSetListView.getSelectionModel().clearSelection();
        if (!stocksListView.getSelectionModel().isEmpty()) {
            int idx = stocksListView.getSelectionModel().getSelectedIndex();
            informationTextArea.setText(MainController.getAndSetStockDatasList(null).get(idx).getInfoString());
        } else {
            informationTextArea.setText("");
        }
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickaddNeuralNetButton(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "NeuralNetSetAdd1.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickUpdateStocksButton(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "StockUpdate.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickaddStocksButton(ActionEvent actionEvent) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "StockAdd.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 320, 160);
        Stage stage = new Stage();
        StockAddController controller = loader.<StockAddController>getController();
        controller.setStockListView(stocksListView);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * ActionEvent method. when user click remove stock button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickRemoveNeuralNetButton(ActionEvent actionEvent) throws IOException {

        if (!neuralNetSetListView.getSelectionModel().isEmpty()) {
            int idx = neuralNetSetListView.getSelectionModel().getSelectedIndex();
            MainController.getAndSetNeuralNetSetsList(null).remove(idx);
            neuralNetSetListView.setItems(FXCollections.observableArrayList(MainController.getNeuralNetSetsName()));
            informationTextArea.setText("");
        }
    }

    /**
     * ActionEvent method. when user click remove stock button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickRemoveStocksButton(ActionEvent actionEvent) throws IOException {

        if (!stocksListView.getSelectionModel().isEmpty()) {
            int idx = stocksListView.getSelectionModel().getSelectedIndex();
            MainController.getAndSetStockDatasList(null).remove(idx);
            stocksListView.setItems(FXCollections.observableArrayList(MainController.getStockDatasNameAndTicker()));
            informationTextArea.setText("");
        }
    }

    /**
     * Method for make animation of Neural Net picture.
     */
    private class ChangeNeuralNetImgClass extends Thread {
        @FXML
        public void run() {
            try {
                boolean isInc = true;
                int i = 0;
                while(getAndSetRunThreadVar(null)) {
                    neuralNetAnimation.setImage(neuralNetImages[i]);
                    if (isInc) {
                        i++;
                    } else {
                        i--;
                    }
                    if (i <= 0 || i >= 7) {
                        isInc = !isInc;
                    }
                    Thread.sleep(100);
                }
            }catch(Exception e) {
                getAndSetRunThreadVar(false);
            }
        }
    }

    /**
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    public synchronized boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
    }


    /**
     * ActionEvent method. when user click remove stock button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void menuFileNew(ActionEvent actionEvent) throws IOException {

        MainController.getAndSetNeuralNetSetsList(new ArrayList<>());
        MainController.getAndSetStockDatasList(new ArrayList<>());

        Platform.runLater(() -> {
            showNeuralNet();
            showStocksList();
            informationTextArea.setText("");
        });
    }

    /**
     * ActionEvent method. when user click remove stock button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void menuFileLoad(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Load.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = new Stage();
        LoadController controller = loader.<LoadController>getController();
        controller.setMainPane(mainPane);
        controller.setMainScreenController(this);
        mainPane.setDisable(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent e) {
                mainPane.setDisable(false);
            }
        });
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void menuFileSave(ActionEvent actionEvent) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Save.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = new Stage();
        SaveController controller = loader.<SaveController>getController();
        controller.setMainPane(mainPane);
        mainPane.setDisable(true);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent e) {
                mainPane.setDisable(false);
            }
        });
    }


    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void menuEditAddaNote(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "EditNeuralNetSet.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    public void menuLearnNeuralNetSet(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Learn1.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    public void menuRunRun (ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Run.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    
}
