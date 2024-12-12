import com.google.gson.Gson;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

public class Block extends Object {
    int index;
    Timestamp timestamp;
    String data;
    int difficulty;
    String previousHash;
    BigInteger nounce;

    // Getters and setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public BigInteger getNounce() {
        return nounce;
    }

    public void setNounce(BigInteger nounce) {
        this.nounce = nounce;
    }

    // Constructor
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    // Calculate hash of the block
    public String calculateHash() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String input = index + timestamp.toString() + data + previousHash + nounce.toString() + difficulty;
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Perform proof of work to find a valid hash
    public String proofOfWork() throws NoSuchAlgorithmException {
        nounce = BigInteger.ZERO;
        String hash = "";
        while (true) {
            hash = calculateHash();
            if (hash.startsWith(repeat("0", difficulty))) {
                break;
            } else {
                nounce = nounce.add(BigInteger.ONE);
            }
        }
        return hash;
    }

    // Helper method to repeat a string
    private static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

    // Convert the block to JSON string
    @Override
    public String toString() {
        Gson gson = new Gson();
        String result = gson.toJson(this);
        return result;
    }
}
