package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Calendar;

public class NeuralNetSet {
    private ArrayList<NeuralNet> neuralNetList;
    private String name;
    private ArrayList<String[]> log;
    private ArrayList<String> orders;

    public NeuralNetSet(ArrayList<NeuralNet> neuralNetList, String name, ArrayList<String> orders) {
        this.neuralNetList = neuralNetList;
        this.orders = orders;
        this.name = name;
        this.log = new ArrayList<String[]>();
        addLog("NeuralNet set is made.");
    }

    public String getInfoString() {
        //String str = new String();
        //str += "Name: " + name + "\n";
        //str += "Number of NeuralNet: " + neuralNetList.size() + "\n \n";
        //str += "### Stocks included ###";
        //for (int i = 0; i < orders.size(); i++) {
        //    str += "\n" + orders.get(i);
        //}
        //str += "\n\n";
        //str += "### NeuralNet Targets ###\n";
        //for (int i = 0; i < neuralNetList.size(); i++) {
        //    str += "neuralnet" + (i + 1) + ": " + neuralNetList.get(i).getTargetName() + " (" + neuralNetList.get(i).getTargetTicker() + ")\n";
        //}
        //str += "\n### logs ###\n";
        //for (int i = 0; i < log.size(); i++) {
        //    str += log.get(i)[0] + " : " + log.get(i)[1] + "\n";
        //}

        String str = "testNN";

        return str;
    }

    public void addLog(String str) {
        String[] logs = new String[2];
        logs[0] = UtilMethods.CalendarToString(Calendar.getInstance());
        logs[1] = str;
    }

    public ArrayList<String[]> getLog() {
        return this.log;
    }

    public ArrayList<String> getNeuralNetNameList() {
        ArrayList<String> neuralNetNameList = new ArrayList<String>();
        for (int i = 0; i < neuralNetList.size(); i++) {
            neuralNetNameList.add(neuralNetList.get(i).getName());
        }
        return neuralNetNameList;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<NeuralNet> getNeuralNets() {
        return this.neuralNetList;
    }

    public NeuralNetSetOutput getRecentOutputData(RecentInputData recentInput) {
        NeuralNetSetOutput ret = new NeuralNetSetOutput();
        ArrayList<Double> recentInputList = recentInput.getRecentInput();

        for (int i = 0; i < neuralNetList.size(); i++) {
            ArrayList<Double> output = neuralNetList.get(i).feedFoward(recentInputList);
            ret.addOutput(neuralNetList.get(i).getName(), output.get(0), output.get(1));
        }

        return ret;
    }

    
    
}
