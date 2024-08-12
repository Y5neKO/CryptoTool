package com.y5neko.encrypt;

import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class SM3_Encryption {
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 原始数据
        byte[] data = "Hello, world!".getBytes();

        // 生成随机盐值
        byte[] salt = generateSalt();

        // 拼接原始数据和盐值
        byte[] dataWithSalt = concatBytes(data, salt);

        // 计算SM3哈希值
        byte[] hash = calculateSM3Hash(dataWithSalt);

        // 转换为16进制字符串
        String hashHex = bytesToHex(hash);
        String saltHex = bytesToHex(salt);
        String hashBase64 = sm3Encrypt("Hello, world!", null, "base64");

        // 输出结果
        System.out.println("Salt: " + saltHex);
        System.out.println("Hash: " + hashHex);
        System.out.println("Base64 hash: " + hashBase64);
        System.out.println("Base64 hash length: " + hashBase64.length());
    }

    /**
     * SM3加密公共接口
     * @param paramData 原始数据
     * @param salt 盐值
     * @return SM3加密后的16进制字符串
     */
    public static String sm3Encrypt(String paramData, byte[] salt, String formatType){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 原始数据
        byte[] data = paramData.getBytes();

        // 检测是否加盐并拼接原始数据和盐值
        if (salt != null) {
            data = concatBytes(data, salt);
        }

        // 计算SM3哈希值
        byte[] hash = calculateSM3Hash(data);

        // 判断加密类型并返回结果
        if (formatType != null){
            if (formatType.equalsIgnoreCase("hex")) {
                return bytesToHex(hash);
            } else if (formatType.equalsIgnoreCase("base64")) {
                return bytesToBase64(hash);
            } else {
                return bytesToHex(hash);
            }
        } else {
            return bytesToHex(hash);
        }
    }

    /**
     * 生成随机盐值
     * @return 随机盐值
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[8];
        new Random().nextBytes(salt);
        return salt;
    }

    /**
     * 拼接原始数据和盐值
     * @param data 原始数据
     * @param salt 盐值
     * @return 拼接后的数据
     */
    private static byte[] concatBytes(byte[] data, byte[] salt) {
        byte[] result = Arrays.copyOf(data, data.length + salt.length);
        System.arraycopy(salt, 0, result, data.length, salt.length);
        return result;
    }

    /**
     * 计算SM3哈希值
     * @param data 原始数据
     * @return SM3哈希值
     */
    private static byte[] calculateSM3Hash(byte[] data) {
        SM3Digest digest = new SM3Digest();
        digest.update(data, 0, data.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    /**
     * 将字节数组转换为Base64编码字符串
     * @param bytes 字节数组
     * @return Base64编码字符串
     */
    private static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
