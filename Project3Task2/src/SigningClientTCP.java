/**
 * Jike Lu, jikelu
 */

import com.google.gson.Gson;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class SigningClientTCP {
    public static BigInteger[] generateKey(){
        BigInteger n;
        BigInteger e;
        BigInteger d;

        Random rnd = new Random();

        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        n = p.multiply(q);

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        e = new BigInteger("65537");

        d = e.modInverse(phi);

        return new BigInteger[]{n,e,d};

    }
    /**
     * Performs different operations based on the user-selected option.
     * These operations include viewing blockchain status, adding a transaction, verifying the blockchain,
     * viewing the blockchain, corrupting the blockchain, repairing the blockchain, and exiting the program.
     *
     * @param option       The user-selected option indicating the operation to perform.
     * @param in           BufferedReader for reading input from the server.
     * @param out          PrintWriter for sending output to the server.
     * @param clientSocket The socket connected to the server.
     * @param clientId     Unique identifier for the client, generated using SHA-256 hash of RSA public key.
     * @param e            Public exponent of the RSA key pair.
     * @param n            Modulus of the RSA key pair.
     * @param d            Private exponent of the RSA key pair.
     * @throws IOException              If an I/O error occurs while reading or writing to the socket.
     * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
     */
    public static void operation(int option, BufferedReader in, PrintWriter out, Socket clientSocket, String clientId, BigInteger e, BigInteger n, BigInteger d) throws IOException, NoSuchAlgorithmException {
        Gson gson = new Gson();
        RequestMessage request = new RequestMessage(); // Create objects for request, signed request, and response messages.
        SignedMessage signedRequest = new SignedMessage();
        signedRequest.ClientID = clientId;
        signedRequest.n = n;
        signedRequest.e = e;
        ResponseMessage response = new ResponseMessage();
        Scanner s = new Scanner(System.in);
        switch (option) {
            case 0:
                request.setChoice(0);
                String message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                String sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                String recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);
                System.out.println("Current size of chain: " + response.chainSize);
                System.out.println("Difficulty of most recent block: " + response.val1);
                System.out.println("Total difficulty for all blocks: " + response.val2);
                System.out.println("Experimented with 2,000,000 hashes.");
                System.out.println("Approximate hashes per second on this machine: " + response.val4);
                System.out.println("Expected total hashes required for the whole chain: " + response.val5);
                System.out.println("Nonce for most recent block: " + response.val6);
                System.out.println("Chain hash: " + response.hash);
                break;
            case 1:
                System.out.println("Enter difficulty >1");
                int dif = s.nextInt();
                s.nextLine();
                System.out.println("Enter transaction");
                String data = s.nextLine();

                request.setChoice(1);
                request.setData(data);
                request.setDifficulty(dif);
                message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);
                System.out.println("Total execution time to add this block was" + (response.runtime) + " milliseconds");
                break;
            case 2:
                request.setChoice(2);
                message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);

                System.out.println("Verifying entire chain");
                System.out.println("Chain verification:" + response.valid);
                System.out.println("Total execution time required to verify the chain was " + (response.runtime) + " milliseconds");
                break;
            case 3:
                request.setChoice(3);
                message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);
                System.out.print("View the Blockchain");
                System.out.println(response.blockChain);
                break;
            case 4:
                System.out.println("Currupt the Blockchain");
                System.out.println("Enter block ID of block to currupt");
                int choice = s.nextInt();
                s.nextLine();
                request.index = choice;
                System.out.println("new data for block " + choice);
                String newData = s.nextLine();
                request.Data = newData;
                request.setChoice(4);
                message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);
                System.out.println("Block " + response.val1 + " now holds " + response.hash);
                break;
            case 5:
                request.setChoice(5);
                message = gson.toJson(request);
                signedRequest.request = message;
                signedRequest.computeSig(d);
                sendingMessage = gson.toJson(signedRequest);
                out.println(sendingMessage);
                out.flush();
                recieveMessage = in.readLine();
                response = gson.fromJson(recieveMessage, ResponseMessage.class);
                System.out.println("Repairing the entire chain");
                System.out.println("Total execution time required to repair the chain was " + (response.runtime) + " milliseconds");
                break;
            case 6:
                clientSocket.close();
                System.exit(0);

        }
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static void main(String args[]) throws IOException {

        Socket clientSocket = null;
        BigInteger[] keys = generateKey();
        System.out.println("n is :"+keys[0]);
        System.out.println("public key:"+keys[1]);
        System.out.println("private key:"+keys[2]);

        try {
            String RSA_Public = keys[1].toString()+keys[0].toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(RSA_Public.getBytes());
            byte[] clientIdByte = new byte[20];
            System.arraycopy(hash, hash.length - 20, clientIdByte, 0, 20);
            String clientId = bytesToHex(clientIdByte);

            int serverPort = 7777;
            InetAddress aHost = InetAddress.getByName("localhost");
            clientSocket = new Socket(aHost, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(
                    "0. View basic blockchain status.\n" +
                            "1. Add a transaction to the blockchain.\n" +
                            "2. Verify the blockchain.\n" +
                            "3. View the blockchain.\n" +
                            "4. Corrupt the chain.\n" +
                            "5. Hide the corruption by repairing the chain.\n" +
                            "6. Exit.\n");
            String m;

            while ((m = typed.readLine()) != null) {
                int option = Integer.parseInt(m);
                operation(option, in, out, clientSocket, clientId, keys[1], keys[0], keys[2]);
                System.out.println("lock Chain Menu\n" +
                        "0. View basic blockchain status.\n" +
                        "1. Add a transaction to the blockchain.\n" +
                        "2. Verify the blockchain.\n" +
                        "3. View the blockchain.\n" +
                        "4. Corrupt the chain.\n" +
                        "5. Hide the corruption by repairing the chain.\n" +
                        "6. Exit.");
            }
        } catch(IOException | NoSuchAlgorithmException e){
            System.out.println("IO Exception:" + e.getMessage());
        } finally{
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }
}
