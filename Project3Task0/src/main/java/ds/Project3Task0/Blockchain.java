package ds.Project3Task0;

/**
 * Jike Lu, jikelu
 */

import com.google.gson.Gson;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a basic implementation of a blockchain.
 */
public class Blockchain {
    private final ArrayList<Block> blockChain; // List to hold blocks in the blockchain
    private String chainHash; // Hash of the entire blockchain
    private int hashesPerSecond; // Hashing speed

    /**
     * Constructor to initialize the blockchain.
     */
    public Blockchain() {
        this.blockChain = new ArrayList<>();
        this.chainHash = "";
    }

    /**
     * Getter for the chain hash.
     * @return The hash of the entire blockchain.
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     * Retrieves the current timestamp.
     * @return Current timestamp.
     */
    public Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Retrieves the last block in the blockchain.
     * @return The last block in the blockchain.
     */
    public Block getLastBlock() {
        return blockChain.get(blockChain.size() - 1);
    }

    /**
     * Retrieves the size of the blockchain.
     * @return Size of the blockchain.
     */
    public int getChainSize() {
        return blockChain.size();
    }

    /**
     * Computes the hashes per second.
     */
    public void computeHashesPerSecond() {
        Timestamp startTime = getCurrentTime();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (int i = 0; i < 2000000; i++) {
                md.update("00000000".getBytes());
                byte[] bytesInfo = md.digest();
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Timestamp endTime = getCurrentTime();
        hashesPerSecond = (int) (2000000 / (endTime.getTime() - startTime.getTime()) * 1000.0);
    }

    /**
     * Getter for hashes per second.
     * @return Number of hashes per second.
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * Adds a new block to the blockchain.
     * @param newBlock The new block to be added.
     */
    public void addBlock(Block newBlock) {
        newBlock.setPreviousHash(chainHash);
        blockChain.add(newBlock);
        chainHash = newBlock.proofOfWork();
    }

    /**
     * Converts the blockchain object to its JSON representation.
     * @return JSON representation of the blockchain.
     */
    public String toString() {
        Blockchain blockchainToJson = new Blockchain();
        for (int i = 0; i < getChainSize(); i++) {
            blockchainToJson.blockChain.add(getBlock(i));
        }
        blockchainToJson.hashesPerSecond = getHashesPerSecond();
        blockchainToJson.chainHash = getChainHash();
        Gson gson = new Gson();
        return gson.toJson(blockchainToJson);
    }

    /**
     * Retrieves a block from the blockchain by index.
     * @param index The index of the block.
     * @return The block at the specified index.
     */
    public Block getBlock(int index) {
        return blockChain.get(index);
    }

    /**
     * Calculates the total difficulty of the blockchain.
     * @return Total difficulty of the blockchain.
     */
    public int getTotalDifficulty() {
        int sumOfDifficulty = 0;
        for (Block block : blockChain) {
            sumOfDifficulty += block.getDifficulty();
        }
        return sumOfDifficulty;
    }

    /**
     * Calculates the total expected hashes of the blockchain.
     * @return Total expected hashes of the blockchain.
     */
    public double getTotalExpectedHashes() {
        double totalExpectedHashes = 0;
        for (Block block : blockChain) {
            totalExpectedHashes += Math.pow("0123456789ABCDEF".length(), block.getDifficulty());
        }
        return totalExpectedHashes;
    }

    /**
     * Checks if the blockchain is valid.
     * @return A string indicating the validity of the blockchain.
     */
    public String isChainValid() {
        Block currentBlock;
        String hash;
        String previousHash = "";
        for (Block block : blockChain) {
            currentBlock = block;
            if (!currentBlock.getPreviousHash().equals(previousHash)) {
                return "FALSE; Improper hash on node " + blockChain.indexOf(currentBlock) + ". Does not match with " + previousHash;
            }
            String beginWithZeros = "0".repeat(currentBlock.getDifficulty());
            hash = currentBlock.calculateHash();
            if (!hash.startsWith(beginWithZeros)) {
                return "FALSE; Improper hash on node " + blockChain.indexOf(currentBlock) + ". Does not begin with " + beginWithZeros;
            }
            previousHash = hash;
        }
        if (!chainHash.equals(previousHash)) {
            return "FALSE; Improper chain hash.";
        }
        return "TRUE";
    }

    /**
     * Repairs the chain by recalculating previous hashes.
     */
    public void repairChain() {
        String previousHash = "";
        for (Block block : blockChain) {
            block.setPreviousHash(previousHash);
            previousHash = block.proofOfWork();
        }
        chainHash = previousHash;
    }

    /**
     * Main method to interact with the blockchain.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        // Initialize the blockchain and compute hashes per second
        Blockchain mainBlockchain = new Blockchain();
        mainBlockchain.computeHashesPerSecond();

        // Create and add genesis block to the blockchain
        Block genesisBlock = new Block(0, mainBlockchain.getCurrentTime(), "Genesis", 2);
        mainBlockchain.addBlock(genesisBlock);

        // Interaction loop
        Scanner scanner = new Scanner(System.in);
        Timestamp launchTime;
        Timestamp finishedTime;
        while (true) {
            // Display menu options
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" +
                    "6. Exit.\n");
            int userChoice = scanner.nextInt();
            if (userChoice == 0) {
                // View blockchain status
                System.out.println("Current size of chain: " + mainBlockchain.getChainSize());
                System.out.println("Difficulty of most recent block: " + mainBlockchain.getLastBlock().getDifficulty());
                System.out.println("Total difficulty for all blocks: " + mainBlockchain.getTotalDifficulty());
                System.out.println("Approximate hashes per second on this machine: " + mainBlockchain.getHashesPerSecond());
                System.out.println("Expected total hashes required for the whole chain: " + mainBlockchain.getTotalExpectedHashes());
                System.out.println("Nonce for most recent block: " + mainBlockchain.getLastBlock().getNonce());
                System.out.println("Chain hash:" + mainBlockchain.getChainHash());
            } else if (userChoice == 1) {
                // Add a transaction to the blockchain
                System.out.println("Enter difficulty > 1");
                int difficulty = scanner.nextInt();
                System.out.println("Enter transaction");
                scanner.nextLine();
                String dataForNewBlock = scanner.nextLine();
                Block newBlock = new Block(mainBlockchain.getChainSize(), mainBlockchain.getCurrentTime(), dataForNewBlock, difficulty);
                launchTime = mainBlockchain.getCurrentTime();
                mainBlockchain.addBlock(newBlock);
                finishedTime = mainBlockchain.getCurrentTime();
                System.out.println("Total execution time to add this block was " + (int) (finishedTime.getTime() - launchTime.getTime()) + " milliseconds");
            } else if (userChoice == 2) {
                // Verify the blockchain
                launchTime = mainBlockchain.getCurrentTime();
                String answer = mainBlockchain.isChainValid();
                if (answer.equals("TRUE")) {
                    System.out.println("Chain verification: " + answer);
                } else {
                    String[] result = answer.split(";");
                    System.out.println("Chain verification: " + result[0]);
                    System.out.println(result[1]);
                }
                finishedTime = mainBlockchain.getCurrentTime();
                System.out.println("Total execution time required to verify the chain was " + (int) (finishedTime.getTime() - launchTime.getTime()) + " milliseconds");
            } else if (userChoice == 3) {
                // View the blockchain
                System.out.println("View the Blockchain");
                System.out.println(mainBlockchain.toString());
            } else if (userChoice == 4) {
                // Corrupt the chain
                System.out.println("Corrupt the Blockchain");
                System.out.println("Enter block ID of block to corrupt: ");
                int blockId = scanner.nextInt();
                System.out.println("Enter new data for block " + blockId);
                scanner.nextLine();
                String newData = scanner.nextLine();
                Block corruptBlock = mainBlockchain.blockChain.get(blockId);
                corruptBlock.setData(newData);
                System.out.printf("Block %d now holds %s\n", blockId, newData);
            } else if (userChoice == 5) {
                // Repair the chain
                launchTime = mainBlockchain.getCurrentTime();
                mainBlockchain.repairChain();
                finishedTime = mainBlockchain.getCurrentTime();
                System.out.println("Total execution time required to repair the chain was " + (int) (finishedTime.getTime() - launchTime.getTime()) + " milliseconds");
            } else if (userChoice == 6) {
                // Exit
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
}
