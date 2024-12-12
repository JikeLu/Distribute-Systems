/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EchoClientUDP {
    public static void main(String args[]) {
        DatagramSocket aSocket = null;
        System.out.println("The UDP client is running."); // Announce client is running
        Scanner scanner = new Scanner(System.in); // Create Scanner object
        System.out.print("Enter server port number: ");
        int serverPort = scanner.nextInt(); // Read server port number from user

        try {
            InetAddress aHost = InetAddress.getByName("localhost");
            aSocket = new DatagramSocket();
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            while ((nextLine = typed.readLine()) != null) {
                byte[] m = nextLine.getBytes();
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                aSocket.send(request);
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                String response = new String(reply.getData(), 0, reply.getLength());
                System.out.println("Reply from server: " + response);
                if (response.trim().equals("halt!")) {
                    System.out.println("UDP Client side quitting");
                    break; // Exit loop to close client
                }
            }

        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
