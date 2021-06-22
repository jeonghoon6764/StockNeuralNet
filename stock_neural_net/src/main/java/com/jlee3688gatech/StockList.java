package com.jlee3688gatech;

import java.util.HashMap;
import java.util.Map;

public class StockList {

    private Map<String, String> techStockList;
    private Map<String, String> techStockList1;
    private Map<String, String> techStockList2;

    public StockList() {
        techStockList = new HashMap<String,String>();
        techStockList1 = new HashMap<String,String>();
        techStockList2 = new HashMap<String,String>();

        setTechStockList();
        separateStockList();
    }

    private void setTechStockList() {
        techStockList.put("ADOBE", "ADBE");
        techStockList.put("AMAZON", "AMZN");
        techStockList.put("AMD", "AMD");
        techStockList.put("APPLE", "AAPL");
        techStockList.put("ASML", "ASML");
        techStockList.put("CISCO", "CSCO");
        techStockList.put("DISNEY", "DIS");
        techStockList.put("EA", "EA");
        techStockList.put("FACEBOOK", "FB");
        techStockList.put("GOLD", "GLD");
        techStockList.put("GOOGLE CLASS A", "GOOGL");
        techStockList.put("GOOGLE CLASS C", "GOOG");
        techStockList.put("HP", "HP");
        techStockList.put("IBM", "IBM");
        techStockList.put("INTEL", "INTC");
        techStockList.put("IYY", "IYY");
        techStockList.put("MICROSOFT", "MSFT");
        techStockList.put("NASDAQ", "NDAQ");
        techStockList.put("NETFLIX", "NFLX");
        techStockList.put("NVIDIA", "NVDA");
        techStockList.put("ORACLE", "ORCL");
        techStockList.put("SALESFORCE", "CRM");
        techStockList.put("SONY", "SONY");
        techStockList.put("TENCENT", "TCEHY");
        techStockList.put("TESLA", "TSLA");
        techStockList.put("TLT", "TLT");
        techStockList.put("TSMC", "TSM");
        techStockList.put("VMWARE", "VMW");
    }

    private void separateStockList() {
        techStockList1.put("ADOBE", "ADBE");
        techStockList1.put("AMAZON", "AMZN");
        techStockList1.put("AMD", "AMD");
        techStockList1.put("APPLE", "AAPL");
        techStockList1.put("ASML", "ASML");
        techStockList1.put("CISCO", "CSCO");
        techStockList1.put("DISNEY", "DIS");
        techStockList1.put("EA", "EA");
        techStockList1.put("FACEBOOK", "FB");
        //techStockList1.put("GOLD", "GLD");
        techStockList1.put("GOOGLE CLASS A", "GOOGL");
        techStockList1.put("GOOGLE CLASS C", "GOOG");
        techStockList1.put("HP", "HP");
        techStockList1.put("IBM", "IBM");
        techStockList2.put("INTEL", "INTC");
        //techStockList2.put("IYY", "IYY");
        techStockList2.put("MICROSOFT", "MSFT");
        //techStockList2.put("NASDAQ", "NDAQ");
        techStockList2.put("NETFLIX", "NFLX");
        techStockList2.put("NVIDIA", "NVDA");
        techStockList2.put("ORACLE", "ORCL");
        techStockList2.put("SALESFORCE", "CRM");
        techStockList2.put("SONY", "SONY");
        techStockList2.put("TENCENT", "TCEHY");
        techStockList2.put("TESLA", "TSLA");
        //techStockList2.put("TLT", "TLT");
        techStockList2.put("TSMC", "TSM");
        techStockList2.put("VMWARE", "VMW");
    }

    public Map<String, String> getTechStockList1() {
        return techStockList1;
    }

    public Map<String, String> getTechStockList2() {
        return techStockList2;
    }

    public Map<String, String> getTechStockList() {
        return techStockList;
    }
    
}
