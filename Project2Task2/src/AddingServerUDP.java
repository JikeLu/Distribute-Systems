/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class AddingServerUDP {
    private static int sum = 0; // Holds the cumulative sum

    public static void main(String[] args) {
        System.out.println("Server started");
        try (DatagramSocket socket = new DatagramSocket(6789)) {
            while (true) {
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (received.equals("halt!")) {
                    System.out.println("Server halting.");
                    continue;
                }

                int numberToAdd = Integer.parseInt(received.trim());
                sum = add(numberToAdd);

                String response = Integer.toString(sum);
                byte[] sendBuffer = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);

                System.out.println("Adding: " + numberToAdd + " to " + (sum - numberToAdd) + "\nReturning sum of " + sum + " to client");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Separate method to perform addition
    private static int add(int numberToAdd) {
        return sum + numberToAdd;
    }
}
