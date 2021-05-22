package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

public class StockData {

    HashMap<String, Stock> stockMap;
    HashMap<String, Stock> indicatorsMap;
    ArrayList<String> tickers;
    ArrayList<String> indicators;

    /**
     * Constructor for Stockdata class.
     * @param from
     * @param to
     * @throws IOException
     */
    public StockData(Calendar from, Calendar to) throws IOException {
        stockMap = new HashMap<String,Stock>();
        indicatorsMap = new HashMap<String,Stock>();
        indicatorsNote();
        tickerNote();
        initMaps(from, to);
    }

    public void initMaps(Calendar from, Calendar to) throws IOException {
        for (int i = 0; i < indicators.size(); i++) {
            Stock st = YahooFinance.get(indicators.get(i), from, to, Interval.DAILY);
            indicatorsMap.put(indicators.get(i), st);
        }
        for (int i = 0; i < tickers.size(); i++) {
            Stock st = YahooFinance.get(tickers.get(i), from, to, Interval.DAILY);
            stockMap.put(tickers.get(i), st);
        }
    }

    public void indicatorsNote() {
        indicators = new ArrayList<String>();
        indicators.add("TLT");
        indicators.add("GLD");
        indicators.add("IYY");
        indicators.add("NDAQ");
    }

    public void tickerNote() {
        tickers = new ArrayList<String>();
        tickers.add("TSLA");
        tickers.add("AAPL");
        tickers.add("MSFT");
        tickers.add("AMZN");
        tickers.add("GE");
        tickers.add("GM");
        tickers.add("AMC");
        tickers.add("GOOG");
        tickers.add("DAL");
        tickers.add("UAL");
        tickers.add("AMD");
    }


    public ArrayList<Double> makeCloseDiffList(Stock st) throws IOException {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < st.getHistory().size(); i++) {
            ret.add(i, st.getHistory().get(i).getClose().doubleValue());
        }
        double maxDiff = 0.0;
        for (int i = ret.size() - 1; i > 0; i--) {
            double diff = ret.get(i) - ret.get(i - 1);
            ret.set(i, diff);
            if (maxDiff < Math.abs(diff)) {
                maxDiff = Math.abs(diff);
            }
        }
        ret.remove(0);
        for (int i = 0; i < ret.size(); i++) {
            ret.set(i, ret.get(i) / maxDiff);
        }
        return ret;
    }

    public ArrayList<Double> makeVolumeDiffList(Stock st) throws IOException {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        for (int i = 0; i < st.getHistory().size(); i++) {
            temp.add(i, st.getHistory().get(i).getVolume().intValue());
        }
        int maxDiff = 0;
        for (int i = temp.size() - 1; i > 0; i--) {
            int diff = temp.get(i) - temp.get(i - 1);
            temp.set(i, diff);
            if (maxDiff < Math.abs(diff)) {
                maxDiff = Math.abs(diff);
            }
        }
        temp.remove(0);
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < temp.size(); i++) {
            ret.add(i, ((double)temp.get(i)) / ((double)maxDiff));
        }
        return ret;
    }

    public ArrayList<ArrayList<Double>> getIndicatorsArr() throws IOException {
        ArrayList<ArrayList<Double>> indicatorsArr = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < indicators.size(); i++) {
            ArrayList<Double> indicatorCloseDiffArray 
            = makeCloseDiffList(indicatorsMap.get(indicators.get(i)));
            ArrayList<Double> indicatorVolumeDiffArray 
            = makeVolumeDiffList(indicatorsMap.get(indicators.get(i)));
            indicatorsArr.add(indicatorCloseDiffArray);
            indicatorsArr.add(indicatorVolumeDiffArray);
        }
        return indicatorsArr;
    }

    public ArrayList<Double> getCloseDiffArr(String str) throws IOException {
        System.out.println("str == "+ str);
        Stock st = stockMap.get(str);
        return makeCloseDiffList(st);
    }

    public ArrayList<Double> getVolumeDiffArr(String str) throws IOException {
        Stock st = stockMap.get(str);
        return makeVolumeDiffList(st);
    }

    public ArrayList<String> getTickersList() {
        return tickers;
    }

    public ArrayList<String> getIndicatorsList() {
        return indicators;
    }
}

