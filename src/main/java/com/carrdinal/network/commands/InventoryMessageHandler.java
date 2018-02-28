package com.carrdinal.network.commands;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;
import com.carrdinal.core.InventoryObject;
import com.carrdinal.core.Transaction;
import com.carrdinal.network.messages.GetDataMessage;
import com.carrdinal.core.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class InventoryMessageHandler extends Command {

    private static ObjectMapper mapper = new ObjectMapper();

    /*
        Should be called when we receive a inv message from a peer.
        We will check if we have the inventory objects already and if they are new we will return a getdata message
        which with an Inventory containing all the objects we want.
     */
    @Override public String execute(String[] args) {
        if(args.length != 1){
            System.out.println("should be one argument containing the inv message");
            return null;
        }

        Inventory inventory;
        try {
            inventory = mapper.readValue(args[0], Inventory.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // For each inventory object check if we have it and if not add to list of objects we want to get back
        for(InventoryObject inventoryObject : inventory.list){
            boolean hasObject = false;
            if(inventoryObject.type.equals(Inventory.BLOCK)){
                for (Block block : BlockChain.getInstance().blocks) {
                    if (block.getHash().equals(inventoryObject.hash)){
                        hasObject = true;
                        break;
                    }
                }
            }
            else if(inventoryObject.type.equals(Inventory.TX)){
                for (Block block : BlockChain.getInstance().blocks) {
                    for (Transaction tx : block.transactions) {
                        if (tx.id.equals(inventoryObject.hash)){
                            hasObject = true;
                            break;
                        }
                    }
                }
            }
            if(hasObject){
                inventory.list.remove(inventoryObject);
            }
        }

        return GetDataMessage.get(inventory);
    }

}
