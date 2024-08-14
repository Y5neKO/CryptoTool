package com.y5neko.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;

public class NTLM_Encryption {

    public static byte[] ntlmv1Hash(String password) throws NoSuchAlgorithmException {
        // Add BouncyCastle as a security provider to use MD4
        Security.addProvider(new BouncyCastleProvider());

        // Convert the password to a byte array using UTF-16LE encoding
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_16LE);

        // Get MD4 MessageDigest instance
        MessageDigest md4 = MessageDigest.getInstance("MD4");

        // Hash the password using MD4
        byte[] ntlmHash = md4.digest(passwordBytes);

        return ntlmHash;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        try {
            String password = "Password123";
            byte[] ntlmv1Hash = ntlmv1Hash(password);

            // Convert the hash to a hexadecimal string for display
            System.out.println("NTLMv1 Hash: " + bytesToHex(ntlmv1Hash));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
