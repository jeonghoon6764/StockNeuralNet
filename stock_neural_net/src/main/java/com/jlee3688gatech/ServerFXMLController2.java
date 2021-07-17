package com.jlee3688gatech;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;

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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ServerFXMLController2 {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portNumTextField;
    @FXML
    private TextField learningRateTextField;
    @FXML
    private TextField minErrorTextField;
    @FXML
    private TextField maxIterTextField;
    @FXML
    private ListView joinedConputerListView;
    @FXML
    private Button buildToDoButton;
    @FXML
    private TextField numOfThreadTextField;
    @FXML
    private Button finButton;
    @FXML
    private Button requestMoreThreadButton;
    @FXML
    private Button requestRemoveThreadButton;
    @FXML
    private ListView statusListView;
    
    private NeuralNetSet neuralNetSet;
    private ArrayList<Learning> learningSet;
    private Server server;
    private String localAddr;
    private int portNum;
    private int nextTargetLearning;
    private Status[] learningStatus;
    private int currActivatedThread;
    private int targetNumOfThread;
    private double learningRate;
    private double minErrorRate;
    private int maxIteration;
    private ArrayList<Double> errorRateArrayList;
    private String slash;

    public enum Status {
        READY, RUNING, RECEIVING, COMPLETE
    }

    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        this.targetNumOfThread = 0;
        this.errorRateArrayList = new ArrayList<>();

        Platform.runLater(() -> {
            ipTextField.setText(localAddr);
            portNumTextField.setText(Integer.toString(portNum));
            server.setGUIController(this);
            updateJoinedComputerTextField();
            //showstatusGUIList();
        });
    }

    public NeuralNetSet getNeuralNetSet() {
        return this.neuralNetSet;
    }

    public ArrayList<Learning> getLearningSet() {
        return this.learningSet;
    }

    public ArrayList<String> getStatusGUIArrayList() {
        ArrayList<String> statusGUIArrayList = new ArrayList<>();
        for (int i = 0; i < learningSet.size(); i++) {
            String str = "Learning " + i + ": <TARGET>" + learningSet.get(i).getNeuralNetTarget();
            str += " " + learningStatus[i].toString();

            if (learningStatus[i] == Status.RUNING) {
                str += " Error Rate: " + errorRateArrayList.get(i);
            }
            statusGUIArrayList.add(str);
        }
        return statusGUIArrayList;
    }

    public synchronized void showstatusGUIList() {
        Platform.runLater(() -> {
            statusListView.setItems(FXCollections.observableArrayList(getStatusGUIArrayList()));
        });
    }

    public void updateJoinedComputerTextField() {
        Platform.runLater(() -> {
            joinedConputerListView.setItems(FXCollections.observableArrayList(server.getOrAddJoinedComputerList(null)));
        });
    }

    public void setErrorRate(int index, double errorRate) {
        this.errorRateArrayList.set(index, errorRate);
        showstatusGUIList();
    }

    public synchronized int getAndSetCurrActivatedThread(Integer val) {
        if (val != null) {
            currActivatedThread += val;
        }
        return currActivatedThread;
    }

    public class LearningThreadClass extends Thread {
        private int idx;
        private double learningRate;
        private int maxIteration;
        private double minError;
        private ServerFXMLController2 thisClass;

        public LearningThreadClass (int idx, double learningRate, int maxIteration, double minError, ServerFXMLController2 thisClass) {
            this.idx = idx;
            this.learningRate = learningRate;
            this.maxIteration = maxIteration;
            this.minError = minError;
            this.thisClass = thisClass;
        }

        public void run() {
            getAndSetCurrActivatedThread(1);
            learningSet.get(idx).setServerFXMLController2(thisClass);
            learningSet.get(idx).setItsIndexInLearningList(idx);
            learningSet.get(idx).backPropLearnNeuralNet(learningRate, maxIteration, minError);
            learningStatus[idx] = Status.COMPLETE;
            getAndSetCurrActivatedThread(-1);
            checkAndRunThreads();
            
        }
    }

    public void notifyFinishWork(int idx) {
        learningStatus[idx] = Status.COMPLETE;
        checkEnd();
    }


    public void checkEnd() {
        boolean allFinished = true;
        for (int i = 0; i < learningStatus.length; i++) {
            if (learningStatus[i] != Status.COMPLETE) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            showstatusGUIList();
            Platform.runLater(() -> {
                finButton.setDisable(false);
            });
        }
    }



    public synchronized int getNetxtReadyLearningSetIndex() {
        int ret = -1;
        for (int i = 0; i < learningStatus.length; i++) {
            if (learningStatus[i] == Status.READY) {
                learningStatus[i] = Status.RUNING;
                return i;
            }
        }
        return ret;
    }

    public void setNeuralNetSet(NeuralNetSet neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }

    public void setLearningSet(ArrayList<Learning> learningSet) {
        this.learningSet = learningSet;
        this.learningStatus = new Status[learningSet.size()];

        for (int i = 0; i < learningStatus.length; i++) {
            this.learningStatus[i] = Status.READY;
            this.errorRateArrayList.add(1.0);
        }
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
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

    public synchronized int getAndSetTargetNumOfThread(Integer val) {
        if (val != null && (targetNumOfThread + val) >= 0) {
            targetNumOfThread += val;
        }
        return targetNumOfThread;
    }


    public void userClickRequestMoreThread(ActionEvent actionEvent) throws IOException{
        getAndSetTargetNumOfThread(1);
        showNumOfThreadTextField();
        showstatusGUIList();
        checkAndRunThreads();
    }

    public void userClickRequestRemoveThread(ActionEvent actionEvent) throws IOException{
        getAndSetTargetNumOfThread(-1);
        showNumOfThreadTextField();
        showstatusGUIList();
        checkAndRunThreads();
    }

    private void showNumOfThreadTextField() {
        int numOfTargetThread = getAndSetTargetNumOfThread(null);
        Platform.runLater(() -> {
            numOfThreadTextField.setText(Integer.toString(numOfTargetThread));
        });
    }

    public synchronized void checkAndRunThreads() {
        if (getAndSetTargetNumOfThread(null) > getAndSetCurrActivatedThread(null)) {
            int idx = getNetxtReadyLearningSetIndex();
            if (idx == -1) {
                return;
            }
            LearningThreadClass learningThreadClass = new LearningThreadClass(idx, learningRate, maxIteration, minErrorRate, this);
            learningThreadClass.start();
        }
    }

    public void userClickBuildToDoButton(ActionEvent actionEvent) throws IOException{
        this.learningRate = Double.parseDouble(learningRateTextField.getText());
        this.maxIteration = Integer.parseInt(maxIterTextField.getText());
        this.minErrorRate = Double.parseDouble(minErrorTextField.getText());

        server.setLeatningRate(learningRate);
        server.setMaxIteration(maxIteration);
        server.setminError(minErrorRate);

        Platform.runLater(() -> {
            learningRateTextField.setDisable(true);
            maxIterTextField.setDisable(true);
            minErrorTextField.setDisable(true);
            buildToDoButton.setDisable(true);
            requestMoreThreadButton.setDisable(false);
            requestRemoveThreadButton.setDisable(false);
        });
        showNumOfThreadTextField();
        showstatusGUIList();
    }
    
    public void useClickFinishButton(ActionEvent actionEvent) throws IOException{
        server.turnOffServer();
        
        String str = new String();
        str += "<LEARNING>" + " ";
        str += "Learning Rate: " + learningRate + ", ";
        str += "Max iteration: " + maxIteration + ", ";
        str += "Minimum Error: " + minErrorRate;
        neuralNetSet.addLog(str);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    

    private boolean validTextFieldChecker() {
        if (validDoubleChecker(learningRateTextField.getText()) 
        && validIntegerChecker(maxIterTextField.getText()) 
        && validDoubleChecker(minErrorTextField.getText())) {
            if (Double.parseDouble(learningRateTextField.getText()) > 0.0
            && Double.parseDouble(learningRateTextField.getText()) < 1.0
            && Integer.parseInt(maxIterTextField.getText()) > 0
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
}
