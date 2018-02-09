package com.carrdinal;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class CryptoUtil {

    public static String applySHA256(String input){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hashHex = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hashHex.append('0');
                hashHex.append(hex);
            }
            return hashHex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] applyECDSASignature(PrivateKey privateKey, String input){
        try{
            // Digital signature algorithm
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            dsa.update(input.getBytes());
            return dsa.sign();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyECDSASignature(PublicKey publicKey, String input, byte[] signature){
        try{
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(publicKey);
            dsa.update(input.getBytes());
            return dsa.verify(signature);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction: transactions){
            previousTreeLayer.add(transaction.transactionID);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1){
            treeLayer = new ArrayList<String>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return  (treeLayer.size() == 1)?treeLayer.get(0):"";

    }

}
