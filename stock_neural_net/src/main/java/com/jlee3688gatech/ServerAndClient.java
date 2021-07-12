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

public class ServerAndClient {
    private InetAddress localAddress;
    private Socket socket;
    private boolean failToInitialize;
    private boolean isServer;

    public ServerAndClient(boolean isServer) {
        socket = new Socket();
        this.isServer = isServer;
        try {
            localAddress = InetAddress.getLocalHost();
            failToInitialize = false;
        } catch (UnknownHostException e) {
            failToInitialize = true;
        }
        System.out.println(localAddress.toString());
    }

    public void server() throws IOException {
        int portNum = 8888;
        int backLogSize = 5;
        ServerSocket serverSocket = new ServerSocket(portNum, backLogSize);
        System.out.println("socket created");
        System.out.println("portNumber = " + portNum);
        System.out.println("backLogSize = " + backLogSize);

        Socket socket = serverSocket.accept();
        System.out.println("Accept client connection");

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        Scanner sc = new Scanner(in);
        PrintStream ps = new PrintStream(out);

        String str = sc.nextLine();
        ps.println(str);

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
