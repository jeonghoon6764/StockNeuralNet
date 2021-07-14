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
        
        NetworkObject networkObject = new NetworkObject("MAC", "Server", "Obj");
        byte[] bytes = UtilMethods.toByteArray(networkObject);

        outputStream.write(bytes);
        outputStream.flush();
        
        outputStream.close();
        inputStream.close();
        socket.close();
    }


}
