package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class Learn3Controller {
    @FXML
    private TextField learningRateTextField;
    @FXML
    private TextField maxIterationTextField;
    @FXML
    private TextField minErrorTextField;
    @FXML
    private TextField numOfThreadTextField;
    @FXML
    private Button buildToDoButton;
    @FXML
    private Button requestMoreThreadButton;
    @FXML
    private Button requestRemoveThreadButton;
    @FXML
    private Button finButton;
    @FXML
    private ImageView cpuImageView;
    @FXML
    private ListView todoListView;
    @FXML
    private ListView threadListView;

    private Image[] cpuImageArr;
    private String slash;
    private boolean runThread;
    private double learningRate;
    private int maxIteration;
    private double minError;
    private ArrayList<Learning> learningList;
    private int currActivatedThread;
    private int targetNumOfThread;
    private Status[] learningStatus;
    private ArrayList<LearningThreadClass> threadList;
    private ShowThreadListViewClass showThreadListViewClass;
    private NeuralNetSet neuralNetSet;

    enum Status {
        INITIALIZING, READY, LEARNING, FINISHED
    }


    @FXML
    private void initialize() {
        showThreadListViewClass = new ShowThreadListViewClass();
        threadList = new ArrayList<>();
        currActivatedThread = 0;
        targetNumOfThread = 0;
        this.slash = UtilMethods.slash;
        disableAllButtons();
        cpuImageArr = new Image[10];
        for (int i = 0; i < cpuImageArr.length; i++) {
            String addr = slash + "Images" + slash + "CPUs" + slash + "CPU" + i + ".png";
            cpuImageArr[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        Platform.runLater(() -> {
            showToDoListView();
        });
    }

    public void setNeuralNetSet(NeuralNetSet neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }

    public void setLearningList(ArrayList<Learning> learningList) {
        this.learningList = learningList;
        this.learningStatus = new Status[learningList.size()];

        for (int i = 0; i < learningStatus.length; i++) {
            learningStatus[i] = Status.INITIALIZING;
        }
    }

    private void showToDoListView() {
        ArrayList<String> showList = new ArrayList<>();

        for (int i = 0; i < learningList.size(); i++) {
            String str = "Learning <" + learningList.get(i).getNeuralNetTarget() + ">: " + getAndSetLearningStatus(i, null).toString();
            showList.add(str);
        }

        Platform.runLater(() -> {
            todoListView.setItems(FXCollections.observableArrayList(showList));
        });

    }

    public class LearningThreadClass extends Thread {
        private int idx;
        private boolean run;
        public LearningThreadClass (int idx) {
            run = false;
            this.idx = idx;
        }
        public void run() {
            run = true;
            getAndSetCurrActivatedThread(1);
            learningList.get(idx).setShowThreadListViewClass(showThreadListViewClass);
            synchronized(showThreadListViewClass) {
                showThreadListViewClass.notifyAll();
            }
            learningList.get(idx).backPropLearnNeuralNet(learningRate, maxIteration, minError);
            
            getAndSetLearningStatus(idx, Status.FINISHED);
            showToDoListView();
            getAndSetCurrActivatedThread(-1);
            checkAndRunThreads();
            run = false;
            synchronized(showThreadListViewClass) {
                showThreadListViewClass.notifyAll();
            }
            checkEnd();
        }

        public synchronized boolean getRun() {
            return this.run;
        }

        public synchronized double getCurrError() {
            return learningList.get(idx).getCurrError();
        }

        public String getNeuralNetTarget() {
            return learningList.get(idx).getNeuralNetTarget();
        }
    }

    public void checkEnd() {
        boolean allFinished = true;
        for (int i = 0; i < learningStatus.length; i++) {
            if (getAndSetLearningStatus(i, null) != Status.FINISHED) {
                allFinished = false;
                break;
            }
        }
        
        if (allFinished) {
            Platform.runLater(() -> {
                finButton.setDisable(false);
            });
        }
    }


    public class ShowThreadListViewClass extends Thread {
        public synchronized void run() {
            while(getAndSetRunThreadVar(null)) {
                ArrayList<String> strList = new ArrayList<>();

                for (int i = 0; i < threadList.size(); i++) {
                    if (threadList.get(i).getRun()) {
                        String currErrorStr = String.format("%.5f", threadList.get(i).getCurrError());
                        strList.add("Thread" + (i + 1) + ": Target<" + threadList.get(i).getNeuralNetTarget() + "> current Error rate : " + currErrorStr);
                    }
                }
                Platform.runLater(() -> {
                    threadListView.setItems(FXCollections.observableArrayList(strList));
                });
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
            
        }
    }

    public void userClickedFinish(ActionEvent actionEvent) throws IOException {
        String str = new String();
        str += "<LEARNING>" + " ";
        str += "Learning Rate: " + learningRate + ", ";
        str += "Max iteration: " + maxIteration + ", ";
        str += "Minimum Error: " + minError;
        neuralNetSet.addLog(str);
        getAndSetRunThreadVar(false);
        synchronized(showThreadListViewClass) {
            showThreadListViewClass.notifyAll();
        }
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }


    public synchronized void checkAndRunThreads() {
        if (getAndSetTargetNumOfThread(null) > getAndSetCurrActivatedThread(null)) {
            int idx = -1;
            for (int i = 0; i < learningStatus.length; i++) {
                if (getAndSetLearningStatus(i, null) == Status.READY) {
                    idx = i;
                    break;
                }
            }
            if (idx == -1) {
                return;
            }
            getAndSetLearningStatus(idx, Status.LEARNING);
            showToDoListView();
            LearningThreadClass learningThreadClass = new LearningThreadClass(idx);
            threadList.add(learningThreadClass);
            learningThreadClass.start();
            synchronized (showThreadListViewClass) {
                showThreadListViewClass.notifyAll();
            }
        }
    }

    public synchronized Status getAndSetLearningStatus(int idx, Status status) {
        if (status != null) {
            learningStatus[idx] = status;
        }
        return learningStatus[idx];
    }

    public synchronized int getAndSetTargetNumOfThread(Integer val) {
        if (val != null && (targetNumOfThread + val) >= 0) {
            targetNumOfThread += val;
        }
        return targetNumOfThread;
    }

    public synchronized int getAndSetCurrActivatedThread(Integer val) {
        if (val != null) {
            currActivatedThread += val;
        }
        return currActivatedThread;
    }

    private void showNumOfThreadTextField() {
        int numOfTargetThread = getAndSetTargetNumOfThread(null);
        Platform.runLater(() -> {
            numOfThreadTextField.setText(Integer.toString(numOfTargetThread));
        });
    }

    public void userClickRequestMoreThread(ActionEvent actionEvent) throws IOException {
        getAndSetTargetNumOfThread(1);
        showNumOfThreadTextField();
        checkAndRunThreads();
    }

    public void userClickRequestRemoveThread(ActionEvent actionEvent) throws IOException {
        getAndSetTargetNumOfThread(-1);
        showNumOfThreadTextField();
        checkAndRunThreads();
    }




    public void userClickBuildToDoButton(ActionEvent actionEvent) throws IOException {
        this.learningRate = Double.parseDouble(learningRateTextField.getText());
        this.maxIteration = Integer.parseInt(maxIterationTextField.getText());
        this.minError = Double.parseDouble(minErrorTextField.getText());
        

        for (int i = 0; i < learningStatus.length; i++) {
            getAndSetLearningStatus(i, Status.READY);
        }
        showToDoListView();
        getAndSetRunThreadVar(true);
        CPUImageChangeClass cpuImageChangeClass = new CPUImageChangeClass();
        cpuImageChangeClass.start();
        showThreadListViewClass.start();

        for (int i = 0; i < learningStatus.length; i++) {
            learningStatus[i] = Status.READY;
        }

        Platform.runLater(() -> {
            
            learningRateTextField.setDisable(true);
            maxIterationTextField.setDisable(true);
            minErrorTextField.setDisable(true);
            buildToDoButton.setDisable(true);
            requestMoreThreadButton.setDisable(false);
            requestRemoveThreadButton.setDisable(false);
        });
        showNumOfThreadTextField();
    }

    public void userTypeTextField(KeyEvent keyEvent) throws IOException {
        if (validTextFieldChecker()) {
            Platform.runLater(() -> {
                buildToDoButton.setDisable(false);
            });
        } else {
            Platform.runLater(() -> {
                buildToDoButton.setDisable(true);
            });
        }
    }

    private boolean validTextFieldChecker() {
        if (validDoubleChecker(learningRateTextField.getText()) 
        && validIntegerChecker(maxIterationTextField.getText()) 
        && validDoubleChecker(minErrorTextField.getText())) {
            if (Double.parseDouble(learningRateTextField.getText()) > 0.0
            && Double.parseDouble(learningRateTextField.getText()) < 1.0
            && Integer.parseInt(maxIterationTextField.getText()) > 0
            && Double.parseDouble(minErrorTextField.getText()) > 0.0
            && Double.parseDouble(minErrorTextField.getText()) < 1.0) {
                return true;
            }
        }
        return false;
    }

    private boolean validDoubleChecker(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validIntegerChecker(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * thread for change calculator image.
     */
    public class CPUImageChangeClass extends Thread {
        @FXML
        public void run() {
            Random rd = new Random();
            try {
                while(getAndSetRunThreadVar(null)) {
                    Platform.runLater(() -> {
                        cpuImageView.setImage(cpuImageArr[rd.nextInt(10)]);
                    });
                    Thread.sleep(100);                    
                }
            } catch (Exception e) {
                getAndSetRunThreadVar(false);
            }
            cpuImageView.setImage(null);
        }
    }
    
    /**
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    private synchronized boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
    }


    private void disableAllButtons() {
        Platform.runLater(() -> {
            buildToDoButton.setDisable(true);
            requestMoreThreadButton.setDisable(true);
            requestRemoveThreadButton.setDisable(true);
            finButton.setDisable(true);
        });
    }


}
