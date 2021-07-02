package com.jlee3688gatech;

import java.io.Serializable;
import java.util.ArrayList;

public class SaveFileStructure implements Serializable{

    private ArrayList<NeuralNetSet> neuralNetSetList;
    private ArrayList<StockDatas> stockDatasList;

    public SaveFileStructure(ArrayList<NeuralNetSet> neuralNetSetList, ArrayList<StockDatas> stockDatasList) {
        this.neuralNetSetList = neuralNetSetList;
        this.stockDatasList = stockDatasList;
    }

    public ArrayList<NeuralNetSet> getNeuralNetSetList() {
        return this.neuralNetSetList;
    }

    public ArrayList<StockDatas> getStockDatasList() {
        return this.stockDatasList;
    }
}
