package com.jlee3688gatech;

import java.util.ArrayList;

public class NeuralNetSetOutput {
    private ArrayList<String> stockNames;
    private ArrayList<Double> trueValues;
    private ArrayList<Double> falseValues;
    private int size;

    public NeuralNetSetOutput() {
        this.stockNames = new ArrayList<String>();
        this.trueValues = new ArrayList<Double>();
        this.falseValues = new ArrayList<Double>();
        this.size = 0;
    }

    public void addOutput(String stockName, double trueValue, double falseValue) {
        this.stockNames.add(stockName);
        this.trueValues.add(trueValue);
        this.falseValues.add(falseValue);
        this.size++;
    }

    public int getSize() {
        return this.size;
    }

    public String getStockName(int i) {
        return this.stockNames.get(i);
    }

    public double getTrueValue(int i) {
        return this.trueValues.get(i);
    }

    public double getFalseValue(int i) {
        return this.falseValues.get(i);
    }
}
