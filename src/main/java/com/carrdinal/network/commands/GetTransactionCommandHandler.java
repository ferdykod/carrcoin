package com.carrdinal.network.commands;

import com.carrdinal.core.Block;
import com.carrdinal.core.BlockChain;
import com.carrdinal.core.Transaction;

public class GetTransactionCommandHandler extends Command {

    @Override public String execute(String[] args) {
        if(args == null || args.length < 1){
            return "gettransaction []: must specify the txid";
        }
        String txid = args[0];

        Transaction tx = BlockChain.getInstance().getTransaction(txid);
        if (tx != null) return tx.toJSON();

        return "gettransaction [" + txid + "]: could not find this transaction";
    }

}
