/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RemoteVariableClientUDP {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("The client is running.");
        System.out.print("Please enter server port: ");
        int serverPort = Integer.parseInt(reader.readLine());
        InetAddress address = InetAddress.getByName("localhost");

        while (true) {
            System.out.println("\n1. Add a value to your sum.\n2. Subtract a value from your sum.\n3. Get your sum.\n4. Exit client");
            int choice = Integer.parseInt(reader.readLine());
            if (choice == 4) {
                System.out.println("Client side quitting.");
                break;
            }

            int value = 0;
            if (choice != 3) { // Only ask for value if the choice is not 3
                System.out.print("Enter value to " + (choice == 1 ? "add: " : "subtract: "));
                value = Integer.parseInt(reader.readLine());
            }

            // Ask for ID outside the condition, as it's required in all cases
            System.out.print("Enter your ID: ");
            int id = Integer.parseInt(reader.readLine());

            String operation = choice == 1 ? "add" : choice == 2 ? "subtract" : "get";
            int result = communicateWithServer(address, serverPort, id, operation, value);
            System.out.println("The result is " + result + ".");
        }
    }

    private static int communicateWithServer(InetAddress address, int serverPort, int id, String operation, int value) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = id + "," + operation + (value != 0 ? "," + value : "");
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
            socket.send(packet);

            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            return Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()).trim());
        }
    }
}

