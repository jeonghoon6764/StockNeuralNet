package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

public class MainController {

    private static ArrayList<NeuralNetSet> neuralNetSetsList = new ArrayList<NeuralNetSet>();
    private static ArrayList<StockDatas> stockDatasList = new ArrayList<StockDatas>();

    public synchronized static ArrayList<NeuralNetSet> getAndSetNeuralNetSetsList (ArrayList<NeuralNetSet> neuralNetSets) {
        if (neuralNetSets != null) {
            neuralNetSetsList = neuralNetSets;
        }
        return neuralNetSetsList;
    }

    public synchronized static ArrayList<StockDatas> getAndSetStockDatasList (ArrayList<StockDatas> stockDatasSets) {
        if (stockDatasSets != null) {
            stockDatasList = stockDatasSets;
        }
        return stockDatasList;
    }

    public synchronized static void addStockToStockDatasList(StockDatas stockDatas) {
        ArrayList<String> tickerList = new ArrayList<>();
        for (int i = 0; i < stockDatasList.size(); i++) {
            tickerList.add(stockDatasList.get(i).getTicker());
        }

        if (tickerList.contains(stockDatas.getTicker())) {
            return;
        }
        stockDatasList.add(stockDatas);
    }

    public static ArrayList<String> getNeuralNetSetsName() {
        ArrayList<String> namesList = new ArrayList<String>();
        for (int i = 0; i < neuralNetSetsList.size(); i++) {
            namesList.add(neuralNetSetsList.get(i).getName());
        }
        return namesList;

    }

    public static ArrayList<String> getStockDatasNameAndTicker() {
        ArrayList<String> stockNamesAndTickers = new ArrayList<String>();
        for (int i = 0; i < stockDatasList.size(); i++) {
            String str = stockDatasList.get(i).getName() + " : " + stockDatasList.get(i).getTicker();
            stockNamesAndTickers.add(str);
        }
        return stockNamesAndTickers;
    }

    public static ArrayList<String> getStockDatasSpecific() {
        ArrayList<String> stockDatasSpecific = new ArrayList<String>();
        for (int i = 0; i < stockDatasList.size(); i++) {
            String str = stockDatasList.get(i).getName() + " : " + stockDatasList.get(i).getTicker();
            str += "      " + UtilMethods.CalendarToString(stockDatasList.get(i).getFromDate());
            str += " ~ " + UtilMethods.CalendarToString(stockDatasList.get(i).getToDate());
            stockDatasSpecific.add(str);
        }
        return stockDatasSpecific;
    }

    
}
