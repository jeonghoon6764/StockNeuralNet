package com.jlee3688gatech;

import java.io.BufferedInputStream;
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
            NetworkObject message = UtilMethods.toObject(datas, NetworkObject.class);

            System.out.println("from : " + message.getMessageFrom());
            System.out.println("to : " + message.getMessageTo());
            System.out.println("time : " + UtilMethods.CalendarToTimeString(message.getCreatedTime()));
            System.out.println("String obj : " + message.getObject(String.class));

            if (message.getObject(String.class).equals("JOIN")) {
                getOrAddJoinedComputerList(message.getMessageFrom());
                guiController.updateJoinedComputerTextField();
            } else if (message.getObject(String.class).equals("REQUEST PREQ / LEARNING")) {
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), learningRate);
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getObject(String.class).equals("REQUEST PREQ / MINIMUM ERROR")) {
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), minError);
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getObject(String.class).equals("REQUEST PREQ / MAX ITERATION")) {
                NetworkObject msg = new NetworkObject("Server", message.getMessageFrom(), maxIteration);
                byte[] msgBytes = UtilMethods.toByteArray(msg);
                outputStream.write(msgBytes);
                outputStream.flush();
            } else if (message.getObject(String.class).equals("REQUEST STATUS")) {
                
            } else if (message.getObject(String.class).equals("SEND STATUS")) {
                
            } else if (message.getObject(String.class).equals("READY TO SEND WORKS")) {
                
            }

        }
        outputStream.close();
        inputStream.close();
        socket.close();
        serverSocket.close();
    }

    public boolean getInitFail() {
        return this.initFail;
    }
}
