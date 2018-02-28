package com.carrdinal.network.commands;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;
import com.carrdinal.core.Transaction;

public class GetBlockCommandHandler extends Command {
    @Override public String execute(String[] args) {
        if(args == null || args.length < 1){
            return "getblock []: must specific the hash";
        }
        String hash = args[0];

        Block block = BlockChain.getInstance().getBlock(hash);

        if (block != null) {
            return block.toJSON();
        }

        return "getblock [" + hash + "]: could not find this block";
    }
}
