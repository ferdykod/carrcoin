package com.carrdinal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class BlockChain {

    public static ArrayList<Block>                   blockchain          = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs               = new HashMap<String, TransactionOutput>();
    public static final float                        MINIMUM_TRANSACTION = 0.01f;
    public static int                                difficulty          = 5;
    public static Wallet walletA;
    public static Wallet walletB;
    private static Transaction genesisTransaction;

    public static void main(String[] args) {
        // Setup BounceyCastle as a security provider
        Security.addProvider(new BouncyCastleProvider());
        // Create our wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionID = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient,
                                                             genesisTransaction.value,
                                                             genesisTransaction.transactionID));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and mining genesis block...");
        Block genesisBlock = new Block("0");
        genesisBlock.addTransaction(genesisTransaction);
        addBlock(genesisBlock);


        //testing
        Block block1 = new Block(genesisBlock.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.generateTransaction(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.generateTransaction(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.generateTransaction( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        for(int i=1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);
            // compare registered hash with calculated hash
            if(!currentBlock.getHash().equals(currentBlock.calculateHash())){
                System.out.println("Current hashes not equal.");
                return false;
            }
            // compare previous hash with registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous hashes not equal.");
                return false;
            }
            // check that hash has been solved
            if(!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }

            TransactionOutput tempTXO;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction transaction = currentBlock.transactions.get(t);
                if(!transaction.verifySignature()){
                    System.out.println("Signature on transaction[" + t + "] is invalid.");
                    return false;
                }
                if(transaction.getInputsValue() != transaction.getOutputsValue()){
                    System.out.println("Inputs are not equal to outputs on transaction[" + t + "].");
                    return false;
                }

                for (TransactionInput input: transaction.inputs){
                    tempTXO = tempUTXOs.get(input.transactionOutputID);
                    if(tempTXO == null){
                        System.out.println("Referenced input on transaction [" + t + "] is missing.");
                        return false;
                    }
                    if (input.UTXO.value != tempTXO.value){
                        System.out.println("Referenced input transaction [" + t + "] value is invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.transactionOutputID);
                }

                for (TransactionOutput TXO: transaction.outputs){
                    tempUTXOs.put(TXO.id, TXO);
                }

                if(transaction.outputs.get(0).recipient != transaction.recipient){
                    System.out.println("Transaction [" + t + "] output recipient is not who it should be.");
                    return false;
                }
                if(transaction.outputs.get(1).recipient != transaction.sender){
                    System.out.println("Transaction [" + t + "] output 'change' is not sender.");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid.");
        return true;
    }

    public static void addBlock(Block block){
        block.mineBlock(difficulty);
        blockchain.add(block);
    }
}
