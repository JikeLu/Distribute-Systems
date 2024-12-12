/**
 * Author: Jike Lu
 * ID: jikelu
 */
package org.example;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import com.google.gson.Gson;

public class NeuralNetworkServer {
    public static void main(String args[]) {
        try (DatagramSocket socket = new DatagramSocket(6789)) {
            System.out.println("Server started");
            byte[] buffer = new byte[1024];
            ArrayList<Double[][]> userTrainingSets = new ArrayList<Double[][]>(Arrays.asList(
                    new Double[][]{{0.0, 0.0}, {0.0}},
                    new Double[][]{{0.0, 1.0}, {0.0}},
                    new Double[][]{{1.0, 0.0}, {0.0}},
                    new Double[][]{{1.0, 1.0}, {0.0}}
            ));
            NeuralNetwork neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
            Gson gson = new Gson();
            Random rand = new Random();
            int random_choice;

            // Hold a list of doubles for input for the neural network to train on.
            // In this example, if we want to train the neural network to learn the XOR,
            // the list would have two doubles, say 0 1 or 1 0 or 1 1.
            List<Double> userTrainingInputs;

            // Hold a list of double for the output of training. For example, XOR would produce 1 double as output.
            List<Double> userTrainingOutputs;
            while(true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                byte[] data = new byte[request.getLength()]; // Correctly size the byte array
                System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
                String requestString = new String(data);
                System.out.println(requestString);
                Message msg = gson.fromJson(requestString,Message.class);
                switch (msg.request) {
                    case "getCurrentRange":
                        double val1 = userTrainingSets.get(0)[1][0];
                        double val2 = userTrainingSets.get(1)[1][0];
                        double val3 = userTrainingSets.get(2)[1][0];
                        double val4 = userTrainingSets.get(3)[1][0];
                        Message msgToSend0 = new Message("getCurrentRange", val1, val2, val3, val4);
                        System.out.println("Received: " + msg.toString(msg.request, false));
                        System.out.println("Sent: " + msgToSend0.toString(msgToSend0.request, true));
                        String messageToSend0 = gson.toJson(msgToSend0);
                        byte[] sendData0 = messageToSend0.getBytes();
                        // Get the client's address and port from the received packet
                        int clientPort0 = request.getPort();
                        // Create DatagramPacket to send the data
                        DatagramPacket sendPacket0 = new DatagramPacket(sendData0, sendData0.length, request.getAddress(), clientPort0);
                        // Send the packet
                        socket.send(sendPacket0);
                        break;

                    case "setCurrentRange":
                        userTrainingSets = new ArrayList<Double[][]>(Arrays.asList(
                                new Double[][]{{0.0, 0.0}, {msg.val1}},
                                new Double[][]{{0.0, 1.0}, {msg.val2}},
                                new Double[][]{{1.0, 0.0}, {msg.val3}},
                                new Double[][]{{1.0, 1.0}, {msg.val4}}
                        ));
                        neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
                        Message msgToSend1 = new Message("setCurrentRange");
                        System.out.println("Received: " + msg.toString(msg.request, false));
                        System.out.println("Sent: " + msgToSend1.toString(msgToSend1.request, true));
                        String messageToSend1 = gson.toJson(msgToSend1);
                        byte[] sendData1 = messageToSend1.getBytes();
                        // Get the client's address and port from the received packet
                        int clientPort1 = request.getPort();
                        // Create DatagramPacket to send the data
                        DatagramPacket sendPacket1 = new DatagramPacket(sendData1, sendData1.length, request.getAddress(), clientPort1);
                        // Send the packet
                        socket.send(sendPacket1);
                        break;

                    case "train":
                        for (int i = 0; i < msg.iterations; i++) {
                            random_choice = rand.nextInt(4);
                            // Get the two inputs
                            userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]);
                            // Get the one output
                            userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]);
                            // Show that row to the neural network
                            neuralNetwork.train(userTrainingInputs, userTrainingOutputs);
                        }
                        Message msgToSend2 = new Message("train", neuralNetwork.calculateTotalError(userTrainingSets));
                        System.out.println("Received: " + msg.toString(msg.request, false));
                        System.out.println("Sent: " + msgToSend2.toString(msgToSend2.request, true));
                        String messageToSend2 = gson.toJson(msgToSend2);
                        byte[] sendData2 = messageToSend2.getBytes();
                        // Get the client's address and port from the received packet
                        int clientPort2 = request.getPort();
                        // Create DatagramPacket to send the data
                        DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, request.getAddress(), clientPort2);
                        // Send the packet
                        socket.send(sendPacket2);
                        break;

                    case "test":
                        List<Double> testUserInputs = new ArrayList<>(Arrays.asList(msg.val1, msg.val2));
                        List<Double> userOutput = neuralNetwork.feedForward(testUserInputs);
                        Message msgToSend3 = new Message("test", userOutput.get(0));
                        System.out.println("Received: " + msg.toString(msg.request, false));
                        System.out.println("Sent: " + msgToSend3.toString(msgToSend3.request, true));
                        String messageToSend3 = gson.toJson(msgToSend3);
                        byte[] sendData3 = messageToSend3.getBytes();
                        // Get the client's address and port from the received packet
                        int clientPort3 = request.getPort();
                        // Create DatagramPacket to send the data
                        DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, request.getAddress(), clientPort3);
                        // Send the packet
                        socket.send(sendPacket3);
                        break;
                }
            }
            // Message incommingMsg = gson.fromJson(someJSON,Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
