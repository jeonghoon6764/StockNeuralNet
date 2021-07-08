package com.jlee3688gatech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class NeuralNetSet implements Serializable {
    private ArrayList<NeuralNet> neuralNetList;
    private String name;
    private int numOfInputDate;
    private int numOfDateOutput;
    private int minIncreaseDate;
    private double rateOfTotalIncrease;
    private ArrayList<String> referenceList;
    private ArrayList<String[]> logs;
    private ArrayList<String> orders;
    private ArrayList<String[]> notes;

    public NeuralNetSet(ArrayList<NeuralNet> neuralNetList, String name, ArrayList<String> orders, int numOfInputDate
    , int numOfDateOutput, int minIncreaseDate, double rateOfTotalIncrease, ArrayList<String> referenceList) {
        this.referenceList = referenceList;
        this.numOfInputDate = numOfInputDate;
        this.numOfDateOutput = numOfDateOutput;
        this.minIncreaseDate = minIncreaseDate;
        this.rateOfTotalIncrease = rateOfTotalIncrease;
        this.neuralNetList = neuralNetList;
        this.orders = orders;
        this.name = name;
        this.logs = new ArrayList<String[]>();
        this.notes = new ArrayList<>();
        addLog("NeuralNet set is made.");
    }

    public String getInfoString() {
        String str = new String();
        str += "Name: " + name + "\n \n";
        str += "Number of NeuralNet: " + neuralNetList.size() + "\n \n";
        str += "Number of Input Date: " + numOfInputDate + "\n \n";
        str += "Number of Hidden layers: " + neuralNetList.get(0).getNumOfHiddenLayer() + "\n \n";
        str += "Number of date to calculate output: " + numOfDateOutput + "\n \n";
        str += "Minimum Increase Date: " + minIncreaseDate + "\n \n";
        str += "Total Increase Rate: " + rateOfTotalIncrease + "\n \n";


        str += "### reference ###";
        for (int i = 0; i < referenceList.size(); i++) {
            str += "\n" + referenceList.get(i);
        }
        str += "\n\n";

        str += "### Stocks included ###";
        for (int i = 0; i < orders.size(); i++) {
            str += "\n" + orders.get(i);
            for (int j = 0; j < neuralNetList.size(); j++) {
                if (neuralNetList.get(j).getTargetTicker().equals(orders.get(i))) {
                    str += "<TARGET>";
                    break;
                }
            }
        }
        if (notes.size() > 0) {
            str += "\n\n### notes ###\n";
            for (int i = 0; i < notes.size(); i++) {
                str += notes.get(i)[0] + " : " + notes.get(i)[1] + "\n";
            }
        }
        
        str += "\n### logs ###\n";
        for (int i = 0; i < logs.size(); i++) {
            str += logs.get(i)[0] + " : " + logs.get(i)[1] + "\n";
        }

        return str;
    }

    public void addLog(String str) {
        String[] log = new String[2];
        log[0] = UtilMethods.CalendarToString(Calendar.getInstance());
        log[1] = str;
        logs.add(log);
    }

    public void addNote(String str) {
        String[] note = new String[2];
        note[0] = UtilMethods.CalendarToString(Calendar.getInstance());
        note[1] = str;
        notes.add(note);
    }

    public int getNumOfInputDate() {
        return this.numOfInputDate;
    }

    public int getNumOfDateOutput() {
        return this.numOfDateOutput;
    }

    public int getMinIncreaseDate() {
        return this.minIncreaseDate;
    }

    public ArrayList<String> getReferenceList() {
        return this.referenceList;
    }

    public double getRateOfTotalIncrease() {
        return this.rateOfTotalIncrease;
    }

    public ArrayList<String[]> getLogs() {
        return this.logs;
    }

    public ArrayList<String[]> getNotes() {
        return this.notes;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<NeuralNet> getNeuralNets() {
        return this.neuralNetList;
    }

    public ArrayList<String> getOrders() {
        return this.orders;
    }

    public NeuralNetSetOutput getRecentOutputData(RecentInputData recentInput) {
        NeuralNetSetOutput ret = new NeuralNetSetOutput();
        ArrayList<Double> recentInputList = recentInput.getRecentInput();

        for (int i = 0; i < neuralNetList.size(); i++) {
            ArrayList<Double> output = neuralNetList.get(i).feedFoward(recentInputList);
            ret.addOutput(neuralNetList.get(i).getTargetTicker(), output.get(0), output.get(1));
        }

        return ret;
    }

    
    
}
