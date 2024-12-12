import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Jike Lu, jikelu
 */

public class EchoServerTCP {

    private static boolean isValid(String msg){
        String clientId = JsonHelper.getField(msg,"ClientId");
        String publicKeyString = JsonHelper.getField(msg,"PublicKey");
        String signature = JsonHelper.getField(msg,"Signature");
        int operationId = Integer.valueOf(JsonHelper.getField(msg,"OperationId"));
        String arg1 = JsonHelper.getField(msg,"Arg1");
        String arg2 = JsonHelper.getField(msg,"Arg2");

        BigInteger e = new BigInteger(publicKeyString.split("\\+")[0]);
        BigInteger n = new BigInteger(publicKeyString.split("\\+")[1]);
        String publicKeyHash = Hash.ComputeSHA_256_as_Hex_String(publicKeyString.toLowerCase());
        if (!publicKeyHash.substring(publicKeyHash.length()-40,publicKeyHash.length()).toUpperCase().equals(clientId)){
            return false;
        }

        String messageToCheck = "";
        messageToCheck += clientId;
        messageToCheck += publicKeyString;
        messageToCheck += String.valueOf(operationId);
        messageToCheck += arg1;
        messageToCheck += arg2;

        BigInteger encryptedHash = new BigInteger(signature);
        BigInteger decryptedHash = encryptedHash.modPow(e, n);
        byte[] messageToCheckDigest = null;
        try{
            byte[] bytesOfMessageToCheck = messageToCheck.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            messageToCheckDigest = md.digest(bytesOfMessageToCheck);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException ex){
            System.out.println(ex.getMessage());
        }

        byte[] extraByte = new byte[messageToCheckDigest.length+1];
        extraByte[0] = 0;
        for (int i = 1; i < extraByte.length; i++){
            extraByte[i] = messageToCheckDigest[i-1];
        }

        BigInteger bigIntegerToCheck = new BigInteger(extraByte);

        return bigIntegerToCheck.compareTo(decryptedHash) == 0;
    }

    public static void main(String args[]) {
        System.out.println("Server Running");
        Socket clientSocket = null;
        BlockChain blockChain = new BlockChain();
        try {
            int serverPort = 7777;
            ServerSocket listenSocket = new ServerSocket(serverPort);

            while(true){
                clientSocket = listenSocket.accept();
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                while (in.hasNextLine()){
                    String data = in.nextLine();
                    String replyString = "";
                    if (!isValid(data)){
                        replyString = "Error in request";
                    }
                    else{
                        String clientId = JsonHelper.getField(data,"ClientId");
                        String publicKeyString = JsonHelper.getField(data,"PublicKey");
                        String signature = JsonHelper.getField(data,"Signature");
                        int operationId = Integer.valueOf(JsonHelper.getField(data,"OperationId"));
                        String arg1 = JsonHelper.getField(data,"Arg1");
                        String arg2 = JsonHelper.getField(data,"Arg2");
                        if (blockChain.getChainSize() == 0){
                            Block genesisBlock = new Block(0, blockChain.getTime(), "Genesis", 2);
                            blockChain.addBlock(genesisBlock);
                        }

                        long startTime;
                        long endTime;
                        switch(operationId){
                            case (0):
                                replyString = "";
                                replyString += ("Current size of chain: " + blockChain.getChainSize());
                                replyString += "\n";
                                replyString += ("Current hashes per second by this machine: " + blockChain.hashesPerSecond());
                                replyString += "\n";
                                replyString += ("Difficulty of most recent block: " + blockChain.getLatestBlock().getDifficulty());
                                replyString += "\n";
                                replyString += ("Nonce for most recent block: " + blockChain.getLatestBlock().getNonce());
                                replyString += "\n";
                                replyString += ("Chain hash: " + blockChain.chainHash);
                                replyString += "\n";
                                break;
                            case (1):
                                replyString = "";
                                Block newBlock = new Block(blockChain.getChainSize(), blockChain.getTime(), arg2, Integer.valueOf(arg1));
                                startTime = System.currentTimeMillis();
                                blockChain.addBlock(newBlock);
                                endTime = System.currentTimeMillis();
                                replyString += ("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                            case (2):
                                replyString = "";
                                startTime = System.currentTimeMillis();
                                boolean isValid = blockChain.isChainValid();
                                replyString += ("Chain verification: " + isValid);
                                replyString += "\n";
                                endTime = System.currentTimeMillis();
                                if (!isValid){
                                    replyString += (blockChain.errorMessage());
                                    replyString += "\n";
                                }
                                replyString += ("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                            case (3):
                                replyString = (blockChain.toString());
                                replyString += "\n";
                                break;
                            case(4):
                                Block corruptBlock = blockChain.chain.get(Integer.valueOf(arg1));
                                corruptBlock.setData(arg2);
                                replyString = (String.format("Block %d now holds %s",Integer.valueOf(arg1),arg2));
                                replyString += "\n";
                                break;
                            case(5):
                                startTime = System.currentTimeMillis();
                                blockChain.repairChain();
                                endTime = System.currentTimeMillis();
                                replyString = ("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                                replyString += "\n";
                                break;
                        }
                    }
                    out.println(getJsonString(replyString));
                    out.flush();
                    System.out.println(getJsonString(replyString));
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

    private static String getJsonString(String reply){
        String result = "";
        result += "{\"Msg\":\"";
        result += reply;
        result += "\"}";
        result += "\n";
        return result;
    }
}
