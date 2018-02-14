package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Block implements Serializable {

    private ObjectMapper mapper = new ObjectMapper();

    @JsonProperty("hash") private String hash;
    @JsonProperty("prev_block") private final String previousHash;
    @JsonProperty("merkle_root") public String merkleRoot;
    @JsonProperty("transactions") public ArrayList<Transaction> transactions = new ArrayList<>();
    @JsonProperty("timestamp") private final long timestamp;
    @JsonProperty("nonce") private int nonce;

    //TODO version_number, difficulty, number_of_transactions, size_of_block_in_bytes

    public Block(String previousHash){
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    @JsonCreator
    public Block(@JsonProperty("prev_block") String previousHash,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("hash") String hash,
            @JsonProperty("nonce") int nonce,
            @JsonProperty("transactions") ArrayList<Transaction> transactions,
            @JsonProperty("merkle_root") String merkleRoot
            ){
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.hash = hash;
        this.nonce = nonce;
        this.transactions = transactions;
        this.merkleRoot = merkleRoot;
    }

    public String calculateHash(){
        return CryptoUtil.applySHA256(previousHash +
                Long.toString(timestamp) +
                Integer.toString(nonce) +
                merkleRoot);
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void mineBlock(int difficulty){
        System.out.println("Mining for block... Difficulty: " + difficulty);
        merkleRoot = CryptoUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)){
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined! : " + hash);
    }

    public boolean addTransaction(Transaction transaction){
        if(transaction == null) return false;
        if((previousHash != "0")){
            if(!transaction.processTransaction()){
                System.out.println("Transaction failed to process.");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to block.");
        return true;
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
