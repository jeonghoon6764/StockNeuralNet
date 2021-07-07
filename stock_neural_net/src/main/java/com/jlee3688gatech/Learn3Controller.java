package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

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
    private Button seeThreadWorkButton;
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

    private int nextTarget;


    @FXML
    private void initialize() {
        currActivatedThread = 0;
        targetNumOfThread = 0;
        this.slash = UtilMethods.slash;
        disableAllButtons();
        cpuImageArr = new Image[10];
        for (int i = 0; i < cpuImageArr.length; i++) {
            String addr = slash + "Images" + slash + "CPUs" + slash + "CPU" + i + ".png";
            cpuImageArr[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
    }

    public void setLearningList(ArrayList<Learning> learningList) {
        this.learningList = learningList;
    }

    public class LearningThreadClass extends Thread {
        private Learning learning;
        public LearningThreadClass (Learning learning) {
            this.learning = learning;
        }

        public void run() {
            
        }
    }

    public void checkAndRunThreads() {
        if (getAndSetTargetNumOfThread(null) > getAndSetCurrActivatedThread(null)) {

        }
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
    }

    public void userClickRequestRemoveThread(ActionEvent actionEvent) throws IOException {
        getAndSetTargetNumOfThread(-1);
        showNumOfThreadTextField();
    }




    public void userClickBuildToDoButton(ActionEvent actionEvent) throws IOException {
        this.learningRate = Double.parseDouble(learningRateTextField.getText());
        this.maxIteration = Integer.parseInt(maxIterationTextField.getText());
        this.minError = Double.parseDouble(minErrorTextField.getText());

        getAndSetRunThreadVar(true);
        CPUImageChangeClass cpuImageChangeClass = new CPUImageChangeClass();
        cpuImageChangeClass.start();

        

        Platform.runLater(() -> {
            
            learningRateTextField.setDisable(true);
            maxIterationTextField.setDisable(true);
            minErrorTextField.setDisable(true);
            buildToDoButton.setDisable(true);
            requestMoreThreadButton.setDisable(false);
            requestRemoveThreadButton.setDisable(false);
            seeThreadWorkButton.setDisable(false);
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
            seeThreadWorkButton.setDisable(true);
            finButton.setDisable(true);
        });
    }


}
