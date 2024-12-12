import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;

/**
 * Jike Lu, jikelu
 */

public class EchoClientTCP {
    Socket clientSocket;
    int serverPort = 7777;
    String privateKeyString;
    String publicKeyString;
    BigInteger n;
    BigInteger e;
    BigInteger d;
    String clientId;

    EchoClientTCP(){
        System.out.println("Client Running");
        try{
            clientSocket = new Socket("localhost", serverPort);
            generateKeys();
            System.out.println("Public Key: ("+publicKeyString+")");
            System.out.println("Private Key: ("+privateKeyString+")");
            String publicKeyHash = Hash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
            clientId = publicKeyHash.substring(publicKeyHash.length()-40, publicKeyHash.length()).toUpperCase();
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        }
    }

    private void close(){
        try {
            if (clientSocket != null) {
                clientSocket.close();
                System.out.println("Socket Closed.");
            }
        } catch (IOException e) {
            // ignore exception on close
        }
    }

    public static void main(String args[]) {
        EchoClientTCP client = new EchoClientTCP();
        Scanner s = new Scanner(System.in);
        Long startTime;
        Long endTime;
        String result = "";
        while(true){
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the Corruption by recomputing hashes.\n" +
                    "6. Exit");
            int opt = s.nextInt();
            switch (opt){
                case 0:
                    result = client.process(0, "", "");
                    s.nextLine();
                    break;
                case 1:
                    System.out.println("Enter difficulty > 1");
                    int difficulty = s.nextInt();
                    System.out.println("Enter transaction");
                    s.nextLine();
                    String data = s.nextLine();
                    result = client.process(1, String.valueOf(difficulty), data);
                    break;
                case 2:
                    System.out.println("Verifying entire chain");
                    result = client.process(2, "", "");
                    s.nextLine();
                    break;
                case 3:
                    result = client.process(3, "", "");
                    s.nextLine();
                    break;
                case 4:
                    System.out.println("Corrupt the Blockchain");
                    System.out.print("Enter block ID of block to Corrupt: ");
                    int blockId = s.nextInt();
                    System.out.print("Enter new data for block " + blockId + ":\n");
                    s.nextLine();
                    String corruptData = s.nextLine();
                    result = client.process(4, String.valueOf(blockId), corruptData);
                    break;
                case 5:
                    System.out.println("Repairing the entire chain");
                    result = client.process(5, "", "");
                    s.nextLine();
                    break;
                case 6:
                    client.close();
                    System.exit(0);
            }
            System.out.println(result);
        }
    }

    private String process(int operationId, String arg1, String arg2){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            String signature = getSignature(operationId, arg1, arg2);
            String jsonString = getJsonString(operationId, arg1, arg2, signature);
            out.println(jsonString);
            out.flush();
            String response = "";
            String row = in.readLine();
            while (!row.equals("")) {
                response += row;
                response += "\n";
                row = in.readLine();
            }
            return JsonHelper.getMsg(response);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e){
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Other exception from Client: " + e.getMessage());
        }
        return null;
    }

    private void generateKeys(){
        Random rnd = new Random();
        BigInteger p = new BigInteger(2048,100,rnd);
        BigInteger q = new BigInteger(2048,100,rnd);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger ("65537");
        d = e.modInverse(phi);
        publicKeyString = e + "+" + n;
        privateKeyString = d + "+" + n;
    }

    private String getSignature(int operation, String arg1, String arg2) throws Exception {
        String concatString = "";
        concatString += clientId;
        concatString += publicKeyString;
        concatString += String.valueOf(operation);
        concatString += arg1;
        concatString += arg2;
        byte[] bytesOfMessage = concatString.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);
        byte[] messageDigest = new byte[bigDigest.length+1];
        messageDigest[0] = 0;
        for (int i = 1; i < messageDigest.length; i++){
            messageDigest[i] = bigDigest[i-1];
        }
        BigInteger m = new BigInteger(messageDigest);
        BigInteger c = m.modPow(d, n);
        return c.toString();
    }

    private String getJsonString(int operationId, String arg1, String arg2, String c){
        String result = "";
        result += "{\"ClientId\":\"";
        result += clientId;
        result += "\",\"PublicKey\":\"";
        result += publicKeyString;
        result += "\",\"OperationId\":\"";
        result += String.valueOf(operationId);
        result += "\",\"Arg1\":\"";
        result += arg1;
        result += "\",\"Arg2\":\"";
        result += arg2;
        result += "\",\"Signature\":\"";
        result += c;
        result += "\"}";
        return result;
    }
}
