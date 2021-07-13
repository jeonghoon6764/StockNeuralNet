package com.jlee3688gatech;

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
        } catch (Exception e) {}
    }

    private void turnOnServer() throws Exception {
        while(true) {
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                break;
            }

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            scanner = new Scanner(inputStream);
            printStream = new PrintStream(outputStream);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectInputStream = new ObjectInputStream(inputStream);

            NetworkObject networkObject = (NetworkObject) objectInputStream.readObject();

            if (networkObject.getType() == Type.Request && networkObject.getRequest_Type() == Request_Type.Learning_Object) {
                Learning learning = new Learning(null, null);
                learning.setCurrError(123.4);
                objectOutputStream.writeObject(learning);
                objectOutputStream.flush();
            }
        }
        scanner.close();
        printStream.close();
        objectOutputStream.close();
        objectInputStream.close();
        outputStream.close();
        inputStream.close();
        socket.close();
        serverSocket.close();
    }
}
