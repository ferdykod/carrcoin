package com.carrdinal.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {

    ObjectMapper mapper = new ObjectMapper();
    private BlockChain blockchain;
    public String      transactionID;
    public String      senderPubK;
    public String      recipientPubK;
    public float       value;
    public byte[]      signature;

    public ArrayList<TransactionInput> inputs;
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey sender, PublicKey recipient, float value, ArrayList<TransactionInput> inputs){
        this.blockchain = BlockChain.getInstance();
        this.senderPubK = CryptoUtil.getStringFromKey(sender);
        this.recipientPubK = CryptoUtil.getStringFromKey(recipient);
        this.value = value;
        this.inputs = inputs;
    }

    public String calculateHash(){
        sequence++;
        return CryptoUtil.applySHA256(senderPubK + recipientPubK + Float.toString(value) + sequence);
    }

    // Signs any data which we don't want tampered with
    public void generateSignature(PrivateKey privateKey){
        String data = senderPubK + recipientPubK + Float.toString(value);
        signature = CryptoUtil.applyECDSASignature(privateKey, data);
    }

    // Verifies the data we signed hasn't been tampered with
    public boolean verifySignature(){
        String data = senderPubK + recipientPubK + Float.toString(value);
        return CryptoUtil.verifyECDSASignature(CryptoUtil.getDecodedKeyFromString(senderPubK), data, signature);
    }

    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction signature failed to verify");
            return false;
        }

        for (TransactionInput input: inputs) {
            input.UTXO = blockchain.UTXOs.get(input.transactionOutputID);
        }

        if (getInputsValue() < BlockChain.MINIMUM_TRANSACTION) {
            System.out.println("Transaction inputs too small: " + getInputsValue());
        }

        float leftOver = getInputsValue() - value;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.recipient, value, transactionID)); // Send value to recipient
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID)); // Send left over back to sender

        // Add outputs to the unspent list
        for(TransactionOutput output: outputs){
            blockchain.UTXOs.put(output.id, output);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for (TransactionInput input: inputs){
            if(input.UTXO == null) continue;
            blockchain.UTXOs.remove(input.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput input: inputs){
            if(input.UTXO == null) continue;
            total += input.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput output: outputs){
            total += output.value;
        }
        return total;
    }

    public String toJSON(){
        // TODO
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
