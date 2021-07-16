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
import java.util.ArrayList;

public class Client {
    private String serverAddr;
    private int port;
    private String sender;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    public Client(String serverAddr, int port, String sender) throws UnknownHostException, IOException {
        this.serverAddr = serverAddr;
        this.port = port;
        this.sender = sender;
    }

    public boolean requestJoin() throws IOException {
        Boolean success = sendAndReceiveFromServer("JOIN", Boolean.class);
        return success;
    }

    public Double requestLearningRate() throws IOException {
        Double learningRate = sendAndReceiveFromServer("REQUEST PREQ / LEARNING", Double.class);
        return learningRate;
    }

    public Double requestMinimumError() throws IOException {
        Double minError = sendAndReceiveFromServer("REQUEST PREQ / MINIMUM ERROR", Double.class);
        return minError;
    }

    public Integer requestMaxIteration() throws IOException {
        Integer maxIteration = sendAndReceiveFromServer("REQUEST PREQ / MAX ITERATION", Integer.class);
        return maxIteration;
    }

    public ArrayList<String> requestStatusList() throws IOException {
        ArrayList<String> ret = sendAndReceiveFromServer("REQUEST STATUS", ArrayList.class);
        return ret;
    }

    public ArrayList<String> requestJoinedComputerList() throws IOException {
        ArrayList<String> ret = sendAndReceiveFromServer("REQUEST JOINED COMPUTER", ArrayList.class);
        return ret;
    }

    public void closeAll() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            socket = null;
            inputStream = null;
            outputStream = null;
        } catch (Exception e) {}
    }

    private <T> T sendAndReceiveFromServer(String requestMsg, Class<T> receiveType) throws IOException {
        socket = null;
        inputStream = null;
        outputStream = null;

        NetworkObject networkObject = new NetworkObject(sender, "Server", null, requestMsg);
        byte[] bytes = UtilMethods.toByteArray(networkObject);

        try {
            socket = new Socket(serverAddr, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            
            byte[] receiveMessage = UtilMethods.readAllByteFromInputStream(inputStream);
            NetworkObject receivedNetworkObject = UtilMethods.toObject(receiveMessage, NetworkObject.class);
            T returnObject = receivedNetworkObject.getObject(receiveType);

            return returnObject;
        } catch (Exception e) {
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }

            socket = null;
            inputStream = null;
            outputStream = null;
        }

        return null;
    }

    private void sendToServer(Object obj, String str) throws IOException {
        socket = null;
        inputStream = null;
        outputStream = null;

        NetworkObject networkObject = new NetworkObject(sender, "Server", obj, str);
        byte[] bytes = UtilMethods.toByteArray(networkObject);

        try {
            socket = new Socket(serverAddr, port);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (Exception e) {
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            socket = null;
            inputStream = null;
            outputStream = null;
        }
    }

}
