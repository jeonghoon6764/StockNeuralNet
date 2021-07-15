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

    
    public Server(int portNum, int backLogSize) {
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

            if (message.getObject(String.class).equals("JOIN / REQUEST REQUIREMENT")) {

            } else if (message.getObject(String.class).equals("REQUEST ASSIGNMENT")) {

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
