/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EavesdropperUDP {
    public static void main(String args[]) {
        DatagramSocket serverSocket = null;
        DatagramSocket clientSocket = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("The Eavesdropper UDP is running.");

        System.out.print("Enter the port to listen on: ");
        int listenPort = scanner.nextInt(); // Eavesdropper listening port

        System.out.print("Enter the server port number to forward messages to: ");
        int serverPort = scanner.nextInt(); // Real server port

        try {
            serverSocket = new DatagramSocket(listenPort);
            InetAddress serverAddress = InetAddress.getByName("localhost");

            while(true) {
                byte[] receiveBuffer = new byte[1000];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket); // Receive packet from client

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Message received: " + receivedMessage);

                // Check for the "like" to "dislike" modification requirement
                if(receivedMessage.contains("like")) {
                    receivedMessage = receivedMessage.replaceFirst("like", "dislike");
                }

                // Forwarding the possibly modified message to the server
                byte[] sendBuffer = receivedMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
                clientSocket = new DatagramSocket();
                clientSocket.send(sendPacket);

                // Receive response from the server
                byte[] responseBuffer = new byte[1000];
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
                clientSocket.receive(responsePacket);
                String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
                System.out.println("Response from server: " + responseMessage);

                // Send response back to the client
                serverSocket.send(new DatagramPacket(responseBuffer, responsePacket.getLength(), receivePacket.getAddress(), receivePacket.getPort()));

                if(receivedMessage.trim().equals("halt!")) {
                    break; // Eavesdropper stops forwarding but itself does not halt
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if(serverSocket != null) serverSocket.close();
            if(clientSocket != null) clientSocket.close();
        }
    }
}
