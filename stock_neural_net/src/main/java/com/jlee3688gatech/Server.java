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

import com.jlee3688gatech.NetworkObject.Request_Type;
import com.jlee3688gatech.NetworkObject.Type;

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

            //System.out.println(localAddress.toString());
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            byte[] datas = UtilMethods.readAllByteFromInputStream(inputStream);
            
            String message = UtilMethods.toObject(datas, String.class);

            System.out.println(message);

        }
        outputStream.close();
        inputStream.close();
        socket.close();
        serverSocket.close();
    }
}
