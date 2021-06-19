package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

public class ExampleMaker {

    private ArrayList<StockDatas> stockDatasArr;
    //private ArrayList<Integer> targets;
    private ArrayList<ArrayList<Double>> examples;
    private int counter;
    private int counterLimit;
    private Calendar sharedFrom;
    private Calendar sharedTo;

    public ExampleMaker(ArrayList<StockDatas> stockDatasArr) {
        this.counter = 0;
        this.stockDatasArr = stockDatasArr;
        updateVars();
    }

    public void addStock(StockDatas newData) {
        stockDatasArr.add(newData);
        updateVars();
    }

    public void addStock(ArrayList<StockDatas> newDatas) {
        stockDatasArr.addAll(newDatas);
        updateVars();
    }

    private void updateVars() {
        //targets = new ArrayList<Integer>();
        
        Calendar maxFrom = stockDatasArr.get(0).getFromDate();
        Calendar minTo = stockDatasArr.get(0).getToDate();

        for (int i = 0; i < stockDatasArr.size(); i++) {
            Calendar from = stockDatasArr.get(i).getFromDate();
            Calendar to = stockDatasArr.get(i).getToDate();

            if (maxFrom.getTimeInMillis() < from.getTimeInMillis()) {
                maxFrom = from;
            }
            if (minTo.getTimeInMillis() > to.getTimeInMillis()) {
                minTo = to;
            }
        }

        sharedFrom = maxFrom;
        sharedTo = minTo;

        for (int i = 0; i < stockDatasArr.size(); i++) {
            //targets.add(stockDatasArr.get(i).lookUpTargetInDate(maxFrom));
            stockDatasArr.get(i).setTarget(stockDatasArr.get(i).lookUpTargetInDate(maxFrom));
        }
        counterLimit = stockDatasArr.get(0).lookUpTargetInDate(minTo)
            - stockDatasArr.get(0).lookUpTargetInDate(maxFrom);
        System.out.println("SharedFrom  == " + sharedFrom.toString());
        System.out.println("SharedTo == " + sharedTo.toString());
        System.out.println("counterLimit == " + counterLimit);
    }

    public ArrayList<ArrayList<Double>> getExamples(int targetTickerNum, int targetNumOfInc, String targetDataType, int targetDataCountFrom, int targetDataCountTo
        , ArrayList<String> inputDataTypes, int numOfDataFromCounter) {
            
        ArrayList<Double> input = getTargetInputExample(inputDataTypes, numOfDataFromCounter);
        ArrayList<Double> output = getTargetOutputData(targetTickerNum, targetNumOfInc, targetDataType, targetDataCountFrom, targetDataCountTo);

        if(input == null || output == null) {
            return null;
        }

        ArrayList<ArrayList<Double>> ret = new ArrayList<ArrayList<Double>>();
        ret.add(input);
        ret.add(output);

        return ret;
    }

    public void incCounter() {
        this.counter++;
    }

    public void incCounter(int inc) {
        this.counter += inc;
    }


    private ArrayList<Double> getTargetOutputData(int tickerNum, int numOfInc, String dataType, int targetDataCountFrom, int targetDataCountTo) {
        ArrayList<Double> temp = getTickerMultipleDatas(tickerNum, dataType, counter + targetDataCountFrom, counter + targetDataCountTo);
        if (temp == null) {
            return null;
        }

        int countInc = 0;
        double base = temp.get(0);
        if (base > temp.get(temp.size() - 1)) {
            return retFalse();
        } else {
            for (int i = 1; i < temp.size(); i++) {
                if (temp.get(i) > temp.get(i - 1)) {
                    countInc++;
                }
            }
            if (countInc >= numOfInc) {
                return retTrue();
            } else {
                return retFalse();
            }
        }
    }

    private ArrayList<Double> retTrue() {
        ArrayList<Double> ret = new ArrayList<Double>();
        ret.add(1.0);
        ret.add(0.0);
        return ret;
    }

    private ArrayList<Double> retFalse() {
        ArrayList<Double> ret = new ArrayList<Double>();
        ret.add(0.0);
        ret.add(1.0);
        return ret;
    }

    private ArrayList<Double> getTargetInputExample(ArrayList<String> dataTypes, int numOfDataFromCounter) {
        ArrayList<Double> ret = unionAllTickerDatas(dataTypes, counter, numOfDataFromCounter);
        return ret;
    }

    private ArrayList<Double> unionAllTickerDatas(ArrayList<String> dataTypes, int countFrom, int countTo) {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < stockDatasArr.size(); i++) {
            ArrayList<Double> temp = unionSingleTickerDatas(i, dataTypes, countFrom, countTo);
            if (temp == null) {
                return null;
            } else {
                ret.addAll(temp);
            }
        }
        return ret;
    }

    private ArrayList<Double> unionSingleTickerDatas(int tickerNum, ArrayList<String> dataTypes, int countFrom, int countTo) {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < dataTypes.size(); i++) {
            ArrayList<Double> temp = getTickerMultipleDatas(tickerNum, dataTypes.get(i), countFrom, countTo);
            if (temp == null) {
                return null;
            } else {
                standardization(temp);
                ret.addAll(temp);
            }
        }
        return ret;
    }

    private ArrayList<Double> getTickerMultipleDatas(int tickerNum, String dataType, int countFrom, int countTo) {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = countFrom; i < countTo; i++) {
            Double temp = getTickerSingleData(tickerNum, dataType, i);
            if (temp == null) {
                return null;
            } else {
                ret.add(temp);
            }
        }
        return ret;
    }

    private Double getTickerSingleData(int tickerNum, String dataType, int count) {
        if (count > counterLimit) {
            return null;
        }

        if (dataType.equalsIgnoreCase("adjclosed")) {
            return stockDatasArr.get(tickerNum).getTargetAdjClose(count).doubleValue();
        } else if (dataType.equalsIgnoreCase("close")) {
            return stockDatasArr.get(tickerNum).getTargetClose(count).doubleValue();
        } else if (dataType.equalsIgnoreCase("high")) {
            return stockDatasArr.get(tickerNum).getTargetHigh(count).doubleValue();
        } else if (dataType.equalsIgnoreCase("low")) {
            return stockDatasArr.get(tickerNum).getTargetLow(count).doubleValue();
        } else if (dataType.equalsIgnoreCase("open")) {
            return stockDatasArr.get(tickerNum).getTargetOpen(count).doubleValue();
        } else if (dataType.equalsIgnoreCase("volume")) {
            return stockDatasArr.get(tickerNum).getTargetVolume(count).doubleValue();
        } else {
            return null;
        }
    }

    public void standardization(ArrayList<Double> arr) {
        double mean = 0.0;
        for (int i = 0; i < arr.size(); i++) {
            mean += arr.get(i);
        }
        mean /= arr.size();

        double standardDeviation = 0.0;
        for (int i = 0; i < arr.size(); i++) {
            standardDeviation += ((arr.get(i) - mean) * (arr.get(i) - mean));
        }
        standardDeviation /= arr.size();
        standardDeviation = Math.sqrt(standardDeviation);

        for (int i = 0; i < arr.size(); i++) {
            arr.set(i, ((arr.get(i) - mean) / standardDeviation));
        }
    }

    


    
}
