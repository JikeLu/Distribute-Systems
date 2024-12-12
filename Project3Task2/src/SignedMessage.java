import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignedMessage {
    String ClientID;
    BigInteger e;
    BigInteger n;
    String request;
    BigInteger signiture;
    public void computeSig(BigInteger d) throws NoSuchAlgorithmException {
        // computer hash first
        BigInteger m = computehash();
        // encrypt
        signiture = m.modPow(d, n);
    }
    public SignedMessage() {
    }

    public SignedMessage(String ClientID, BigInteger n, BigInteger e) {
        this.ClientID = ClientID;
        this.n = n;
        this.e=e;
    }
    public BigInteger computehash() throws NoSuchAlgorithmException {
        // concat all the value
        String allToken = ClientID + e.toString() + n.toString() + request;
        // perform the hash from the sample code
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(allToken.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        BigInteger m = new BigInteger(hexString.toString().getBytes());
        // return the hash value
        return m;
    }


}
