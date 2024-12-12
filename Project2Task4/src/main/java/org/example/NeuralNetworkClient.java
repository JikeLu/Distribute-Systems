/**
 * Author: Jike Lu
 * ID: jikelu
 */


package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import com.google.gson.Gson;
public class NeuralNetworkClient {

    public static void main(String[] args) throws Exception {
        Gson gson = new Gson();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("The client is running.");
        System.out.print("Please enter server port: ");
        int serverPort = Integer.parseInt(reader.readLine());
        InetAddress address = InetAddress.getByName("localhost");

        while (true) {
            System.out.println("Using a neural network to learn a truth table.\nMain Menu");
            System.out.println("0. Display the current range.");
            System.out.println("1. Provide four inputs for the range of the two input truth table and build a new neural network. To test XOR, enter 0  1  1  0.");
            System.out.println("2. Perform a single training step.");
            System.out.println("3. Perform n training steps. 10000 is a typical value for n.");
            System.out.println("4. Test with a pair of inputs.");
            System.out.println("5. Exit program.");
            int choice = Integer.parseInt(reader.readLine());
            if (choice == 5) {
                System.out.println("Client side quitting.");
                break;
            }

            try (DatagramSocket socket = new DatagramSocket()) {
                if (choice == 0) {
                    Message msg = new Message("getCurrentRange");
                    String messageToSend = gson.toJson(msg);
                    byte[] buffer = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                    socket.send(packet);

                    byte[] m = new byte[2000];
                    DatagramPacket reply = new DatagramPacket(m, m.length);
                    socket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());
                    Message msgResponse = gson.fromJson(response, Message.class);
                    System.out.println("The current range is " + msgResponse.val1 + " " + msgResponse.val2 + " " + msgResponse.val3 + " " + msgResponse.val4);
                } else if (choice == 1) {
                    System.out.println("Enter the four results of a 4 by 2 truth table. Each value should be 0 or 1.");
                    String input = reader.readLine();
                    String[] parts = input.split(" ");
                    Double val1 = Double.parseDouble(parts[0]);
                    Double val2 = Double.parseDouble(parts[1]);
                    Double val3 = Double.parseDouble(parts[2]);
                    Double val4 = Double.parseDouble(parts[3]);
                    Message msg = new Message("setCurrentRange", val1, val2, val3, val4);
                    String messageToSend = gson.toJson(msg);
                    byte[] buffer = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                    socket.send(packet);

                    byte[] m = new byte[2000];
                    DatagramPacket reply = new DatagramPacket(m, m.length);
                    socket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());
                    Message msgResponse = gson.fromJson(response, Message.class);
                    System.out.println("Set successfully");
                } else if (choice == 2) {
                    Message msg = new Message("train", 1);
                    String messageToSend = gson.toJson(msg);
                    byte[] buffer = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                    socket.send(packet);

                    byte[] m = new byte[2000];
                    DatagramPacket reply = new DatagramPacket(m, m.length);
                    socket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());
                    Message msgResponse = gson.fromJson(response, Message.class);
                    System.out.println("After 1 training step, our error is " + msgResponse.val1);
                } else if (choice == 3) {
                    System.out.println("Enter iterations.");
                    int iterations = Integer.parseInt(reader.readLine());
                    Message msg = new Message("train", iterations);
                    String messageToSend = gson.toJson(msg);
                    byte[] buffer = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                    socket.send(packet);

                    byte[] m = new byte[2000];
                    DatagramPacket reply = new DatagramPacket(m, m.length);
                    socket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());
                    Message msgResponse = gson.fromJson(response, Message.class);
                    System.out.println("After " + iterations + " training steps, our error is " + msgResponse.val1);
                } else if (choice == 4) {
                    System.out.println("Enter a pair of doubles from a row of the truth table. These are domain values.");
                    String input = reader.readLine();
                    String[] parts = input.split(" ");
                    Double val1 = Double.parseDouble(parts[0]);
                    Double val2 = Double.parseDouble(parts[1]);
                    Message msg = new Message("test", val1, val2);
                    String messageToSend = gson.toJson(msg);
                    byte[] buffer = messageToSend.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                    socket.send(packet);

                    byte[] m = new byte[2000];
                    DatagramPacket reply = new DatagramPacket(m, m.length);
                    socket.receive(reply);
                    String response = new String(reply.getData(), 0, reply.getLength());
                    Message msgResponse = gson.fromJson(response, Message.class);
                    System.out.println("The range value is approximately " + msgResponse.val1);
                }
            }
        }
    }

}
