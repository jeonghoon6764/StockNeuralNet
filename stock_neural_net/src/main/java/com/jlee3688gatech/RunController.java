package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

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
    private Status currentStatus;
    private ChangeImageClass changeImageClass;

    private boolean runThread;
    public enum Status {
        OFF, DOWNLOADING, CALCULATING, FAIL
    }

    @FXML
    private void initialize() {
        this.currentStatus = Status.OFF;
        this.runThread = true;
        this.changeImageClass = new ChangeImageClass();
        changeImageClass.start();

        getExampleButton.setDisable(true);
        this.slash = UtilMethods.slash;
        downloadImg = new Image[3];
        runningImg = new Image[8];

        String addr = slash + "Images" + slash + "Run" + slash + "Base.png";
        baseImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < downloadImg.length; i++) {
            addr = UtilMethods.perOSStartAddress + "Images" + slash + "Run" + slash + "Down" + i + ".png";
            downloadImg[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        addr = UtilMethods.perOSStartAddress + "Images" + slash + "Run" + slash + "Fail.png";
        failImg = new Image(getClass().getResource(addr).toExternalForm());
        for (int i = 0; i < runningImg.length; i++) {
            addr = UtilMethods.perOSStartAddress + "Images" + slash + "Run" + slash + "Run" + i + ".png";
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

            getAndSetStatus(Status.DOWNLOADING);
            synchronized (changeImageClass) {
                changeImageClass.notifyAll();
            }

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

            boolean isFailDuringDownloading = false;

            for (int i = 0; i < getStockDatasClassList.size(); i++) {
                if (getStockDatasClassList.get(i).getLoadFail()) {
                    isFailDuringDownloading = true;
                    break;
                }
            }
            if (isFailDuringDownloading) {
                getAndSetStatus(Status.FAIL);
                Platform.runLater(() -> {
                    backButton.setDisable(false);
                });
                return;
            }

            getAndSetStatus(Status.CALCULATING);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {}
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

            
            Calendar period = UtilMethods.CalendarMaker(dateEndToTextField.getText());
            period.add(Calendar.DATE, 1);
            Platform.runLater(() -> {
                periodStartTextField.setText(UtilMethods.CalendarToString(period));
                openingDateTextField.setText(Integer.toString(neuralNetSet.getNumOfDateOutput()));
                numOfDaysIncTextField.setText(Integer.toString(neuralNetSet.getMinIncreaseDate()));
                degreeOfIncStockTextField.setText(Double.toString(neuralNetSet.getRateOfTotalIncrease()));
            });

            ArrayList<Output> outputList = new ArrayList<>();
            for (int i = 0; i < neuralNetSetOutput.getSize(); i++) {
                outputList.add(new Output(neuralNetSetOutput.getStockName(i), neuralNetSetOutput.getTrueValue(i), neuralNetSetOutput.getFalseValue(i)));
            }
            Collections.sort(outputList);
            ArrayList<String> arrList = new ArrayList<>();

            for (int i = 0; i < outputList.size(); i++) {
                String str = new String();
                str += "Stock: " + outputList.get(i).stockName;
                str += " Probability: " + String.format("%.2f", outputList.get(i).probablity * 100) + "%";
                arrList.add(str);
            }
            
            Platform.runLater(() -> {
                resultListView.setItems(FXCollections.observableArrayList(arrList));
                backButton.setDisable(false);
                getExampleButton.setDisable(false);
            });
            getAndSetStatus(Status.OFF);
        }
    }

    public class Output implements Comparable {
        String stockName;
        Double probablity;
        public Output(String stockName, Double trueValue, Double falseValue) {
            this.stockName = stockName;
            this.probablity = trueValue - falseValue;
            this.probablity += 1;
            this.probablity /= 2;
        }
        @Override
        public int compareTo(Object o) {
            return ((Output)o).probablity.compareTo(this.probablity);
        }
    }


    public class ChangeImageClass extends Thread {

        public synchronized void run() {
            Random rd = new Random();
            while(getAndSetRunThreadVar(null)) {
                while (getAndSetStatus(null) == Status.DOWNLOADING) {
                    Platform.runLater(() -> {
                        imageView.setImage(downloadImg[rd.nextInt(downloadImg.length)]);
                    });
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                }

                while (getAndSetStatus(null) == Status.CALCULATING) {
                    for (int i = 0; i < runningImg.length; i++) {
                        int idx = i;
                        Platform.runLater(() -> {
                            imageView.setImage(runningImg[idx]);
                        });
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {}
                    }
                }

                if (getAndSetStatus(null) == Status.FAIL) {
                    Platform.runLater(() -> {
                        imageView.setImage(failImg);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                    Platform.runLater(() -> {
                        imageView.setImage(baseImg);
                    });
                    getAndSetStatus(Status.OFF);
                }

                if (getAndSetStatus(null) == Status.OFF) {
                    Platform.runLater(() -> {
                        imageView.setImage(baseImg);
                    });
                    try {
                        wait();
                    } catch (InterruptedException e) {}
                }
            }
        }
    }
    
    public void clearInfoField() {
        Platform.runLater(() -> {
            periodStartTextField.setText("");
            openingDateTextField.setText("");
            numOfDaysIncTextField.setText("");
            degreeOfIncStockTextField.setText("");
            resultListView.setItems(null);
        });
    }

    public synchronized Status getAndSetStatus(Status status) {
        if (status != null) {
            this.currentStatus = status;
        }
        return this.currentStatus;
    }

    public synchronized boolean getAndSetRunThreadVar(Boolean val) {
        if (val != null) {
            this.runThread = val;
        }
        return this.runThread;
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
                stockDatas = new StockDatas("AUTO_GENERATED", stockTicker, from, to);
            } catch (Exception e) {
                loadFail = true;
            }
            if (loadFail) {
                getAndSetRunning(false);
                synchronized(getExampleClass) {
                    getExampleClass.notifyAll();
                }
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

    private void showNeuralNet() {
        ArrayList<String> nameList = MainController.getNeuralNetSetsName();
        neuralListView.setItems(FXCollections.observableArrayList(nameList));
    }

    public void neuralNetSetMouseClicked(MouseEvent mouseEvent) throws IOException{
        enableGetExampleButton();
        clearInfoField();
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
        clearInfoField();
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

        getAndSetRunThreadVar(false);
        
        synchronized (changeImageClass) {
            changeImageClass.notifyAll();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }





}
