package com.jlee3688gatech;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class represents perceptrons layer.
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class PerceptronsLayer implements Serializable{

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
    public PerceptronsLayer(int numOfNode, int numOfInput) {
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
     * feedFoward method.
     * weightsum -> addBias -> sigmoid then
     * store values to the output variable.
     */
    public void feed() {
        ArrayList<Double> weightSum = getWeightSum();
        addBias(weightSum);
        outputArr = sigmoidActivation(weightSum);
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
                sum += perceptronWeightsArr.get(j) * inputArr.get(j);
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

    /**
     * sigmoid function, this will be return sigmoid value from input
     *                    1
     * Equetion == --------------
     *             1 + e^(-value)
     * @param value the input value
     * @return sigmoid value
     */
    private double sigmoidFunction(double value) {
        double child = 1;
        double parent = 1 + Math.pow(Math.E, value * (-1.0));
        return child / parent;
    }

    /**
     * sigmoid deriv function, this will be return sigmoid value from input
     *                 e^(-value)
     * Equetion == ------------------
     *             (1 + e^(-value))^2
     * @param value the input value
     * @return sigmoid deriv value
     */
    private double sigmoidDeriv(Double value) {
        double child = Math.pow(Math.E, value * (-1.0));
        double parent = Math.pow(1.0D + Math.pow(Math.E, value * (-1.0)), 2);
        return child / parent;
    }

    /**
     * perform sigmoid activation on the input arraylist
     * @param  input arraylist
     * @return the ArrayList after we use sigmoid function each entry
     */
    private ArrayList<Double> sigmoidActivation(ArrayList<Double> input) {
        for (int i = 0; i < input.size(); i++) {
            input.set(i, (sigmoidFunction(input.get(i))));
        }
        return input;
    }

    /**
     * perform sigmoid deriv on the input arraylist
     * @return the arraylist after we use sigmoid deriv function.
     */
    public ArrayList<Double> sigmoidActivationDeriv() {
        ArrayList<Double> ws = getWeightSum();
        ArrayList<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < ws.size(); i++) {
            ret.add(i, (sigmoidDeriv(ws.get(i) + biasArr.get(i))));
        }
        return ret;
    }

    /**
     * method for update weight
     * @param learningRate learning rate
     * @param delta delta value.
     */
    public void update(double learningRate, ArrayList<Double> delta) {
        for(int i = 0; i < inputArr.size(); i++) {
            for (int j = 0; j < numOfNode; j++) {
                updateWeight(learningRate, delta.get(j), i, j);
            }
        }
    }

    /**
     * helper method for update weight
     * @param learningRate rate of learning
     * @param delta delta value
     * @param start start from
     * @param end end to
     */
    private void updateWeight(double learningRate, double delta, int start, int end) {
        setWeight(start, end, getWeight(start, end) + delta * inputArr.get(start) * learningRate);
    }

    /**
     * setter for input array.
     * @param input the array of inputs.
     */
    public void setInput(ArrayList<Double> inputArr) {
        this.inputArr = inputArr;
    }

    /**
     * getter for output array.
     * @return output array.
     */
    public ArrayList<Double> getOutput() {
        return this.outputArr;
    }

    /**
     * getter for Number of Node.
     * @return number of node this perceptrons layer have
     */
    public int getNumofNode() {
        return numOfNode;
    }

    /**
     * getter for getting weight list.
     * @param idx node's index number
     * @return ArrayList of node's weight list.
     */
    public ArrayList<Double> getWeightList(int idx) {
        return weightsArr.get(idx);
    }

    /**
     * getter for weight value
     * @param start node that start from
     * @param end node that end to
     * @return value of weigth from weightlist. (prev)start -> "weight" -> (curr)end
     */
    public double getWeight(int start, int end) {
        return weightsArr.get(end).get(start);
    }

    /**
     * setter for weight value
     * @param start node that start from
     * @param end node that end to
     * @param value value of weight from weightList.
     */
    public void setWeight(int start, int end, double value) {
        this.weightsArr.get(end).set(start, value);
    }
}
