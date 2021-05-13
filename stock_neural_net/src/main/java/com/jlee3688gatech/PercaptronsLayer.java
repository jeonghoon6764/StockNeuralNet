package com.jlee3688gatech;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents perceptrons layer.
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class PercaptronsLayer {

    ArrayList<Double> inputArr;
    ArrayList<Double> outputArr;
    ArrayList<ArrayList<Double>> weightsArr;
    ArrayList<Double> biasArr;
    int numOfNode;

    /**
     * Constructor for perceptronsLayer class.
     * it initializes the each weight with random values
     * and set bias to 1 to all.
     * @param numOfNode number of node we build.
     * @param numOfInput number of input this will get.
     */
    public PercaptronsLayer(int numOfNode, int numOfInput) {
        this.numOfNode = numOfNode;
        weightsArr = new ArrayList<ArrayList<Double>>();
        biasArr = new ArrayList<Double>();

        for (int i = 0; i < numOfNode; i++) {
            ArrayList<Double> perceptronWeightsList = new ArrayList<Double>();
            for (int j = 0; j < numOfInput; j++) {
                perceptronWeightsList.add(getRandomDouble());
            }
            weightsArr.add(perceptronWeightsList);
        }
        for (int i = 0; i < numOfNode; i++) {
            biasArr.add(1.0D);
        }
    }

    /**
     * Helper method to get the random double value.
     * This method will return the double value with
     * range from -1 to 1.
     * @return random double number from -1 to 1.
     */
    private double getRandomDouble() {
        Random randomGenerator = new Random();
        double ret = randomGenerator.nextDouble();
        ret *= 2;
        return --ret;
    }

    /**
     * Helper method to get sum of weights arrays x input arrays.
     * @return arrayList of sum values.
     */
    private ArrayList<Double> getWeightSum() {
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < weightsArr.size(); i++) {
            ArrayList<Double> perceptronWeightsArr = weightsArr.get(i);
            Double sum = 0.0D;
            for (int j = 0; j < perceptronWeightsArr.size(); j++) {
                sum += perceptronWeightsArr.get(j) * input.get(j);
            }
            ret.add(i, sum);
        }
        return ret;
    }

    /**
     * Helper method to add bias into each input arrays
     * @param inputArr input ArrayList.
     */
    private void addBias (ArrayList<Double> inputArr) {
        for (int i = 0; i < inputArr.size(); i++) {
            inputArr.set(i, (inputArr.get(i) + biasArr.get(i)));
        }
    }
}
