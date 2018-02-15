package com.carrdinal.network.commands;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;

public class GetBlockCommandHandler extends Command {
    @Override public String execute(String[] args) {
        if(args == null || args.length < 1){
            return "getblock []: must specific the hash";
        }
        String hash = args[0];
        for(Block block : BlockChain.getInstance().blocks){
            if(block.getHash().equals(hash)){
                return block.toJSON();
            }
        }
        return "getblock [" + hash + "]: could not find this block";
    }
}
