package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.event.SwingPropertyChangeSupport;

import com.jlee3688gatech.Learn3Controller.ShowThreadListViewClass;

import org.slf4j.helpers.Util;

public class Learning {

    private NeuralNet neuralNet;
    private ExampleMaker exampleMaker;
    private ArrayList<ArrayList<ArrayList<Double>>> examples;
    private ArrayList<StockDatas> stockDatasArr;
    private Double currError;
    private ShowThreadListViewClass showThreadListViewClass;
    
    public Learning(NeuralNet neuralNet, ArrayList<StockDatas> stockDatasArr) {
        this.currError = 1.0;
        this.stockDatasArr = stockDatasArr;
        this.neuralNet = neuralNet;
        exampleMaker = new ExampleMaker(stockDatasArr);
        this.examples = new ArrayList<ArrayList<ArrayList<Double>>>();
    }

    public void setShowThreadListViewClass(ShowThreadListViewClass showThreadListViewClass) {
        this.showThreadListViewClass = showThreadListViewClass;
    }

    public double makeExamples(int targetNumOfInc, String targetDataType, int targetDataCountFrom
    , int targetDataCountTo, double incRate, ArrayList<String> inputDataTypes, int numOfDataFromCounter) {

        int targetTickerNum = -1;

        for (int i = 0; i < stockDatasArr.size(); i++) {
            if (neuralNet.getTargetTicker().equals(stockDatasArr.get(i).getTicker())) {
                targetTickerNum = i;
            }
        }
        int numOfExample = 0;
        int numOfTrue = 0;

        ArrayList<ArrayList<Double>> temp = exampleMaker.getExamples(targetTickerNum, targetNumOfInc, targetDataType, targetDataCountFrom, targetDataCountTo, incRate
        , inputDataTypes, numOfDataFromCounter);

        while(temp != null) {
            if (temp.get(1).get(0) > 0) {
                numOfTrue++;
            }
            numOfExample++;
            this.examples.add(temp);
            exampleMaker.incCounter();
            temp = exampleMaker.getExamples(targetTickerNum, targetNumOfInc, targetDataType, targetDataCountFrom, targetDataCountTo, incRate
            , inputDataTypes, numOfDataFromCounter);
        }

        if (numOfExample == 0) {
            return 0;
        }

        return (double)numOfTrue / (double)numOfExample;
    }

    public double makeExamples(int targetTickerNum) {
        ArrayList<String> inputTypes = new ArrayList<String>();
        inputTypes.add("adjclosed");
        inputTypes.add("close");
        inputTypes.add("high");
        inputTypes.add("low");
        inputTypes.add("open");
        inputTypes.add("volume");
        return makeExamples(3, "adjclosed", 5, 10, 0.01, inputTypes, 5);
    }

    public String getNeuralNetTarget() {
        return neuralNet.getTargetTicker();
    }

    private boolean checkAvailable() {
        int nNInputSize = neuralNet.getInputSize();
        int nNOutputSize = neuralNet.getOutputSize();

        if (examples.get(0).get(0).size() != nNInputSize) {
            return false;
        }
        if (examples.get(0).get(1).size() != nNOutputSize) {
            return false;
        }
        return true;
    }

    public void backPropLearnNeuralNet(double learningRate, int maxIteration, double minError) {
        if (checkAvailable()) {
            Double error = Double.MAX_VALUE;
            int iteration = 0;
            while(error > minError && iteration < maxIteration) {
                error = 0.0;
                for (int i = 0; i < examples.size(); i++) {
                    neuralNet.feedFoward(examples.get(i).get(0));
                    error += neuralNet.backPropLearning(examples.get(i).get(1), learningRate);
                }
                error /= examples.size();
                iteration++;
                currError = error;
                System.out.println(error);
                synchronized(showThreadListViewClass) {
                    showThreadListViewClass.notifyAll();
                }
            }
        } else {
            System.out.println("NN or dataset(examples) does not match.");
        }
    }

    public double getCurrError() {
        return this.currError;
    }

    public int getNumOfExample() {
        return this.examples.size();
    }

    public ArrayList<String> getStockNames() {
        return exampleMaker.getStockNameList();
    }

    public ArrayList<String> getStockTickers() {
        return exampleMaker.getStockTickerList();
    }
}
