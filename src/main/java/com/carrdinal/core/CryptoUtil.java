package com.carrdinal.core;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
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

    public static boolean verifyECDSASignature(PublicKey publicKey, String input, byte[] signature) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initVerify(publicKey);
            dsa.update(input.getBytes());
            return dsa.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey getDecodedKeyFromString(String key){
        PublicKey publicKey;
        try {
            byte[] decoded = Base64.getDecoder().decode(key);
            KeyFactory factory = KeyFactory.getInstance("ECDSA", "BC");
            publicKey = factory.generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return publicKey;
    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions){
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction: transactions){
            previousTreeLayer.add(transaction.id);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while(count > 1){
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySHA256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return  (treeLayer.size() == 1)?treeLayer.get(0):"";
    }

    public static String decryptMessage(String input, Key key){
        try {
            Cipher decrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decrypt.init(Cipher.ENCRYPT_MODE, key);
            return new String(decrypt.doFinal(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String encryptMessage(String input, Key key){
        try{
            Cipher encrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encrypt.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptdBytes = encrypt.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return new String(encryptdBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        Security.addProvider(new BouncyCastleProvider());
        PublicKey pubKey;
        PrivateKey priKey;
        try{
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA", "BC");
            keygen.initialize(2048);
            KeyPair keyPair  = keygen.generateKeyPair();
            priKey = keyPair.getPrivate();
            pubKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String message = "Hello world!";
        System.out.println("Message: " + message);
        String encrypted = encryptMessage(message, pubKey);
        System.out.println("Encrypted: " + encrypted);
        System.out.println(encrypted.getBytes().length);
//        String decrypted = decryptMessage(encrypted, priKey);
  //      System.out.println("Decrypted: " + decrypted);
    }

}
