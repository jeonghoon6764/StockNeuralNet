package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StockList {

    private ArrayList<String> techStockNameList;
    private ArrayList<String> techStockTickerList;

    public StockList() {
        techStockNameList = new ArrayList<String>();
        techStockTickerList = new ArrayList<String>();
        setTechStockList();
    }

    private void setTechStockList() {
        techStockNameList.add("ADOBE");
        techStockNameList.add("AMAZON");
        techStockNameList.add("AMD");
        techStockNameList.add("APPLE");
        techStockNameList.add("ASML");
        techStockNameList.add("CISCO");
        techStockNameList.add("DISNEY");
        techStockNameList.add("EA");
        techStockNameList.add("FACEBOOK");
        techStockNameList.add("GOLD");
        techStockNameList.add("GOOGLE CLASS A");
        techStockNameList.add("GOOGLE CLASS C");
        techStockNameList.add("HP");
        techStockNameList.add("IBM");
        techStockNameList.add("INTEL");
        techStockNameList.add("IYY");
        techStockNameList.add("MICROSOFT");
        techStockNameList.add("NASDAQ");
        techStockNameList.add("NETFLIX");
        techStockNameList.add("NVIDIA");
        techStockNameList.add("ORACLE");
        techStockNameList.add("SALESFORCE");
        techStockNameList.add("SONY");
        techStockNameList.add("TENCENT");
        techStockNameList.add("TESLA");
        techStockNameList.add("TLT");
        techStockNameList.add("TSMC");
        techStockNameList.add("VMWARE");
        

        techStockTickerList.add("ADBE");
        techStockTickerList.add("AMZN");
        techStockTickerList.add("AMD");
        techStockTickerList.add("AAPL");
        techStockTickerList.add("ASML");
        techStockTickerList.add("CSCO");
        techStockTickerList.add("DIS");
        techStockTickerList.add("EA");
        techStockTickerList.add("FB");
        techStockTickerList.add("GLD");
        techStockTickerList.add("GOOGL");
        techStockTickerList.add("GOOG");
        techStockTickerList.add("HP");
        techStockTickerList.add("IBM");
        techStockTickerList.add("INTC");
        techStockTickerList.add("IYY");
        techStockTickerList.add("MSFT");
        techStockTickerList.add("NDAQ");
        techStockTickerList.add("NFLX");
        techStockTickerList.add("NVDA");
        techStockTickerList.add("ORCL");
        techStockTickerList.add("CRM");
        techStockTickerList.add("SONY");
        techStockTickerList.add("TCEHY");
        techStockTickerList.add("TSLA");
        techStockTickerList.add("TLT");
        techStockTickerList.add("TSM");
        techStockTickerList.add("VMW");
    }

    public ArrayList<String> getNameAndTicker(int i) {
        if (i > techStockNameList.size() || i > techStockTickerList.size()) {
            return null;
        }

        ArrayList<String> nameAndTicker = new ArrayList<String>();
        nameAndTicker.add(techStockNameList.get(i));
        nameAndTicker.add(techStockTickerList.get(i));

        return nameAndTicker;
    }

    public int getSize() {
        if (techStockNameList.size() != techStockTickerList.size()) {
            return -1;
        } else {
            return techStockNameList.size();
        }
    }

    public ArrayList<String> getSeparateNameList(int separate, int num) {
        ArrayList<String> ret = new ArrayList<String>();
        for (int i = 0; i < techStockNameList.size(); i++) {
            if (i % separate == num) {
                ret.add(techStockNameList.get(i));
            }
        }
        return ret;
    }
    
}
