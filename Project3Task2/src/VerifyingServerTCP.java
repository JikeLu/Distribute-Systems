/**
 * Jike Lu, jikelu
 */

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class VerifyingServerTCP {
    static BlockChain bc;

    // this did not change, same as task1
    public static ResponseMessage operation(RequestMessage r) throws NoSuchAlgorithmException {
        ResponseMessage response = new ResponseMessage();
        int option = r.choice;
        switch (option) {
            case 0:
                // show operation, which set default information in response message
                response.chainSize= bc.getChain().size();
                response.val1= bc.getChain().get(bc.getChain().size()-1).getDifficulty();
                response.val2=bc.getTotalDifficulty();
                response.val4=bc.getHashesPerSecond();
                response.val5=bc.getTotalExpectedHashes();
                response.val6=bc.getChain().get(bc.getChain().size() - 1).getNounce();
                response.hash=bc.getChainHash();
                break;
            case 1:
                // add block
                int dif = r.getDifficulty();
                String data = r.getData();
                Block newTransaction = new Block(bc.getChainSize(), bc.getTime(), data, dif);
                long start = System.currentTimeMillis();
                bc.addBlock(newTransaction);
                long end = System.currentTimeMillis();
                response.runtime=end - start;
                break;
            case 2:
                // check validity
                long startTime = System.currentTimeMillis();
                response.valid=bc.isChainValid();
                long endTime = System.currentTimeMillis();
                response.runtime=endTime - startTime;
                break;
            case 3:
                // show all the block in the chain
                Gson builder = new Gson();
                response.blockChain=builder.toJson(bc);
                break;
            case 4:
                // corrupt the blcok
                int choice = r.index;
                String newData = r.Data;
                bc.getChain().get(choice).setData(newData);
                response.val1=choice;
                response.hash=newData;
                break;
            case 5:
                // repair the chain
                long start_repair = System.currentTimeMillis();
                bc.repairChain();
                long end_repair = System.currentTimeMillis();
                response.runtime=end_repair-start_repair;
                break;}
        // store all the chain size
        response.chainSize= bc.getChainSize();
        return response;

    }
    // from chatgpt
    // Helper method to convert byte array to hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static void main(String args[]) throws NoSuchAlgorithmException {
        // initiate bc and add gensis block infront
        bc = new BlockChain();
        Block b = new Block(0,bc.getTime(),"",2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();
        Socket clientSocket = null;
        // from the example code
        try {
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);

            while(true) {
                clientSocket = listenSocket.accept();
                Scanner in = new Scanner(clientSocket.getInputStream());

                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                Gson gson = new Gson();
                while (true) {
                    if (!in.hasNext()) break; // Exit loop if no more input

                    String data = in.nextLine();
                    SignedMessage s = gson.fromJson(data, SignedMessage.class);

                    // Compute the signature decryption and verify the hash
                    BigInteger decryptHash = s.signiture.modPow(s.e, s.n);
                    BigInteger verifyHash = s.computehash();

                    // Concatenate public key components for ID computation
                    String RSA_Public = s.e.toString() + s.n.toString();

                    // Compute the client ID from RSA public key
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(RSA_Public.getBytes());
                    byte[] clientIdByte = new byte[20];
                    System.arraycopy(hash, hash.length - 20, clientIdByte, 0, 20);
                    String clientId = bytesToHex(clientIdByte);

                    System.out.println("New Client.");

                    // Check if the client ID and decrypted hash match the expected values
                    if (clientId.equals(s.ClientID) && decryptHash.compareTo(verifyHash) == 0) {
                        System.out.println("Signature verified ID: " + clientId);

                        RequestMessage request = gson.fromJson(s.request, RequestMessage.class);
                        ResponseMessage response = operation(request);

                        String sendBack = gson.toJson(response);
                        System.out.println("public key {e: "+s.e+"} {n: "+s.n+"}");
                        System.out.println("Json Message: " + sendBack);
                        System.out.println("Number of Blocks on Chain == " + response.chainSize + "\n");

                        out.println(sendBack);
                    } else {
                        System.out.println("Error: Signature verification failed.");
                        out.println("Error in request");
                    }
                    out.flush();
                }

            }
            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());

            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
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
