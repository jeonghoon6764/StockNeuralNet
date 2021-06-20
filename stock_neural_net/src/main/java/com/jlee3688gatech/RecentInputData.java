package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

public class RecentInputData {

    private ArrayList<Double> recentInput;
    private ArrayList<String> stockNames;
    private int numOfInput;
    private Calendar dateStartOf;
    private Calendar dateEndOf;
    private Calendar createdDate;
    
    public RecentInputData(ArrayList<Double> recentInput, ArrayList<String> stockNames, Calendar dateStartOf, Calendar dateEndOf) {
        this.recentInput = recentInput;
        this.stockNames = stockNames;
        this.dateStartOf = dateStartOf;
        this.dateEndOf = dateEndOf;
        this.createdDate = Calendar.getInstance();
        this.numOfInput = recentInput.size();
    }

    public ArrayList<Double> getRecentInput() {
        return recentInput;
    }

    public ArrayList<String> getStockNames() {
        return stockNames;
    }

    public int getNumOfInput() {
        return numOfInput;
    }

    public Calendar getDateStartOf() {
        return dateStartOf;
    }

    public Calendar getDateEndOf() {
        return dateEndOf;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

}
