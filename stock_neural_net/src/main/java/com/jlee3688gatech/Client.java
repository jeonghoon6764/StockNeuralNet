package com.jlee3688gatech;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
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
        Boolean success = connectWithServer("JOIN", Boolean.class, null, null, true);
        return success;
    }

    public Double requestLearningRate() throws IOException {
        Double learningRate = connectWithServer("REQUEST PREQ / LEARNING", Double.class, null, null, true);
        return learningRate;
    }

    public Double requestMinimumError() throws IOException {
        Double minError = connectWithServer("REQUEST PREQ / MINIMUM ERROR", Double.class, null, null, true);
        return minError;
    }

    public Integer requestMaxIteration() throws IOException {
        Integer maxIteration = connectWithServer("REQUEST PREQ / MAX ITERATION", Integer.class, null, null, true);
        return maxIteration;
    }

    public ArrayList<String> requestStatusList() throws IOException {
        ArrayList<String> ret = connectWithServer("REQUEST STATUS", ArrayList.class, null, null, true);
        return ret;
    }

    public ArrayList<String> requestJoinedComputerList() throws IOException {
        ArrayList<String> ret = connectWithServer("REQUEST JOINED COMPUTER", ArrayList.class, null, null, true);
        return ret;
    }

    public Learning requestNextAssignment() throws IOException {
        Learning ret = connectWithServer("REQUEST ASSIGNMENT", Learning.class, null, null, true);
        return ret;
    }

    public void sendStatus(String name, Double errorRate) throws IOException {
        ErrorInfo errorInfo = new ErrorInfo(errorRate, name);
        connectWithServer(null, null, errorInfo, "ERROR STATUS", false);
    }

    public void sendFinishedAssignment(NeuralNet neuralNet) throws IOException {
        connectWithServer(null, null, neuralNet, "NEURAL NET", false);
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

    private synchronized <T> T connectWithServer(String requestMsg, Class<T> receiveType, Object obj, String str, boolean receive) throws IOException {
        if (receive) {
            return sendAndReceiveFromServer(requestMsg, receiveType);
        } else {
            sendToServer(obj, str);
            return null;
        }
    }

    private <T> T sendAndReceiveFromServer(String requestMsg, Class<T> receiveType) throws IOException {
        socket = null;
        inputStream = null;
        outputStream = null;

        NetworkObject networkObject = new NetworkObject(sender, "Server", null, requestMsg);
        byte[] bytes = UtilMethods.toByteArray(networkObject);

        try {
            socket = new Socket(serverAddr, port);
            
            outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            
            socket.shutdownOutput();//save my lifr :)

            System.out.println("Success to send message.");
            inputStream = socket.getInputStream();
            System.out.println("Ready to receive bytes from server.");
            byte[] receiveMessage = UtilMethods.readAllByteFromInputStream(inputStream);
            NetworkObject receivedNetworkObject = UtilMethods.toObject(receiveMessage, NetworkObject.class);
            T returnObject = receivedNetworkObject.getObject(receiveType);

            System.out.println("Receiving success.");

            return returnObject;
        } catch (Exception e) {
            e.printStackTrace();
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
            socket.shutdownOutput();

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
