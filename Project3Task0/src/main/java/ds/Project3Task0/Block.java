package ds.Project3Task0;

import com.google.gson.Gson;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class Block {
    private int index;
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    private BigInteger nonce;
    private int difficulty;


    public Block(int index, Timestamp timestamp,String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    public String calculateHash() {
        String block_to_string = "";
        block_to_string = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(block_to_string.getBytes("UTF-8"),0,block_to_string.length());
            byte[] bytes_info;
            bytes_info = md.digest();
            return bytesToHex(bytes_info);
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException" + e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private String bytesToHex(byte[] bytesInfo) {
        char[] hexChars = new char[bytesInfo.length * 2];
        for (int j = 0; j < bytesInfo.length; j++) {
            int v = bytesInfo[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public int getDifficulty() {

        return difficulty;
    }


    public BigInteger getNonce() {

        return this.nonce;
    }

    public String getPreviousHash() {

        return previousHash;
    }


    public String proofOfWork() {
        nonce = BigInteger.ZERO;
        String zeros = "0".repeat(difficulty);

        while (true) {
            String hash = calculateHash();
            if (hash.startsWith(zeros)) {
                return hash;
            } else {
                nonce =  this.nonce.add(BigInteger.ONE);
            }
        }
    }

    public void setData(String data) {

        this.data = data;
    }

    public void setPreviousHash(String previousHash) {

        this.previousHash = previousHash;
    }


    public String toString() {
        Block block_helper = new Block (index, timestamp, data, difficulty);
        block_helper.nonce = nonce;
        block_helper.setPreviousHash(previousHash);
        Gson gson = new Gson();
        return gson.toJson(block_helper);
    }

}