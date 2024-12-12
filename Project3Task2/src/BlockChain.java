/**
 * Jike Lu, jikelu
 */

import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class BlockChain extends Object {
    private ArrayList<Block> chain;
    private String chainHash;
    private Double hashesPerSecond;

    public BlockChain() {
        this.chain = new ArrayList<>();
        this.chainHash = "";
        this.hashesPerSecond = 0.0;
    }

    public ArrayList<Block> getChain() {
        return chain;
    }

    public void setChain(ArrayList<Block> chain) {
        this.chain = chain;
    }

    public String getChainHash() {
        return chainHash;
    }

    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    public Double getHashesPerSecond() {
        return hashesPerSecond;
    }

    public void setHashesPerSecond(Double hashesPerSecond) {
        this.hashesPerSecond = hashesPerSecond;
    }

    public Timestamp getTime() {
        Date now = new Date();
        return new Timestamp(now.getTime());
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public int getChainSize() {
        return chain.size();
    }

    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
        String hashInformation = "00000000";
        long start = System.currentTimeMillis();
        MessageDigest hashFunc = MessageDigest.getInstance("SHA-256");
        for (int i = 0; i < 2000000; i++) {
            byte[] hash = hashFunc.digest(hashInformation.getBytes());
        }
        long end = System.currentTimeMillis();
        double timeInSeconds = (double) (end - start) / 1000.0;
        hashesPerSecond = 2000000 / timeInSeconds;
    }

    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        newBlock.setPreviousHash(chainHash);
        newBlock.proofOfWork();
        chainHash = newBlock.calculateHash();
        chain.add(newBlock);
    }

    public int getTotalDifficulty() {
        int sum = 0;
        for (Block b : chain) {
            sum += b.getDifficulty();
        }
        return sum;
    }

    public Block getBlock(int i) {
        return chain.get(i);
    }

    public double getTotalExpectedHashes() {
        double sum = 0.0;
        for (Block b : chain) {
            double expectedHash = Math.pow(16, b.getDifficulty());
            sum += expectedHash;
        }
        return sum;
    }

    private static String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

    /**
     * Checks the validity of the blockchain by examining each block's hash and previous hash pointers.
     * @return A string indicating the validity status of the blockchain.
     * @throws NoSuchAlgorithmException Thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public String isChainValid() throws NoSuchAlgorithmException {
        // If the chain has only one block
        if (chain.size() == 1) {
            // Check if the hash of the block meets the difficulty requirement
            if (chain.get(0).calculateHash().startsWith(repeat("0", chain.get(0).getDifficulty()))) {
                // If the block's hash matches the stored chain hash, the chain is considered valid
                if (chain.get(0).calculateHash().equals(chainHash)) {
                    return "TRUE";
                } else {
                    return "False";
                }
            } else {
                return "False";
            }
        } else {
            // For chains with more than one block
            for (int i = 1; i < chain.size(); i++) {
                // Check if the previous block's hash meets the difficulty requirement
                if (chain.get(i - 1).calculateHash().startsWith(repeat("0", chain.get(i - 1).getDifficulty()))) {
                    // If the previous block's hash doesn't match the current block's previous hash, return an error message
                    if (!chain.get(i - 1).calculateHash().equals(chain.get(i).previousHash)) {
                        return " at position" + (i - 1) + "the hash doesn't equal to the next hash pointer";
                    }
                } else {
                    return " at position" + (i - 1) + "the prove of work is wrong";
                }
            }
            // Check if the last block's hash meets the difficulty requirement
            if (chain.get(chain.size() - 1).calculateHash().startsWith(repeat("0", chain.get(chain.size() - 1).getDifficulty()))) {
                // If the last block's hash matches the stored chain hash, the chain is considered valid
                if (chain.get(chain.size() - 1).calculateHash().equals(chainHash)) {
                    return "TRUE";
                } else {
                    return "False last chainhash is wrong";
                }
            } else {
                return "False,  last block proof of work is wrong";
            }
        }
    }


    /**
     * This method attempts to repair the blockchain by validating each block and performing necessary adjustments.
     * It uses proof of work to ensure the integrity of each block.
     * @throws NoSuchAlgorithmException Thrown when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public void repairChain() throws NoSuchAlgorithmException {
        // Loop until the chain is valid
        while (!isChainValid().equals("TRUE")) {
            // If there's only one block in the chain
            if (chain.size() == 1) {
                // Check if the hash of the block starts with a sufficient number of zeros
                if (chain.get(0).calculateHash().startsWith(repeat("0", chain.get(0).getDifficulty()))) {
                    // If the calculated hash matches the stored chain hash, the chain is considered valid and repair is not needed
                    if (chain.get(0).calculateHash().equals(chainHash)) {
                        return;
                    } else {
                        // Otherwise, update the chain hash
                        chainHash = chain.get(0).calculateHash();
                    }
                } else {
                    // If the hash doesn't meet the difficulty requirement, perform proof of work and update the chain hash
                    chain.get(0).proofOfWork();
                    chainHash = chain.get(0).calculateHash();
                }
            } else {
                // For chains with more than one block
                for (int i = 1; i < chain.size(); i++) {
                    // Check if the previous block's hash meets the difficulty requirement
                    if (chain.get(i - 1).calculateHash().startsWith(repeat("0", chain.get(i - 1).getDifficulty()))) {
                        // If the previous block's hash doesn't match the current block's previous hash, update the previous hash
                        if (!chain.get(i - 1).calculateHash().equals(chain.get(i).previousHash)) {
                            chain.get(i).setPreviousHash(chain.get(i - 1).calculateHash());
                        }
                    } else {
                        // If the previous block's hash doesn't meet the difficulty requirement, perform proof of work and update the previous hash
                        chain.get(i - 1).proofOfWork();
                        chain.get(i).setPreviousHash(chain.get(i - 1).calculateHash());
                    }
                }
                // Check if the last block's hash meets the difficulty requirement
                if (chain.get(chain.size() - 1).calculateHash().startsWith(repeat("0", chain.get(chain.size() - 1).getDifficulty()))) {
                    // If the last block's hash doesn't match the stored chain hash, update the chain hash
                    if (!chain.get(chain.size() - 1).calculateHash().equals(chainHash)) {
                        chainHash = chain.get(chain.size() - 1).calculateHash();
                    }
                } else {
                    // If the last block's hash doesn't meet the difficulty requirement, perform proof of work and update the chain hash
                    chain.get(chain.size() - 1).proofOfWork();
                    chainHash = chain.get(chain.size() - 1).calculateHash();
                }
            }
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        BlockChain bc = new BlockChain();
        Block b = new Block(0, bc.getTime(), "", 2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();
        double hashesPerSecond = bc.getHashesPerSecond();
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("Block Chain Menu\n" +
                    "0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" +
                    "6. Exit.");
            int option = s.nextInt();
            switch (option) {
                case 0:
                    System.out.println("Current size of chain: " + bc.chain.size());
                    System.out.println("Difficulty of most recent block: " + bc.chain.get(bc.chain.size() - 1).getDifficulty());
                    System.out.println("Total difficulty for all blocks: " + bc.getTotalDifficulty());
                    System.out.println("Experimented with 2,000,000 hashes.");
                    System.out.println("Approximate hashes per second on this machine: " + bc.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + bc.getTotalExpectedHashes());
                    System.out.println("Nonce for most recent block: " + bc.chain.get(bc.chain.size() - 1).getNounce());
                    System.out.println("Chain hash: " + bc.getChainHash());
                    break;
                case 1:
                    System.out.println("Enter difficulty >1");
                    int dif = s.nextInt();
                    s.nextLine();
                    System.out.println("Enter transaction");
                    String data = s.nextLine();
                    Block newTransaction = new Block(bc.getChainSize(), bc.getTime(), data, dif);
                    long start = System.currentTimeMillis();
                    bc.addBlock(newTransaction);
                    long end = System.currentTimeMillis();
                    System.out.println("Total execution time to add this block was " + (end - start) + " milliseconds");
                    break;
                case 2:
                    long startTime = System.currentTimeMillis();
                    System.out.println("Verifying entire chain");
                    System.out.println("Chain verification: " + bc.isChainValid());
                    long endTime = System.currentTimeMillis();
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 3:
                    Gson builder = new Gson();
                    System.out.println("View the Blockchain");
                    System.out.println(builder.toJson(bc));
                    break;
                case 4:
                    System.out.println("Corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int choice = s.nextInt();
                    s.nextLine();
                    System.out.println("new data for block " + choice);
                    String newData = s.nextLine();
                    bc.getChain().get(choice).setData(newData);
                    System.out.println("Block " + choice + " now holds " + newData);
                    break;
                case 5:
                    System.out.println("Repairing the entire chain");
                    long start_repair = System.currentTimeMillis();
                    bc.repairChain();
                    long end_repair = System.currentTimeMillis();
                    System.out.println("Total execution time required to repair the chain was " + (end_repair - start_repair) + " milliseconds");
                    break;
                case 6:
                    return;
            }
        }
    }
}
