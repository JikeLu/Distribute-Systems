/**
 * Author: Jike Lu
 * ID: jikelu
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.TreeMap;

public class RemoteVariableServerUDP {
    private static final Map<Integer, Integer> userSums = new TreeMap<>();

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(6789)) {
            System.out.println("Server started");

            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                String[] parts = received.split(",");
                int id = Integer.parseInt(parts[0]);
                String operation = parts[1];
                int value = operation.equals("get") ? 0 : Integer.parseInt(parts[2]);

                int result = performOperation(id, operation, value);
                if(!operation.equals("get")) {
                    System.out.println("Client " + id + " " + operation + " " + value + " is " + result);
                } else {
                    System.out.println("Client " + id + " gets value " + value);
                }

                String response = String.valueOf(result);
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int performOperation(int id, String operation, int value) {
        userSums.putIfAbsent(id, 0);
        if ("add".equals(operation)) {
            userSums.computeIfPresent(id, (k, v) -> v + value);
        } else if ("subtract".equals(operation)) {
            userSums.computeIfPresent(id, (k, v) -> v - value);
        }
        return userSums.get(id);
    }
}
