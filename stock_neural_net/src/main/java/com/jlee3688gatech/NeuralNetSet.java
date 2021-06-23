package com.jlee3688gatech;

import java.util.ArrayList;

public class NeuralNetSet {
    private ArrayList<NeuralNet> neuralNetSet;
    private String setName;

    public NeuralNetSet(String name) {
        this.neuralNetSet = new ArrayList<NeuralNet>();
        this.setName = name;
    }

    public boolean addNeuralNets(ArrayList<NeuralNet> neuralNets) {
        boolean ret = true;
        if (neuralNets.size() <= 1) {
            return true;
        }
        int[] firstNeuralNetSize = neuralNets.get(0).getNodesInfo();
        for (int i = 1; i < neuralNets.size(); i++) {
            if (neuralNets.get(i).getNodesInfo() != firstNeuralNetSize) {
                ret = false;
            }
        }

        if (ret) {
            this.neuralNetSet.addAll(neuralNets);
            return true;
        }
        return false;
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

    public String getName() {
        return this.setName;
    }
    
}
