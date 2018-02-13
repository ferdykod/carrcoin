package com.carrdinal.core;

import java.security.PublicKey;

public class TransactionOutput {

    public String id;
    public String recipient;
    public float value;
    public String parentTransactionID;

    public TransactionOutput(String recipient, float value, String parentTransactionID){
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.id = CryptoUtil.applySHA256(CryptoUtil.getStringFromKey(recipient) +
                                         Float.toString(value) +
                                         parentTransactionID);
    }

    public boolean isOwnedBy(String publicKey){
        return (publicKey == recipient);
    }

}
