package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class RunController {
    @FXML
    ListView neuralListView;
    @FXML
    ListView resultListView;
    @FXML
    ListView stockListView;
    @FXML
    TextField dateEndToTextField;
    @FXML
    TextField periodStartTextField;
    @FXML
    TextField openingDateTextField;
    @FXML
    TextField numOfDaysIncTextField;
    @FXML
    TextField degreeOfIncStockTextField;
    @FXML
    ImageView imageView;
    @FXML
    Button backButton;
    @FXML
    Button getExampleButton;

    private Image baseImg;
    private Image[] downloadImg;
    private Image failImg;
    private Image[] runningImg;
    private String slash;
    private ArrayList<StockDatas> stockDatasList;

    @FXML
    private void initialize() {
        getExampleButton.setDisable(true);
        this.slash = UtilMethods.slash;
        downloadImg = new Image[3];
        runningImg = new Image[8];

        String addr = slash + "Images" + slash + "Run" + slash + "Base.png";
        baseImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < downloadImg.length; i++) {
            addr = slash + "Images" + slash + "Run" + slash + "Down" + i + ".png";
            downloadImg[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        addr = slash + "Images" + slash + "Run" + slash + "Fail.png";
        failImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < runningImg.length; i++) {
            addr = slash + "Images" + slash + "Run" + slash + "Run" + i + ".png";
            runningImg[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        imageView.setImage(baseImg);
        showNeuralNet();
    }

    public void userClickGetExampleButton(ActionEvent actionEvent) throws IOException {
        Platform.runLater(() -> {
            backButton.setDisable(true);
            getExampleButton.setDisable(true);
        });

        NeuralNetSet neuralNetSet = MainController.getAndSetNeuralNetSetsList(null).get(neuralListView.getSelectionModel().getSelectedIndex());
        String endDateStr = dateEndToTextField.getText();

        GetExampleClass getExampleClass = new GetExampleClass(neuralNetSet, endDateStr);
        getExampleClass.start();
    }

    public class GetExampleClass extends Thread {
        private NeuralNetSet neuralNetSet;
        private String endDateStr;

        public GetExampleClass (NeuralNetSet neuralNetSet, String endDateStr) {
            this.neuralNetSet = neuralNetSet;
            this.endDateStr = endDateStr;
        }

        public synchronized void run() {
            ArrayList<String> stockList = neuralNetSet.getOrders();
            int numOfInputDate = neuralNetSet.getNumOfInputDate();

            Calendar start = UtilMethods.CalendarMaker(endDateStr);
            Calendar end = UtilMethods.CalendarMaker(endDateStr);

            System.out.println(UtilMethods.CalendarToString(end));

            start.add(Calendar.DATE, (-1) * (numOfInputDate * 2 + 14));

            stockDatasList = new ArrayList<>();
            ArrayList<GetStockDatasClass> getStockDatasClassList = new ArrayList<>();
            
            for (int i = 0; i < stockList.size(); i++) {
                GetStockDatasClass getStockDatasClass = new GetStockDatasClass(stockList.get(i), start, end, this);
                getStockDatasClassList.add(getStockDatasClass);
                getStockDatasClass.start();
            }

            while(true) {
                try {
                    wait();
                } catch (InterruptedException e) {}
                boolean out = true;
                for (int i = 0; i < getStockDatasClassList.size(); i++) {
                    if (getStockDatasClassList.get(i).getAndSetRunning(null)) {
                        out = false;
                        break;
                    }
                }
                if (out) {
                    break;
                }
            }

            ArrayList<StockDatas> inputStockDatasOrderedList = new ArrayList<>();

            for (int i = 0; i < stockList.size(); i++) {
                for (int j = 0; j < stockDatasList.size(); j++) {
                    if (stockList.get(i).equals(stockDatasList.get(j).getTicker())) {
                        inputStockDatasOrderedList.add(stockDatasList.get(j));
                    }
                }
            }

            ExampleMaker exampleMaker = new ExampleMaker(inputStockDatasOrderedList);
            RecentInputData recentInputData = exampleMaker.getRecentInput(neuralNetSet.getReferenceList(), neuralNetSet.getNumOfInputDate());
            NeuralNetSetOutput neuralNetSetOutput = neuralNetSet.getRecentOutputData(recentInputData);

            
            for (int i = 0; i < neuralNetSetOutput.getSize(); i++) {
                System.out.println(neuralNetSetOutput.getStockName(i));
                System.out.println(neuralNetSetOutput.getTrueValue(i));
                System.out.println(neuralNetSetOutput.getFalseValue(i));
            }


        }
    }

    public class GetStockDatasClass extends Thread {

        private String stockTicker;
        private Calendar from;
        private Calendar to;
        private boolean loadFail;
        private boolean running;
        private GetExampleClass getExampleClass;

        public GetStockDatasClass(String stockTicker, Calendar from, Calendar to, GetExampleClass getExampleClass) {
            this.stockTicker = stockTicker;
            this.from = UtilMethods.CalendarMaker(UtilMethods.calendarToSimpleString(from));
            this.to = UtilMethods.CalendarMaker(UtilMethods.calendarToSimpleString(to));
            this.loadFail = false;
            this.running = false;
            this.getExampleClass = getExampleClass;
        }

        public void run() {
            getAndSetRunning(true);
            StockDatas stockDatas = null;
            try {
                System.out.println(UtilMethods.CalendarToString(to));
                stockDatas = new StockDatas("AUTO_GENERATED", stockTicker, from, to);
            } catch (Exception e) {
                loadFail = true;
            }
            if (loadFail) {
                return;
            }
            addStockInList(stockDatas);
            getAndSetRunning(false);
            synchronized(getExampleClass) {
                getExampleClass.notifyAll();
            }
        }

        public boolean getLoadFail() {
            return this.loadFail;
        }

        public synchronized boolean getAndSetRunning(Boolean val) {
            if (val != null) {
                running = val;
            }
            return running;
        }
    }

    

    public synchronized void addStockInList(StockDatas stockDatas) {
        if (stockDatas != null) {
            stockDatasList.add(stockDatas);
        }
    }



    public class ImageContorller extends Thread {
        
    }

    private void showNeuralNet() {
        ArrayList<String> nameList = MainController.getNeuralNetSetsName();
        neuralListView.setItems(FXCollections.observableArrayList(nameList));
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        enableGetExampleButton();
        if (!neuralListView.getSelectionModel().isEmpty()) {
            int idx = neuralListView.getSelectionModel().getSelectedIndex();
            ArrayList<String> orderList = MainController.getAndSetNeuralNetSetsList(null).get(idx).getOrders();
            Platform.runLater(() -> {
                stockListView.setItems(FXCollections.observableArrayList(orderList));
            });
        } else {
            Platform.runLater(() -> {
                stockListView.setItems(null);
            });
        }
    }

    public void userTypeDateEndToTextField(KeyEvent keyEvent) throws IOException {
        enableGetExampleButton();
    }

    private void enableGetExampleButton() {
        if (dateChecker() && !neuralListView.getSelectionModel().isEmpty()) {
            Platform.runLater(() -> {
                getExampleButton.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                getExampleButton.setDisable(true);
            });
        }
    }


    private boolean dateChecker() {
        if (dateChecker(dateEndToTextField.getText())) {
            if (Integer.parseInt(dateEndToTextField.getText()) > 19900101) {
                return true;
            }
        }
        return false;
    }

    private boolean dateChecker(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
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





}
