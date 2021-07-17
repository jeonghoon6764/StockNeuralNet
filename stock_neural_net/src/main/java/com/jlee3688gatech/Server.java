package com.jlee3688gatech;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.event.SwingPropertyChangeSupport;

public class Server extends Thread{
    private ServerSocket serverSocket;
    private InetAddress localAddress;
    private boolean initFail;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Scanner scanner;
    private PrintStream printStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private ArrayList<String> joinedComputerList;
    private ArrayList<Learning> learningList;
    private Double learningRate;
    private Double minError;
    private Integer maxIteration;
    private ServerFXMLController2 guiController;

    
    public Server(int portNum, int backLogSize) {
        this.joinedComputerList = new ArrayList<>();
        this.joinedComputerList.add("Server");
        try {
            this.localAddress = InetAddress.getLocalHost();
            this.serverSocket = new ServerSocket(portNum, backLogSize);
            this.initFail = false;
        } catch (Exception e) {
            this.initFail = true;
        }
    }

    public void run() {
        try {
            turnOnServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setGUIController(ServerFXMLController2 serverFXMLController2) {
        this.guiController = serverFXMLController2;
    }

    public void setLeatningRate(double d) {
        this.learningRate = d;
    }

    public void setminError(double d) {
        this.minError = d;
    }

    public void setMaxIteration(int i) {
        this.maxIteration = i;
    }

    public synchronized ArrayList<String> getOrAddJoinedComputerList(String name) {
        if (name != null) {
            if (!joinedComputerList.contains(name)) {
                joinedComputerList.add(name);
            }
        }
        return joinedComputerList;
    }

    private void turnOnServer() throws Exception {
        while(true) {
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                System.out.println("error occured");
                break;
            }
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            byte[] datas = UtilMethods.readAllByteFromInputStream(inputStream);
            
            System.out.println("bytes received");
            NetworkObject message = UtilMethods.toObject(datas, NetworkObject.class);

            System.out.println("from : " + message.getMessageFrom());
            System.out.println("to : " + message.getMessageTo());
            System.out.println("time : " + UtilMethods.CalendarToTimeString(message.getCreatedTime()));
            System.out.println("String : " + message.getString());

            if (message.getString().equals("JOIN")) {
                getOrAddJoinedComputerList(message.getMessageFrom());
                guiController.updateJoinedComputerTextField();
                Boolean trueWrapper = true;
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), trueWrapper, "SUCCESS");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST PREQ / LEARNING")) {
                Double learningRateWrapper = learningRate;
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), learningRateWrapper, "Learning Rate");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST PREQ / MINIMUM ERROR")) {
                Double minErrorWrapper = minError;
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), minErrorWrapper, "Minimum Error");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST PREQ / MAX ITERATION")) {
                Integer maxIterWrapper = maxIteration;
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), maxIterWrapper, "MAX Iteration");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST STATUS")) {
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), guiController.getStatusGUIArrayList(), "Status");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST JOINED COMPUTER")) {
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), getOrAddJoinedComputerList(null), "Joined ComputerList");
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("REQUEST ASSIGNMENT")) {
                int idx = guiController.getNetxtReadyLearningSetIndex();
                NetworkObject msg;

                if (idx != -1) {
                    msg = new NetworkObject("Server", message.getMessageFrom(), guiController.getLearningSet().get(idx), "Learning");
                } else {
                    msg = new NetworkObject("Server", message.getMessageFrom(), null, "Nothing");
                }
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                System.out.println("ByteLength == " + msgBytes.length);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getString().equals("ERROR STATUS")) {
                ErrorInfo errorInfo = message.getObject(ErrorInfo.class);
                int idx = 0;
                for (int i = 0; i < guiController.getLearningSet().size(); i++) {
                    if (guiController.getLearningSet().get(i).getNeuralNetTarget().equals(errorInfo.getName())) {
                        idx = i;
                        break;
                    }
                }
                guiController.setErrorRate(idx, errorInfo.getErrorRate());
            } else if (message.getString().equals("NEURAL NET")) {
                NeuralNet neuralNet = message.getObject(NeuralNet.class);
                int idx = 0;
                for (int i = 0; i < guiController.getLearningSet().size(); i++) {
                    if (guiController.getLearningSet().get(i).getNeuralNetTarget().equals(neuralNet.getTargetTicker())) {
                        idx = i;
                        break;
                    }
                }
                guiController.getLearningSet().get(idx).setNeuralNet(neuralNet);
                guiController.notifyFinishWork(idx);
            }
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        serverSocket.close();
    }

    public boolean getInitFail() {
        return this.initFail;
    }
}
