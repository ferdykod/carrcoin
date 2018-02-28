package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryObject {

    @JsonProperty("type") public final String type;
    @JsonProperty("hash") public final String hash;

    public InventoryObject(String type, String hash) {
        this.type = type;
        this.hash = hash;
    }

    @Override
    public String toString(){
        return type + " " + hash;
    }

}
