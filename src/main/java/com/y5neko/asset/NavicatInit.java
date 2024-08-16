package com.y5neko.asset;

import com.y5neko.tools.Tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class NavicatInit {

    private int version;
    private static final String AES_KEY = "libcckeylibcckey";
    private static final String AES_IV = "libcciv libcciv ";
    private static final String BLOW_STRING = "3DC5CA39";
    private byte[] blowKey;
    private byte[] blowIv;

    public NavicatInit(int version) throws Exception {
        this.version = version;
        this.blowKey = sha1(BLOW_STRING);
        this.blowIv = hexToBytes("d9c7c3c8870d64bd");
    }

    public String encrypt(String string) throws Exception {
        String result = null;
        switch (this.version) {
            case 11:
                result = encryptEleven(string);
                break;
            case 12:
                result = encryptTwelve(string);
                break;
            default:
                break;
        }
        return result;
    }

    private String encryptEleven(String string) throws Exception {
        int round = string.length() / 8;
        int leftLength = string.length() % 8;
        byte[] result = new byte[0];
        byte[] currentVector = blowIv;

        for (int i = 0; i < round; i++) {
            byte[] block = xorBytes(string.substring(8 * i, 8 * (i + 1)).getBytes(), currentVector);
            byte[] temp = encryptBlock(block);
            currentVector = xorBytes(currentVector, temp);
            result = concatenateByteArrays(result, temp);
        }

        if (leftLength > 0) {
            currentVector = encryptBlock(currentVector);
            result = concatenateByteArrays(result, xorBytes(string.substring(8 * round).getBytes(), currentVector));
        }

        return bytesToHex(result).toUpperCase();
    }

    private String encryptTwelve(String string) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(string.getBytes());
        return bytesToHex(encrypted).toUpperCase();
    }

    public String decrypt(String string) throws Exception {
        String result = null;
        switch (this.version) {
            case 11:
                result = decryptEleven(string);
                break;
            case 12:
                result = decryptTwelve(string);
                break;
            default:
                break;
        }
        return result;
    }

    private String decryptEleven(String upperString) throws Exception {
        byte[] string = hexToBytes(upperString.toLowerCase());
        int round = string.length / 8;
        int leftLength = string.length % 8;
        byte[] result = new byte[0];
        byte[] currentVector = blowIv;

        for (int i = 0; i < round; i++) {
            byte[] encryptedBlock = Arrays.copyOfRange(string, 8 * i, 8 * (i + 1));
            byte[] temp = xorBytes(decryptBlock(encryptedBlock), currentVector);
            currentVector = xorBytes(currentVector, encryptedBlock);
            result = concatenateByteArrays(result, temp);
        }

        if (leftLength > 0) {
            currentVector = encryptBlock(currentVector);
            result = concatenateByteArrays(result, xorBytes(Arrays.copyOfRange(string, 8 * round, string.length), currentVector));
        }

        return new String(result);
    }

    private String decryptTwelve(String upperString) throws Exception {
        byte[] string = hexToBytes(upperString.toLowerCase());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(AES_IV.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(string);
        return new String(decrypted);
    }

    private byte[] encryptBlock(byte[] block) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(blowKey, "Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(block);
    }

    private byte[] decryptBlock(byte[] block) throws Exception {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(blowKey, "Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(block);
    }

    private byte[] xorBytes(byte[] str1, byte[] str2) {
        byte[] result = new byte[str1.length];
        for (int i = 0; i < str1.length; i++) {
            result[i] = (byte) (str1[i] ^ str2[i]);
        }
        return result;
    }

    private byte[] sha1(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        return md.digest(input.getBytes());
    }

    private byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] concatenateByteArrays(byte[] a, byte[] b) {
        return Tools.concatBytes(a, b);
    }

    public static void main(String[] args) throws Exception {
        NavicatInit navicat = new NavicatInit(12);
        String encrypted = navicat.encrypt("example");
        System.out.println("Encrypted: " + encrypted);

        String decrypted = navicat.decrypt("7577A214DB17F79A8933758307190176");
        System.out.println("Decrypted: " + decrypted);
    }
}