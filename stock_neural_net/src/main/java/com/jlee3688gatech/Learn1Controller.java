package com.jlee3688gatech;
import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ext.DOMDeserializer.NodeDeserializer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Learn1Controller {
    
    @FXML
    private ListView<String> neuralNetSetListView;
    @FXML
    private ListView<Label> stocksListView;
    @FXML
    private TextArea informationTextArea;
    @FXML
    private Button addButton;
    @FXML
    private ImageView neuralNetAnimation;
    @FXML
    private Button nextButton;
    
    private Image[] neuralNetImages;

    private String slash;
    private ArrayList<Stage> addStages;
    private boolean runThread;


    @FXML
    private void initialize() {

        runThread = true;
        this.slash = UtilMethods.slash;
        addButton.setDisable(true);
        nextButton.setDisable(true);

        neuralNetImages = new Image[8];
        for (int i = 1; i <= 8; i++) {
            String addr = slash + "Images" + slash + "NeuralNetAnimation" + slash + "NN0" + i + ".png";
            neuralNetImages[i - 1] = new Image(getClass().getResource(addr).toExternalForm());
        }

        ChangeNeuralNetImgClass thread0 = new ChangeNeuralNetImgClass();
        thread0.start();

        addStages = new ArrayList<>();
        this.slash = UtilMethods.slash;
        showNeuralNet();

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

    private void showNeuralNet() {
        ArrayList<String> neuralNetNameList = new ArrayList<>();
        ArrayList<NeuralNetSet> neuralNetSetList = MainController.getAndSetNeuralNetSetsList(null);
        for (int i = 0; i < neuralNetSetList.size(); i++) {
            neuralNetNameList.add(neuralNetSetList.get(i).getName());
        }
        Platform.runLater(() -> {
            neuralNetSetListView.setItems(FXCollections.observableArrayList(neuralNetNameList));
        });
    }

    public void showRequiredStocks() {
        if (neuralNetSetListView.getSelectionModel().isEmpty()) {
            return;
        }
        int idx = neuralNetSetListView.getSelectionModel().getSelectedIndex();
        NeuralNetSet target = MainController.getAndSetNeuralNetSetsList(null).get(idx);
        ArrayList<StockDatas> stocks = MainController.getAndSetStockDatasList(null);

        ArrayList<String> req = target.getOrders();
        ArrayList<String> existStock = new ArrayList<>();
        
        for (int i = 0; i < stocks.size(); i++) {
            existStock.add(stocks.get(i).getTicker());
        }

        ArrayList<Label> showList = new ArrayList<>();
        boolean needMore = false;
        for (int i = 0; i < req.size(); i++) {
            Label label = new Label();
            String temp = req.get(i) + " :: ";
            if (existStock.contains(req.get(i))) {
                temp += "READY";
                label.setDisable(false);
            } else {
                temp += "NEED";
                needMore = true;
                label.setDisable(true);
            }
            label.setText(temp);
            showList.add(label);
        }
        boolean fnlNeedMore = needMore;
        Platform.runLater(() -> {
            stocksListView.setItems(FXCollections.observableArrayList(showList));
            if (fnlNeedMore) {
                addButton.setDisable(false);
                nextButton.setDisable(true);
            } else {
                addButton.setDisable(true);
                nextButton.setDisable(false);
                clearAddStages();
            }
        });
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        stocksListView.getSelectionModel().clearSelection();
        if (!neuralNetSetListView.getSelectionModel().isEmpty()) {
            int idx = neuralNetSetListView.getSelectionModel().getSelectedIndex();
            informationTextArea.setText(MainController.getAndSetNeuralNetSetsList(null).get(idx).getInfoString());
            showRequiredStocks();
        } else {
            informationTextArea.setText("");
        }
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
        addStages.add(stage);
        StockAddController controller = loader.<StockAddController>getController();
        controller.setLearn1Controller(this);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickBack(ActionEvent actionEvent) throws IOException {
        clearAddStages();
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    private void clearAddStages() {
        for (int i = 0; i < addStages.size(); i++) {
            if (addStages.get(i) != null) {
                try {
                    addStages.get(i).close();
                } catch (Exception e) {}
            }
        }
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickNext(ActionEvent actionEvent) throws IOException {

        NeuralNetSet selectedNeuralNetSet = MainController.getAndSetNeuralNetSetsList(null).get(neuralNetSetListView.getSelectionModel().getSelectedIndex());
        ArrayList<StockDatas> stockDatasList = new ArrayList<>();

        ArrayList<StockDatas> mainStockList = MainController.getAndSetStockDatasList(null);

        for (int i = 0; i < selectedNeuralNetSet.getOrders().size(); i++) {
            for (int j = 0; j < mainStockList.size(); j++) {
                if (selectedNeuralNetSet.getOrders().get(i).equals(mainStockList.get(j).getTicker())) {
                    stockDatasList.add(mainStockList.get(j));
                    break;
                }
            }
        }

        for (int i = 0; i < selectedNeuralNetSet.getOrders().size(); i++) {
            System.out.println("//////////////////////////////////////////////");
            System.out.println(selectedNeuralNetSet.getOrders().get(i));
            System.out.println(stockDatasList.get(i).getTicker());
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "Learn2.fxml"));
        Parent root = loader.load();
        Learn2Controller controller = loader.<Learn2Controller>getController();
        controller.setNeuralNetSet(selectedNeuralNetSet);
        controller.setStockDatasList(stockDatasList);
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

}
