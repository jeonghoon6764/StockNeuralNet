package com.jlee3688gatech;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.jlee3688gatech.NetworkObject.Request_Type;

public class Client {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    

    public Client(String serverAddr, int port) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAddr, port);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void requestAssignment() throws IOException, ClassNotFoundException {
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println("Assignment");
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        NetworkObject networkObject = new NetworkObject(Request_Type.Learning_Object);
        objectOutputStream.writeObject(networkObject);
        objectOutputStream.flush();
        System.out.println("Sending Request... LearningObject");
        Learning learning = (Learning)objectInputStream.readObject();
        System.out.println("learning Eror = " + learning.getCurrError());
        objectOutputStream.close();
        objectInputStream.close();
        outputStream.close();
        inputStream.close();
        socket.close();
    }


}
