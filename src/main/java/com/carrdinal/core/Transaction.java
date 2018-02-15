package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.security.PrivateKey;
import java.util.ArrayList;

public class Transaction implements Serializable{

    private ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore private BlockChain blockchain;

    @JsonProperty("id")        public  String id;
    @JsonProperty("sender")    public  String senderPubK;
    @JsonProperty("recipient") public  String recipientPubK;
    @JsonProperty("value")     public  float  value;
    @JsonProperty("signature") public  byte[] signature;

    @JsonProperty("inputs")  public ArrayList<TransactionInput> inputs;
    @JsonProperty("outputs") public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    @JsonProperty("sequence") private static int sequence = 0;

    @JsonCreator
    public Transaction(@JsonProperty("sender") String sender,
                       @JsonProperty("recipient") String recipient,
                       @JsonProperty("value") float value,
                       @JsonProperty("inputs") ArrayList<TransactionInput> inputs){
        this.blockchain = BlockChain.getInstance();
        this.senderPubK = sender;
        this.recipientPubK = recipient;
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
            input.UTXO = blockchain.UTXOs.get(input.UTXO.id);
        }

        if (getInputsValue() < BlockChain.MINIMUM_TRANSACTION) {
            System.out.println("Transaction inputs too small: " + getInputsValue());
        }

        float leftOver = getInputsValue() - value;
        id = calculateHash();
        outputs.add(new TransactionOutput(this.recipientPubK, value, id)); // Send value to recipientPubK
        outputs.add(new TransactionOutput(this.senderPubK, leftOver, id)); // Send left over back to sender

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

    @JsonIgnore
    public float getInputsValue() {
        float total = 0;
        if(inputs == null) return total;

        for(TransactionInput input: inputs){
            if(input.UTXO == null) continue;
            total += input.UTXO.value;
        }
        return total;
    }

    @JsonIgnore
    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput output: outputs){
            total += output.value;
        }
        return total;
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
