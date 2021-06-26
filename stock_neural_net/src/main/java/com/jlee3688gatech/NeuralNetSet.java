package com.jlee3688gatech;

import java.util.ArrayList;

public class NeuralNetSet {
    private ArrayList<NeuralNet> neuralNetSet;

    public NeuralNetSet(ArrayList<NeuralNet> neuralNetSet) {
        this.neuralNetSet = neuralNetSet;
    }


    public ArrayList<String> getNeuralNetNameList() {
        ArrayList<String> neuralNetNameList = new ArrayList<String>();
        for (int i = 0; i < neuralNetSet.size(); i++) {
            neuralNetNameList.add(neuralNetSet.get(i).getName());
        }
        return neuralNetNameList;
    }

    public ArrayList<NeuralNet> getNeuralNets() {
        return this.neuralNetSet;
    }

    public NeuralNetSetOutput getRecentOutputData(RecentInputData recentInput) {
        NeuralNetSetOutput ret = new NeuralNetSetOutput();
        ArrayList<Double> recentInputList = recentInput.getRecentInput();

        for (int i = 0; i < neuralNetSet.size(); i++) {
            ArrayList<Double> output = neuralNetSet.get(i).feedFoward(recentInputList);
            ret.addOutput(neuralNetSet.get(i).getName(), output.get(0), output.get(1));
        }

        return ret;
    }
    
}
