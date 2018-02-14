package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransactionInput implements Serializable {

    @JsonProperty("prev_out") public TransactionOutput UTXO;

    @JsonCreator
    public TransactionInput(@JsonProperty("prev_out") TransactionOutput UTXO){
        this.UTXO = UTXO;
    }

}
