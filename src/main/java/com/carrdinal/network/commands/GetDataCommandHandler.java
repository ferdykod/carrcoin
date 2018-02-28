package com.carrdinal.network.commands;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;
import com.carrdinal.core.Transaction;

public class GetDataCommandHandler extends Command {

    @Override public String execute(String[] args) {
        // Returns an inventory of all blocks in a range.
        // args: type, hash
        if(args.length != 1){
            System.out.println("arguments should be in the form: <type> <hash>");
            return null;
        }

        String type = args[0];
        String hash = args[1];

        switch(type){
            case "tx":
                return getTransactionsInventory(hash);
            case "block":
                return getBlocksInventory(hash);
            default:
                System.out.println("type should be either: \"tx\" or \"block\"");
                return null;
        }
    }

    private String getTransactionsInventory(String hash){
        Transaction tx = BlockChain.getInstance().getTransaction(hash);
        if(tx != null) return tx.toJSON();
        return "getdata tx [" + hash + "]: could not find this transaction";
    }

    private String getBlocksInventory(String hash){
        Block block = BlockChain.getInstance().getBlock(hash);
        if(block != null) return block.toJSON();
        return "getdata block [" + hash + "]: could not find this block";
    }

}
