/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class EchoServerUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null;
        System.out.println("The UDP server is running."); // Announce server is running
        Scanner scanner = new Scanner(System.in); // Create Scanner object
        System.out.print("Enter port number: ");
        int port = scanner.nextInt(); // Read port number from user
        byte[] buffer = new byte[1000];
        try{
            aSocket = new DatagramSocket(port);
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                byte[] data = new byte[request.getLength()]; // Correctly size the byte array
                System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
                String requestString = new String(data);
                System.out.println("Echoing: "+requestString);
                if(requestString.trim().equals("halt!")){
                    DatagramPacket reply = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
                    aSocket.send(reply);
                    System.out.println("UDP Server side quitting");
                    break; // Exit loop to close server
                }
                DatagramPacket reply = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}
