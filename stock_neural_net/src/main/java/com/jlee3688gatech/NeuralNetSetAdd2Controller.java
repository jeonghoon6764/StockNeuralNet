package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class NeuralNetSetAdd2Controller {

    @FXML
    private TextField numOfDateOutputTextField;
    @FXML
    private TextField minIncreaseDateTextField;
    @FXML
    private TextField totalIncreaseRateTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextArea fnlConfirmationTextArea;
    @FXML
    private Slider truethRatioSlider;
    @FXML
    private Label trueRatioLabel;
    @FXML
    private Label falseRatioLabel;
    @FXML
    private Button calculateButton;
    @FXML
    private Button continueButton;
    @FXML
    private ImageView calculatorImageView;


    private String neuralNetName;
    private int neuralNetNumOfInput;
    private int neuralNetNumOfHiddenLayers;
    private ArrayList<String> referenceList;
    private ArrayList<String> indicatorStocksList;
    private ArrayList<String> indicatorStocksTargetList;
    private String slash;
    private boolean runThread;
    private Image[] calculatorImages;


    /**
     * FXML initializer
     */
    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        calculatorImages = new Image[9];
        for (int i = 0; i < calculatorImages.length; i++) {
            String addr = UtilMethods.perOSStartAddress + "Images" + slash + "Calculator" + slash + "Calculator" + i + ".png";
            calculatorImages[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        calculateButton.setDisable(true);
        continueButton.setDisable(true);
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
     * ActionEvent method. when user click calculate button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickCalculateButton(ActionEvent actionEvent) throws IOException {
        CalculateTrueFalseRateClass calculateTrueFalseRateClass = new CalculateTrueFalseRateClass();
        calculateTrueFalseRateClass.start();
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickContinueButton(ActionEvent actionEvent) throws IOException {
        ArrayList<NeuralNet> neuralNetList = new ArrayList<>();

        for (int i = 0; i < indicatorStocksTargetList.size(); i++) {
            int inputSize = neuralNetNumOfInput * referenceList.size() * indicatorStocksList.size(); 
            int outputSize = 2;
            int[] numOfHiddenLayerNodeArr = new int[neuralNetNumOfHiddenLayers];
            for (int j = 0; j < numOfHiddenLayerNodeArr.length; j++) {
                numOfHiddenLayerNodeArr[j] = inputSize;
            }
            String targetTicker = indicatorStocksTargetList.get(i);

            NeuralNet neuralNet = new NeuralNet(inputSize, outputSize, numOfHiddenLayerNodeArr, targetTicker);

            neuralNetList.add(neuralNet);
        }

        NeuralNetSet neuralNetSet = new NeuralNetSet(neuralNetList, neuralNetName, indicatorStocksList, neuralNetNumOfInput, Integer.parseInt(numOfDateOutputTextField.getText())
        , Integer.parseInt(minIncreaseDateTextField.getText()), Double.parseDouble(totalIncreaseRateTextField.getText()), referenceList);
        MainController.getAndSetNeuralNetSetsList(null).add(neuralNetSet);
        neuralNetSet.addNote(descriptionTextArea.getText());

        userClickBack(actionEvent);
    }

    /**
     * thread for change calculator image.
     */
    public class CalculatorImageChangeClass extends Thread {
        @FXML
        public void run() {
            Random rd = new Random();
            try {
                while(getAndSetRunThreadVar(null)) {
                    Platform.runLater(() -> {
                        calculatorImageView.setImage(calculatorImages[rd.nextInt(9)]);
                    });
                    Thread.sleep(100);                    
                }
            } catch (Exception e) {
                getAndSetRunThreadVar(false);
            }
            calculatorImageView.setImage(null);
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

    /**
     * thread for calculating true/false ratio in example.
     */
    public class CalculateTrueFalseRateClass extends Thread{

        public void run() {
            getAndSetRunThreadVar(true);
            CalculatorImageChangeClass calculatorImageChangeClass = new CalculatorImageChangeClass();
            calculatorImageChangeClass.start();

            ArrayList<StockDatas> tempStockDatasList = new ArrayList<>();
            ArrayList<Integer> tempStockDatasTargetIndices = new ArrayList<>();

            for (int i = 0; i < indicatorStocksList.size(); i++) {
                for (int j = 0; j < MainController.getAndSetStockDatasList(null).size(); j++) {
                    if (indicatorStocksList.get(i).equals(MainController.getAndSetStockDatasList(null).get(j).getTicker())) {
                        tempStockDatasList.add(MainController.getAndSetStockDatasList(null).get(j));
                        break;
                    }
                }
            }

            for (int i = 0; i < tempStockDatasList.size(); i++) {
                if (indicatorStocksTargetList.contains(tempStockDatasList.get(i).getTicker())) {
                    tempStockDatasTargetIndices.add(i);
                }
            }

            int totNumOfExample = 0;
            int totNumOfTrue = 0;

            for (int i = 0; i < tempStockDatasTargetIndices.size(); i++) {
                ExampleMaker exampleMaker = new ExampleMaker(tempStockDatasList);

                ArrayList<ArrayList<Double>> example = exampleMaker.getExamples(tempStockDatasTargetIndices.get(i), Integer.parseInt(minIncreaseDateTextField.getText())
                , "adjclosed", neuralNetNumOfInput, neuralNetNumOfInput + Integer.parseInt(numOfDateOutputTextField.getText())
                , Double.parseDouble(totalIncreaseRateTextField.getText()), referenceList, neuralNetNumOfInput);

                int numOfExample = 0;
                int numOfTrue = 0;

                while(example != null) {
                    if (example.get(1).get(0) > 0) {
                        numOfTrue++;
                    }
                    numOfExample++;
                    exampleMaker.incCounter();
                    example = exampleMaker.getExamples(tempStockDatasTargetIndices.get(i), Integer.parseInt(minIncreaseDateTextField.getText())
                    , "adjclosed", neuralNetNumOfInput, neuralNetNumOfInput + Integer.parseInt(numOfDateOutputTextField.getText())
                    , Double.parseDouble(totalIncreaseRateTextField.getText()), referenceList, neuralNetNumOfInput);
                }
                
                totNumOfExample += numOfExample;
                totNumOfTrue += numOfTrue;
            }
            
            if (totNumOfExample == 0) {
                totNumOfExample = 1;
            }

            double trueRate = (double)totNumOfTrue / (double)totNumOfExample;
            double falseRate = 1 - trueRate;

            String trueRateString = String.valueOf(Math.round(trueRate * 100)) + "%";
            String falseRateString = String.valueOf(Math.round(falseRate * 100)) + "%";
            truethRatioSlider.setMin(0.0);
            truethRatioSlider.setMax(1.0);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {}

            String neuralNetDescription = descriptionTextArea.getText();
            String fnlConfirmText = getFinalConfirmationString(trueRateString, neuralNetDescription);

            getAndSetRunThreadVar(false);
            

            Platform.runLater(() -> {
                truethRatioSlider.setValue(trueRate);
                trueRatioLabel.setText(trueRateString);
                falseRatioLabel.setText(falseRateString);
                continueButton.setDisable(false);
                fnlConfirmationTextArea.setText(fnlConfirmText);
            });
        }
    }

    /**
     * helper method for making final confirmation string
     * @param trueRatioString true ratios value
     * @param description description of neuralNet set.
     * @return String of 
     */
    private String getFinalConfirmationString(String trueRatioString, String description) {
        String str = new String();
        str += "Name: " + neuralNetName + "\n";
        str += "Number of Input: " + Integer.toString(neuralNetNumOfInput) + "\n";
        str += "Number of hidden layer: " + Integer.toString(neuralNetNumOfHiddenLayers) + "\n";
        String temp = referenceList.get(0);
        for (int i = 1; i < referenceList.size(); i++) {
            temp += ", " + referenceList.get(i);
        }
        str += "Reference: " + temp + "\n";
        temp = indicatorStocksList.get(0);
        if (indicatorStocksTargetList.contains(indicatorStocksList.get(0))) {
            temp += "<TARGET>";
        }
        for (int i = 1; i < indicatorStocksList.size(); i++) {
            temp += ", " + indicatorStocksList.get(i);
            if (indicatorStocksTargetList.contains(indicatorStocksList.get(i))) {
                temp += "<TARGET>";
            }
        }
        str += "Indicators: " + temp + "\n";
        str += "Number of output date: " + numOfDateOutputTextField.getText() + "\n";
        str += "Minimum Increase Date: " + minIncreaseDateTextField.getText() + "\n";
        str += "Total Increase Date: " + totalIncreaseRateTextField.getText() + "\n";
        str += "Truth existance in example: " + trueRatioString + "\n";
        str += "Description: " + description;

        return str;
    }

    /**
     * setter method.
     * @param neuralNetName neuralnet name.
     */
    public void setNeuralNetName(String neuralNetName) {
        this.neuralNetName = neuralNetName;
    }

    /**
     * setter method.
     * @param neuralNetNumOfInput neuralNetNumOfInput
     */
    public void setNeuralNetNumOfInput(int neuralNetNumOfInput) {
        this.neuralNetNumOfInput = neuralNetNumOfInput;
    }

    /**
     * setter method.
     * @param neuralNetNumOfHiddenLayers neuralNetNumOfHiddenLayers
     */
    public void setNeuralNetNumOfHiddenLayers(int neuralNetNumOfHiddenLayers) {
        this.neuralNetNumOfHiddenLayers = neuralNetNumOfHiddenLayers;
    }

    /**
     * setter method.
     * @param referenceList referenceList
     */
    public void setReferenceList(ArrayList<String> referenceList) {
        this.referenceList = referenceList;
    }

    /**
     * setter method.
     * @param indicatorStocksList indicatorStocksList
     */
    public void setIndicatorStocksList(ArrayList<String> indicatorStocksList) {
        this.indicatorStocksList = indicatorStocksList;
    }

    /**
     * setter method.
     * @param indicatorStocksTargetList indicatorStocksTargetList
     */
    public void setIndicatorStocksTargetList(ArrayList<String> indicatorStocksTargetList) {
        this.indicatorStocksTargetList = indicatorStocksTargetList;
    }
    

    /**
     * method for enabling continue button.
     */
    public void enableCalculateButton(KeyEvent keyEvent) {
        Platform.runLater(()-> {
            fnlConfirmationTextArea.setText("");
            truethRatioSlider.setValue(0.0);
            trueRatioLabel.setText("0%");
            falseRatioLabel.setText("0%");
        });
        if (!continueButton.isDisable()) {
            continueButton.setDisable(true);
        }

        if (validIntegerNumberTextFieldChecker(numOfDateOutputTextField.getText())
         && validIntegerNumberTextFieldChecker(minIncreaseDateTextField.getText())
         && validDoubleNumberTextFieldChecker(totalIncreaseRateTextField.getText())) {
            if (Integer.parseInt(numOfDateOutputTextField.getText()) >= Integer.parseInt(minIncreaseDateTextField.getText())) {
                calculateButton.setDisable(false);
                return;
            }
        }
        calculateButton.setDisable(true);
    }


    /**
     * method for valid String checker.
     * input should be a String which is represent a integer that bigger than 0.
     * @param str inputString
     * @return true if it is valid otherwise false
     */
    private boolean validIntegerNumberTextFieldChecker(String str) {
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

    /**
     * method for valid String checker.
     * input should be a String which is represent a integer that bigger than 0.
     * @param str inputString
     * @return true if it is valid otherwise false
     */
    private boolean validDoubleNumberTextFieldChecker(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


}
