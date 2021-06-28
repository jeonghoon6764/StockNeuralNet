package com.jlee3688gatech;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class represents Neural Net.
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class NeuralNet implements Serializable{

    
    
    private ArrayList<PerceptronsLayer> NN;
    private ArrayList<ArrayList<Double>> deltas;
    private String targetName;
    private String targetTicker;
    private int inputSize;
    private int outputSize;
    private int[] numOfHiddenLayerNodeArr;

    
    /**
     * Constructor for Neural Net class.
     * @param name name of this Neural Network.
     * @param inputSize size of input in Neural Networks.
     * @param outputSize size of output
     * @param numOfHiddenLayerNodeArr number of nodes in hidden layers.
     * @param targetName target stock's name.
     * @param targetTicker target stock's ticker.
     */
    public NeuralNet(int inputSize, int outputSize, int[] numOfHiddenLayerNodeArr, String targetName, String targetTicker) {

        this.targetName = targetName;
        this.targetTicker = targetTicker;
        
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.numOfHiddenLayerNodeArr = numOfHiddenLayerNodeArr;

        int[] numOfInputArr = new int[numOfHiddenLayerNodeArr.length + 1];

        for (int i = 0; i < numOfHiddenLayerNodeArr.length + 1; i++) {
            if (i == 0) {
                numOfInputArr[i] = inputSize;
            } else {
                numOfInputArr[i] = numOfHiddenLayerNodeArr[i - 1];
            }
        }
            
        NN = new ArrayList<PerceptronsLayer>();
        for (int i = 0; i < numOfHiddenLayerNodeArr.length; i++) {
            NN.add(new PerceptronsLayer(numOfHiddenLayerNodeArr[i], numOfInputArr[i]));
        }
        NN.add(new PerceptronsLayer(outputSize, numOfInputArr[numOfInputArr.length - 1]));
    }

    /**
     * method for feed foward
     * @param input input of Neural Net
     * @return output of Neural Net
     */
    public ArrayList<Double> feedFoward(ArrayList<Double> input) {
        NN.get(0).setInput(input);
        for (int i = 0; i < NN.size() - 1; i++) {
            NN.get(i).feed();
            NN.get(i + 1).setInput(NN.get(i).getOutput());
        }
        NN.get(NN.size() - 1).feed();
        return NN.get(NN.size() - 1).getOutput();
    }

    /**
     * method for back propagation learning.
     * @param expectOutput expectOutput from example
     * @param learningRate learning Rate
     * @return avg error from learning.
     */
    public double backPropLearning(ArrayList<Double> expectOutput, double learningRate) {
        
        this.deltas = new ArrayList<ArrayList<Double>>();
        double avgError = 0.0D;
        ArrayList<ArrayList<Double>> deltas = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> gPrimes = NN.get(NN.size() - 1).sigmoidActivationDeriv();
        ArrayList<Double> errors = new ArrayList<Double>();

        for (int i = 0; i < this.outputSize; i++) {
            double error = expectOutput.get(i) - NN.get(NN.size() - 1).getOutput().get(i);
            errors.add(i, error);
            avgError += Math.abs(error);           
        }
        ArrayList<Double> gPrimexErrors = new ArrayList<Double>();
        for (int i = 0; i < outputSize; i++) {
            gPrimexErrors.add(i, gPrimes.get(i) * errors.get(i));
        }
        deltas.add(0, gPrimexErrors);

        for (int i = NN.size() - 2; i >= 0; i--) {
            gPrimes = NN.get(i).sigmoidActivationDeriv();
            ArrayList<Double> delta = new ArrayList<Double>();
            for (int j = 0; j < NN.get(i).numOfNode; j++) {
                double weightDeltaSum = 0;
                for (int k = 0; k < NN.get(i + 1).numOfNode; k++) {
                    PerceptronsLayer temp = NN.get(i + 1);
                    weightDeltaSum += temp.getWeightList(k).get(j) * deltas.get(0).get(k);
                }
                delta.add(j, weightDeltaSum * gPrimes.get(j));
            }
            deltas.add(0, delta);
        }

        for (int i = 0; i < deltas.size(); i++) {
            NN.get(i).update(learningRate, deltas.get(i));
        }
        return avgError / this.outputSize;
    }

    /**
     * getter method for inputSize
     * @return inputSize
     */
    public int getInputSize() {
        return this.inputSize;
    }

    /**
     * getter method for outputSize
     * @return outputSize
     */
    public int getOutputSize() {
        return this.outputSize;
    }

    /**
     * getter for targetName
     * @return targetName
     */
    public String getTargetName() {
        return this.targetName;
    }

    /**
     * getter for targetTicker
     * @return targetTicker
     */
    public String getTargetTicker() {
        return this.targetTicker;
    }


    /**
     * helper method for return node nums in each layers.
     * nodeInfo[0] = inputSize, nodeInfo[1] = hidden layer 1's node number
     * nodeInfo[2] = hidden layer 2's node number ....
     * nodeInfo[nodeInfo.len - 1] = output node numeber.
     * @return nodeInfo (see above)
     */
    public int[] getNodesInfo() {
        int[] nodeInfo = new int[numOfHiddenLayerNodeArr.length + 2];
        nodeInfo[0] = this.inputSize;
        for (int i = 0; i < numOfHiddenLayerNodeArr.length; i++) {
            nodeInfo[i + 1] = numOfHiddenLayerNodeArr[i];
        }
        nodeInfo[nodeInfo.length - 1] = this.outputSize;
        return nodeInfo;
    }
}

