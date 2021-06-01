package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

public class ExampleMaker {

    private ArrayList<StockDatas> stockDatasArr;
    private ArrayList<Integer> targets;
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
        targets = new ArrayList<Integer>();
        
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
            targets.add(stockDatasArr.get(i).lookUpTargetInDate(maxFrom));
        }
        counterLimit = stockDatasArr.get(0).lookUpTargetInDate(minTo)
            - stockDatasArr.get(0).lookUpTargetInDate(maxFrom);
    }

    public ArrayList<Double> getInputExamples(ArrayList<String> dataType, int count) {
        
        while(counter + count - 1 <= counterLimit) {
            for (int i = 0; i < count; i++) {

            }
            counter += count;
        }
    }

    public ArrayList<Double> standardization(ArrayList<Double> arr) {
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

        return arr;
    }

    


    
}
