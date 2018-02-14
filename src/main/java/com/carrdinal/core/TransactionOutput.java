package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransactionOutput implements Serializable {

    @JsonProperty("id")        public String id;
    @JsonProperty("recipient") public String recipientPubK;
    @JsonProperty("value")     public float  value;
    @JsonProperty("parent_tx") public String parentTransactionID;

    public TransactionOutput(String recipientPubK, float value, String parentTransactionID){
        this.recipientPubK = recipientPubK;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.id = CryptoUtil.applySHA256(recipientPubK +
                                         Float.toString(value) +
                                         parentTransactionID);
    }

    @JsonCreator
    public TransactionOutput(@JsonProperty("id") String id,
                             @JsonProperty("recipient") String recipientPubK,
                             @JsonProperty("value") float value,
                             @JsonProperty("parent_tx") String parentTransactionID){
        this.id = id;
        this.recipientPubK = recipientPubK;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
    }

    public boolean isOwnedBy(String publicKey){
        return publicKey.equals(recipientPubK);
    }

}
