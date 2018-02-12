package com.carrdinal.core;

import java.util.ArrayList;
import java.util.Date;

public class Block {
    private       String hash;
    private final String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private final long   timestamp;
    private       int    nonce;

    public Block(String previousHash){
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
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
}
