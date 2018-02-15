package com.carrdinal.core;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.Serializable;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain implements Serializable {

    private static ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("min_tx") public static final float MINIMUM_TRANSACTION = 0.01f;
    @JsonProperty("difficulty") public static int difficulty = 5;

    @JsonProperty("blocks") public ArrayList<Block> blocks = new ArrayList<>();
    @JsonProperty("utxos") public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    @JsonProperty("genesis_tx") private Transaction genesisTransaction;

    public Block currentBlock;
    private static BlockChain instance;

    @JsonCreator
    public BlockChain(@JsonProperty("blocks") ArrayList<Block> blocks,
            @JsonProperty("utxos") HashMap<String, TransactionOutput> UTXOs,
            @JsonProperty("genesis_tx") Transaction genesisTransaction){
        this.blocks = blocks;
        this.UTXOs = UTXOs;
        this.genesisTransaction = genesisTransaction;
    }

    private BlockChain(){

    }

    public static BlockChain getInstance(){
        if(instance == null){
            instance = new BlockChain();
        }
        return instance;
    }

    public static void main(String[] args) {
        // Setup Bouncy Castle as a security provider
        Security.addProvider(new BouncyCastleProvider());

        // Create our wallets
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();
        BlockChain blockchain = BlockChain.loadFromFile("blockchain.json");

        System.out.println(blockchain.isChainValid());

//        //testing
//        Block block1 = new Block(genesisBlock.getHash());
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
//        block1.addTransaction(walletA.generateTransaction(walletB.publicKey, 40f));
//        blockchain.addBlock(block1);
//        System.out.println(genesisBlock.toJSON());
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//        Block block2 = new Block(block1.getHash());
//        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
//        block2.addTransaction(walletA.generateTransaction(walletB.publicKey, 1000f));
//        blockchain.addBlock(block2);
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//        Block block3 = new Block(block2.getHash());
//        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
//        block3.addTransaction(walletB.generateTransaction( walletA.publicKey, 20));
//        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//        System.out.println("WalletB's balance is: " + walletB.getBalance());
//        blockchain.addBlock(block3);
//        System.out.println(block3.toJSON());
//
//        blockchain.isChainValid();
//        System.out.println(blockchain.toJSON());
//        blockchain.saveToFile();
    }

    private static void createGenesisBlock(Wallet walletA, Wallet coinbase, BlockChain blockchain) {
        System.out.println("Creating and mining genesis block...");

        blockchain.genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, new ArrayList<TransactionInput>());
        blockchain.genesisTransaction.generateSignature(coinbase.privateKey);
        blockchain.genesisTransaction.id = "0";
        blockchain.genesisTransaction.outputs.add(new TransactionOutput(blockchain.genesisTransaction.recipientPubK,
                blockchain.genesisTransaction.value,
                blockchain.genesisTransaction.id));
        blockchain.UTXOs.put(blockchain.genesisTransaction.outputs.get(0).id, blockchain.genesisTransaction.outputs.get(0));

        Block genesisBlock = new Block("0");
        genesisBlock.addTransaction(blockchain.genesisTransaction);
        blockchain.addBlock(genesisBlock);
    }

    @JsonIgnore
    public Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        for(int i = 1; i < blocks.size(); i++){
            currentBlock = blocks.get(i);
            previousBlock = blocks.get(i-1);
            // compare registered hash with calculated hash
            if(!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("Current hashes not equal.");
                return false;
            }
            // compare previous hash with registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous hashes not equal.");
                return false;
            }
            // check that hash has been solved
            if(!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }

            TransactionOutput tempTXO;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction transaction = currentBlock.transactions.get(t);
                if(!transaction.verifySignature()){
                    System.out.println("Signature on transaction[" + t + "] is invalid.");
                    return false;
                }
                if(transaction.getInputsValue() != transaction.getOutputsValue()){
                    System.out.println("Inputs are not equal to outputs on transaction[" + t + "].");
                    return false;
                }

                for (TransactionInput input: transaction.inputs){
                    tempTXO = tempUTXOs.get(input.UTXO.id);
                    if(tempTXO == null){
                        System.out.println("Referenced input on transaction [" + t + "] is missing.");
                        return false;
                    }
                    if (input.UTXO.value != tempTXO.value){
                        System.out.println("Referenced input transaction [" + t + "] value is invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.UTXO.id);
                }

                for (TransactionOutput TXO: transaction.outputs){
                    tempUTXOs.put(TXO.id, TXO);
                }

                if(!transaction.outputs.get(0).recipientPubK.equals(transaction.recipientPubK)){
                    System.out.println("Transaction [" + t + "] output recipientPubK is not who it should be.");
                    return false;
                }
                if(!transaction.outputs.get(1).recipientPubK.equals(transaction.senderPubK)){
                    System.out.println("Transaction [" + t + "] output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid.");
        return true;
    }

    public void addBlock(){
        currentBlock.mineBlock(difficulty);
        blocks.add(currentBlock);
    }

    public static BlockChain loadFromFile(String filepath){
        try {
            instance = mapper.readValue(new File(filepath), BlockChain.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void saveToFile(){
        try {
            mapper.writeValue(new File("blockchain.json"), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toJSON(){
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
