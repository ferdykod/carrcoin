package com.carrdinal.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;



public class Wallet {
    private BlockChain blockchain;
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

    public Wallet(){
        this.blockchain = BlockChain.getInstance();
        generateKeyPair();
    }

    public void generateKeyPair(){
        try{
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Elliptic  Curve KeyPair
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            keygen.initialize(ecSpec, random);
            KeyPair keyPair  = keygen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for(TransactionOutput UTXO: blockchain.UTXOs.values()){
            if(UTXO.isOwnedBy(CryptoUtil.getStringFromKey(publicKey))){
                UTXOs.put(UTXO.id, UTXO); // Add to *our* list of unspent transactions
                total += UTXO.value;
            }
        }
        return total;
    }

    public Transaction generateTransaction(PublicKey recipient, float value){
        if(getBalance() < value){
            System.out.println("Insufficient funds to create transaction.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        float total = 0;
        for(TransactionOutput UTXO: UTXOs.values()){
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO));
            if(total>value) break;
        }

        Transaction transaction = new Transaction(CryptoUtil.getStringFromKey(publicKey),
                                                  CryptoUtil.getStringFromKey(recipient),
                                                  value,
                                                  inputs);
        transaction.generateSignature(privateKey);

        for (TransactionInput input: inputs){
            UTXOs.remove(input.UTXO.id);
        }

        return transaction;
    }

}
