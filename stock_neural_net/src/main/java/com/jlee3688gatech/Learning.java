package com.jlee3688gatech;

import java.util.ArrayList;

public class Learning {

    private NeuralNet neuralNet;
    private ExampleMaker exampleMaker;
    private ArrayList<ArrayList<ArrayList<Double>>> examples;
    private RecentInputData recentInput;
    private ArrayList<StockDatas> stockDatasArr;
    
    public Learning(NeuralNet neuralNet, ArrayList<StockDatas> stockDatasArr) {
        this.stockDatasArr = stockDatasArr;
        this.neuralNet = neuralNet;
        exampleMaker = new ExampleMaker(stockDatasArr);
        this.examples = new ArrayList<ArrayList<ArrayList<Double>>>();
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

        this.recentInput = exampleMaker.getRecentInput(inputDataTypes, numOfDataFromCounter);
        System.out.println(numOfExample + " number of example created.");

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

                String errorString = Double.toString(error);
                errorString = errorString.substring(0, 7);

                String str = "iteration == " + iteration + "  " + "error == " + errorString;
                System.out.println(str);
            }
        } else {
            System.out.println("NN or dataset(examples) does not match.");
        }
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

    public RecentInputData getRecentInput() {
        return this.recentInput;
    }

    
}
