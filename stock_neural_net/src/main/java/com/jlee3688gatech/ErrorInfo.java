package com.jlee3688gatech;

import java.io.Serializable;

public class ErrorInfo implements Serializable{

    private double errorRate;
    private String name;

    public ErrorInfo(double errorRate, String name) {
        this.errorRate = errorRate;
        this.name = name;
    }

    public double getErrorRate() {
        return this.errorRate;
    }

    public String getName() {
        return this.name;
    }
    
}
