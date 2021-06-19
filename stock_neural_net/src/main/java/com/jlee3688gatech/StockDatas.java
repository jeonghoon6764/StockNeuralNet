package com.jlee3688gatech;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

public class StockDatas implements Serializable{
    private String name;
    private String ticker;
    private int target;
    private Calendar from;
    private Calendar to;
    private ArrayList<Calendar> dates;
    private ArrayList<BigDecimal> adjCloseList;
    private ArrayList<BigDecimal> closeList;
    private ArrayList<BigDecimal> highList;
    private ArrayList<BigDecimal> lowList;
    private ArrayList<BigDecimal> openList;
    private ArrayList<Long> volumeList;

    public StockDatas(String name, String ticker, Calendar from, Calendar to) throws IOException {
        to.add(Calendar.DATE, 1);
        this.name = name;
        this.ticker = ticker;
        this.target = 0;
        this.dates = new ArrayList<Calendar>();
        this.adjCloseList = new ArrayList<BigDecimal>();
        this.closeList = new ArrayList<BigDecimal>();
        this.highList = new ArrayList<BigDecimal>();
        this.lowList = new ArrayList<BigDecimal>();
        this.openList = new ArrayList<BigDecimal>();
        this.volumeList = new ArrayList<Long>();
        
        Stock st = YahooFinance.get(ticker, from, to, Interval.DAILY);
        
        for (int i = 0; i < st.getHistory().size(); i++) {
            this.dates.add(st.getHistory().get(i).getDate());
            this.adjCloseList.add(st.getHistory().get(i).getAdjClose());
            this.closeList.add(st.getHistory().get(i).getClose());
            this.highList.add(st.getHistory().get(i).getHigh());
            this.lowList.add(st.getHistory().get(i).getLow());
            this.openList.add(st.getHistory().get(i).getOpen());
            this.volumeList.add(st.getHistory().get(i).getVolume());
        }
        this.from = dates.get(0);
        this.to = dates.get(dates.size() - 1);
    }

    public boolean updateStockData(Calendar newDate) {
        Stock st;
        ArrayList<Calendar> tempDates = new ArrayList<Calendar>();
        ArrayList<BigDecimal> tempAdjCloseList = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> tempCloseList = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> tempHighList = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> tempLowList = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> tempOpenList = new ArrayList<BigDecimal>();
        ArrayList<Long> tempVolumeList = new ArrayList<Long>();
        this.target = 0;

        if (newDate.getTimeInMillis() < from.getTimeInMillis()) {
            try {
                st = YahooFinance.get(ticker, newDate, from, Interval.DAILY);
                for (int i = 0; i < st.getHistory().size(); i++) {
                    tempDates.add(st.getHistory().get(i).getDate());
                    tempAdjCloseList.add(st.getHistory().get(i).getAdjClose());
                    tempCloseList.add(st.getHistory().get(i).getClose());
                    tempHighList.add(st.getHistory().get(i).getHigh());
                    tempLowList.add(st.getHistory().get(i).getLow());
                    tempOpenList.add(st.getHistory().get(i).getOpen());
                    tempVolumeList.add(st.getHistory().get(i).getVolume());
                }
            } catch (IOException e) {
                return false;
            }
            tempDates.addAll(this.dates);
            tempAdjCloseList.addAll(this.adjCloseList);
            tempCloseList.addAll(this.closeList);
            tempHighList.addAll(this.highList);
            tempLowList.addAll(this.lowList);
            tempOpenList.addAll(this.openList);
            tempVolumeList.addAll(this.volumeList);
            this.dates = tempDates;
            this.adjCloseList = tempAdjCloseList;
            this.closeList = tempCloseList;
            this.highList = tempHighList;
            this.lowList = tempLowList;
            this.openList = tempOpenList;
            this.volumeList = tempVolumeList;
            this.from = dates.get(0);

            return true;
        } else if (newDate.getTimeInMillis() > to.getTimeInMillis()) {
            to.add(Calendar.DATE, 1);
            newDate.add(Calendar.DATE, 1);
            try {
                st = YahooFinance.get(ticker, to, newDate, Interval.DAILY);
                for (int i = 0; i < st.getHistory().size(); i++) {
                    tempDates.add(st.getHistory().get(i).getDate());
                    tempAdjCloseList.add(st.getHistory().get(i).getAdjClose());
                    tempCloseList.add(st.getHistory().get(i).getClose());
                    tempHighList.add(st.getHistory().get(i).getHigh());
                    tempLowList.add(st.getHistory().get(i).getLow());
                    tempOpenList.add(st.getHistory().get(i).getOpen());
                    tempVolumeList.add(st.getHistory().get(i).getVolume());
                }
            } catch (IOException e) {
                to.add(Calendar.DATE, -1);
                return false;
            }
            this.dates.addAll(tempDates);
            this.adjCloseList.addAll(tempAdjCloseList);
            this.closeList.addAll(tempCloseList);
            this.highList.addAll(tempHighList);
            this.lowList.addAll(tempLowList);
            this.openList.addAll(tempOpenList);
            this.volumeList.addAll(tempVolumeList);
            this.to = dates.get(dates.size() - 1);
            return true;
        } else {
            return false;
        }
    }

    public int lookUpTargetInDate(Calendar targetDate) {
        return dates.indexOf(targetDate);
    }

    public String getName() {
        return this.name;
    }

    public String getTicker() {
        return this.ticker;
    }

    public int getTarget() {
        return this.target;
    }

    public Calendar getTargetDate(int count) {
        return this.dates.get(target + count);
    }

    public BigDecimal getTargetAdjClose(int count) {
        return this.adjCloseList.get(target + count);
    }

    public BigDecimal getTargetClose(int count) {
        return this.closeList.get(target + count);
    }

    public BigDecimal getTargetHigh(int count) {
        return this.highList.get(target + count);
    }

    public BigDecimal getTargetLow(int count) {
        return this.lowList.get(target + count);
    }

    public BigDecimal getTargetOpen(int count) {
        return this.openList.get(target + count);
    }

    public Long getTargetVolume(int count) {
        return this.volumeList.get(target + count);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Calendar getFromDate() {
        return this.from;
    }

    public Calendar getToDate() {
        return this.to;
    }
    
}
