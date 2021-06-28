package com.jlee3688gatech;

import java.util.ArrayList;

public class MainController {

    public static ArrayList<NeuralNetSet> neuralNetSetsList = new ArrayList<NeuralNetSet>();
    public static ArrayList<StockDatas> stockDatasList = new ArrayList<StockDatas>();

    public static ArrayList<String> getNeuralNetSetsName() {
        //ArrayList<String> namesList = new ArrayList<String>();
        //for (int i = 0; i < neuralNetSetsList.size(); i++) {
        //    namesList.add(neuralNetSetsList.get(i).getName());
        //}
        //return namesList;

        ArrayList<String> namesList = new ArrayList<String>();
        namesList.add("T");
        namesList.add("E");
        namesList.add("S");
        namesList.add("T");

        return namesList;

    }

    public static ArrayList<String> getStockDatasNameAndTicker() {
        //ArrayList<String> stockNamesAndTickers = new ArrayList<String>();
        //for (int i = 0; i < stockDatasList.size(); i++) {
        //    String str = stockDatasList.get(i).getName() + " : " + stockDatasList.get(i).getTicker();
        //    stockNamesAndTickers.add(str);
        //}
        //return stockNamesAndTickers;

        ArrayList<String> ret = new ArrayList<String>();
        ret.add("T : T");
        ret.add("E : E");
        ret.add("S : S");
        ret.add("T : T");

        return ret;
    }

    
}
