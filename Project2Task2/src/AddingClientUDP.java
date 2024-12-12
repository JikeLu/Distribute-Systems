/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AddingClientUDP {
    public static void main(String[] args) throws Exception {
        System.out.println("The client is running.");
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please enter server port: ");
        int serverPort = Integer.parseInt(userInput.readLine().trim());
        String inputLine;

        while (true) {
            System.out.println("Enter a number or 'halt!': ");
            inputLine = userInput.readLine();
            if (inputLine.equalsIgnoreCase("halt!")) {
                add(serverPort, inputLine); // Send halt command to server
                System.out.println("Client side quitting.");
                break;
            }

            int number = Integer.parseInt(inputLine);
            int result = add(serverPort, number);
            System.out.println("The server returned " + result + ".");
        }
    }

    public static int add(int serverPort, String message) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            byte[] sendBuffer = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(sendPacket);

            if (message.equals("halt!")) return 0; // No need to wait for a response

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            return Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()).trim());
        }
    }

    public static int add(int serverPort, int number) throws Exception {
        return add(serverPort, String.valueOf(number));
    }
}
