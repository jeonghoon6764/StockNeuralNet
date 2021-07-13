package com.jlee3688gatech;

import java.io.Serializable;


public class NetworkObject implements Serializable{

    private Learning learning;
    private NeuralNet neuralNet;
    private Double error_rate;
    private Type type;
    private Request_Type requestType;
    
    enum Type {
        Learning_Object, NeuralNet_Object, Error_Rate, Request
    }

    enum Request_Type {
        Learning_Object, NeuralNet_Object
    }
    
    public NetworkObject(Learning learning) {
        this.learning = learning;
        this.type = Type.Learning_Object;
    }

    public NetworkObject(NeuralNet neuralNet) {
        this.neuralNet = neuralNet;
        this.type = Type.NeuralNet_Object;
    }

    public NetworkObject (Double error_rate) {
        this.error_rate = error_rate;
        this.type = Type.Error_Rate;
    }

    public NetworkObject(Request_Type requestType) {
        this.type = Type.Request;
        this.requestType = requestType;
    }

    public Learning getLearning() {
        return this.learning;
    }

    public NeuralNet getNeuralNet() {
        return this.neuralNet;
    }

    public Double getErrRate() {
        return this.error_rate;
    }

    public Type getType() {
        return this.type;
    }

    public Request_Type getRequest_Type() {
        return this.requestType;
    }
    
}
