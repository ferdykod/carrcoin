package com.carrdinal.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Inventory {

    private static ObjectMapper mapper = new ObjectMapper();
    public static final String BLOCK   = "block";
    public static final String TX = "tx";

    @JsonProperty("inventory") public final ArrayList<InventoryObject> list;

    public Inventory(ArrayList<InventoryObject> list){
        this.list = list;
    }

    public Inventory(Block block){
        this.list = new ArrayList<>();
        add(block);
    }

    public Inventory(Transaction tx){
        this.list = new ArrayList<>();
        add(tx);
    }

    public void add(Block block){
        this.list.add(new InventoryObject(Inventory.BLOCK, block.getHash()));
    }

    public void add(Transaction tx){
        this.list.add(new InventoryObject(Inventory.TX, tx.id));
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
