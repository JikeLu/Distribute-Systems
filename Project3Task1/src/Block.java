import java.math.BigInteger;
import java.sql.Timestamp;

public class Block {
    int index;
    Timestamp timestamp;
    String data;
    String previousHash;
    BigInteger nonce;
    int difficulty;

    Block(int index, Timestamp timestamp, String data, int difficulty){
        this.index=index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    public String calculateHash(){
        String s = "";
        s += String.valueOf(this.index);
        s += this.timestamp.toString();
        s += this.data;
        s += this.previousHash;
        s += this.nonce;
        s += this.difficulty;
        String hash = Hash.ComputeSHA_256_as_Hex_String(s);
        return hash;
    }


    public BigInteger getNonce(){
        return this.nonce;
    }

    public String proofOfWork(){
        this.nonce = BigInteger.ZERO;
        while (true){
            String hash = calculateHash();
            if (hash.substring(0,this.difficulty).equals("0".repeat(this.difficulty))){
                return hash;
            }
            else{
                this.nonce = this.nonce.add(BigInteger.ONE);
            }
        }
    }


    public int getDifficulty(){
        return this.difficulty;
    }


    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

    @Override
    public String toString(){
        String jsonString = "{\"index\" : \"";
        jsonString += String.valueOf(this.index);
        jsonString += "\", \"time stamp\" : \"";
        jsonString += this.timestamp.toString();
        jsonString += "\", \"Tx\" : \"";
        jsonString += this.data;
        jsonString += "\", \"PrevHash\" : \"";
        jsonString += this.previousHash;
        jsonString += "\", \"nonce\" : ";
        jsonString += this.nonce;
        jsonString += ", \"difficulty\" : ";
        jsonString += String.valueOf(this.difficulty);
        jsonString += "}";
        return jsonString;
    }


    public void setPreviousHash(String previousHash){
        this.previousHash = previousHash;
    }


    public String getPreviousHash(){
        return this.previousHash;
    }

    public int getIndex(){
        return this.index;
    }


    public void setIndex(int index){
        this.index = index;
    }


    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }


    public Timestamp getTimestamp(){
        return this.timestamp;
    }


    public String getData(){
        return this.data;
    }

    public void setData(String data){
        this.data = data;
    }

    public static void main(java.lang.String[] args){
    }

}
