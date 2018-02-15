package com.carrdinal.network.commands;

import com.carrdinal.core.BlockChain;

public class GetBlockCountCommandHandler extends com.carrdinal.network.commands.Command {

    @Override public String execute(String[] args) {
        return String.valueOf(BlockChain.getInstance().blocks.size());
    }

}
