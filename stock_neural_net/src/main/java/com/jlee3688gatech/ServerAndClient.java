package com.jlee3688gatech;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.jlee3688gatech.RunController.Output;

public class ServerAndClient extends Thread{
    private InetAddress localAddress;
    private Socket socket;
    private boolean failToInitialize;
    private boolean isServer;
    private boolean serverOn;
    private int portNum;

    public ServerAndClient(boolean isServer) {
        this.isServer = isServer;
        try {
            localAddress = InetAddress.getLocalHost();
            failToInitialize = false;
        } catch (UnknownHostException e) {
            failToInitialize = true;
        }
        System.out.println(localAddress.toString());
    }

    public synchronized boolean getOrSetServerOn(Boolean serverOn) {
        if (serverOn != null) {
            this.serverOn = serverOn;
        } else if (serverOn.booleanValue() == false) {
            try {
                socket.close();
            } catch (IOException e) {}
        }
        return this.serverOn;
    }

    public void server(int portNum, int backLogSize) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNum, backLogSize);
        System.out.println("socket created");
        System.out.println("portNumber = " + portNum);
        System.out.println("backLogSize = " + backLogSize);

        InputStream in;
        OutputStream out;
        Scanner sc;
        PrintStream ps;

        while(getOrSetServerOn(null)) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {}
            System.out.println("Accept client connection");
    
            in = socket.getInputStream();
            out = socket.getOutputStream();
    
            sc = new Scanner(in);
            ps = new PrintStream(out);
    
            String str = sc.nextLine();
    
            System.out.println(str);
            ps.println(str);
        }
        
        in.close();
        sc.close();
        out.close();
        ps.close();
        socket.close();
        serverSocket.close();
    }

    public void client(String serverAddr, int port) throws UnknownHostException, IOException {
        Socket socket = new Socket(serverAddr, port);
        System.out.println("Success to make socket Object");

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Scanner sc = new Scanner(in);
        PrintStream ps = new PrintStream(out);

        System.out.println("ready to use input/output stream");
        ps.println("This from client.");
        System.out.println("message send complete.");

        String response = sc.nextLine();
        System.out.println("get message from server\n message: " + response);

        sc.close();
        in.close();
        ps.close();
        out.close();
        socket.close();
    }


}
