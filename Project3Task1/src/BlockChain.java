import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Jike Lu, jikelu
 */

public class BlockChain {
    ArrayList<Block> chain;
    String chainHash;
    public BlockChain(){
        this.chain = new ArrayList<Block>();
        this.chainHash = "";
    }

    public Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    public Block getLatestBlock(){
        return this.chain.get(this.chain.size()-1);
    }

    public int getChainSize(){
        return this.chain.size();
    }

    public int hashesPerSecond(){
        Long startTime = System.currentTimeMillis();
        int numHash = 0;
        while (numHash < 1000000){
            Hash.ComputeSHA_256_as_Hex_String("00000000");
            numHash ++;
        }
        Long endTime = System.currentTimeMillis();
        return (int) (numHash / ((endTime - startTime)/1000.0));
    }

    public void addBlock(Block newBlock){
        newBlock.setPreviousHash(this.chainHash);
        this.chain.add(newBlock);
        this.chainHash = newBlock.proofOfWork();
    }

    public String toString(){
        String s = "{\"ds_chain\" : [";
        for (int i = 0; i < this.chain.size(); i++){
            s += this.chain.get(i);
            if (i != this.chain.size()-1){
                s += ",\n";
            }
            else{
                s += "\n";
            }
        }
        s += "], \"chainHash\":\"";
        s += this.chainHash;
        s += "\"}";
        return s;
    }

    public boolean isChainValid(){
        Block thisBlock;
        String thisHash;
        String prevHash = "";
        for (int i = 0; i < this.chain.size(); i++){
            thisBlock = this.chain.get(i);
            if (!thisBlock.previousHash.equals(prevHash)){
                //System.out.printf("..Improper previousHash on node %d Does not match with %s\n",i, prevHash);
                return false;
            }
            thisHash = thisBlock.calculateHash();
            if (!thisHash.substring(0,thisBlock.difficulty).equals("0".repeat(thisBlock.difficulty))){
                //System.out.printf("..Improper hash on node %d Does not begin with %s\n",i,"0".repeat(thisBlock.difficulty));
                return false;
            }
            prevHash = thisHash;
        }
        if (chainHash.equals(prevHash)){
            return true;
        }
        else{
            return false;
        }
    }
    public String errorMessage(){
        Block thisBlock;
        String thisHash;
        String prevHash = "";
        for (int i = 0; i < this.chain.size(); i++){
            thisBlock = this.chain.get(i);
            if (!thisBlock.previousHash.equals(prevHash)){
                return String.format("..Improper previousHash on node %d Does not match with %s",i, prevHash);
            }
            thisHash = thisBlock.calculateHash();
            if (!thisHash.substring(0,thisBlock.difficulty).equals("0".repeat(thisBlock.difficulty))){
                return String.format("..Improper hash on node %d Does not begin with %s",i,"0".repeat(thisBlock.difficulty));
            }
            prevHash = thisHash;
        }
        if (!chainHash.equals(prevHash)){
            return "..Improper chain hash";
        }
        return "";
    }

    public void repairChain(){
        Block thisBlock;
        String thisHash = "";
        for (int i = 0; i < this.chain.size(); i++){
            thisBlock = this.chain.get(i);
            thisBlock.setPreviousHash(thisHash);
            thisHash = thisBlock.proofOfWork();
        }
        this.chainHash = thisHash;
    }

    public static void main(java.lang.String[] args) {
        BlockChain bc = new BlockChain();
        Block genesisBlock = new Block(0, bc.getTime(), "Genesis", 2);
        bc.addBlock(genesisBlock);
        Scanner s = new Scanner(System.in);
        Long startTime;
        Long endTime;
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
                    System.out.println("Current size of chain: " + bc.getChainSize());
                    System.out.println("Current hashes per second by this machine: " + bc.hashesPerSecond());
                    System.out.println("Difficulty of most recent block: " + bc.getLatestBlock().getDifficulty());
                    System.out.println("Nonce for most recent block: " + bc.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + bc.chainHash);
                    break;
                case 1:
                    System.out.println("Enter difficulty > 0");
                    int difficulty = s.nextInt();
                    System.out.println("Enter transaction");
                    s.nextLine();
                    String data = s.nextLine();
                    Block newBlock = new Block(bc.getChainSize(), bc.getTime(), data, difficulty);
                    startTime = System.currentTimeMillis();
                    bc.addBlock(newBlock);
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
                    break;
                case 2:
                    System.out.println("Verifying entire chain");
                    startTime = System.currentTimeMillis();
                    boolean isValid = bc.isChainValid();
                    System.out.println("Chain verification: " + isValid);
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 3:
                    System.out.println(bc.toString());
                    break;
                case 4:
                    System.out.println("Corrupt the Blockchain");
                    System.out.print("Enter block ID of block to Corrupt: ");
                    int blockId = s.nextInt();
                    System.out.print("Enter new data for block " + blockId + ":\n");
                    s.nextLine();
                    String corruptData = s.nextLine();
                    Block corruptBlock = bc.chain.get(blockId);
                    corruptBlock.setData(corruptData);
                    System.out.printf("Block %d now holds %s\n",blockId,corruptData);
                    break;
                case 5:
                    System.out.println("Repairing the entire chain");
                    startTime = System.currentTimeMillis();
                    bc.repairChain();
                    endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 6:
                    System.exit(0);
            }
        }
    }
}
