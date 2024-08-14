package com.y5neko.encrypt;

import com.y5neko.tools.Tools;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.util.encoders.Hex;

import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class SM3_Encryption {
    /**
     * SM3加密公共接口
     * @param paramData 原始数据
     * @param salt 盐值
     * @return SM3加密后的16进制字符串
     */
    public static byte[] sm3Encrypt(String paramData, String inputType, String salt, String saltType){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        byte[] data = null;
        byte[] saltBytes = null;

        // 原始数据
        if(inputType!= null){
            switch (inputType) {
                case "Base64":
                    data = Base64.getDecoder().decode(paramData);
                    break;
                case "Hex":
                    data = Hex.decode(paramData);
                    break;
                case "UTF-8":
                    data = paramData.getBytes();
                    break;
                case "Hexdump":
                    data = Tools.toHexDump(paramData.getBytes()).getBytes();
            }
        }

        if(salt!= null){
            switch (saltType) {
                case "Base64":
                    saltBytes = Base64.getDecoder().decode(salt);
                    break;
                case "Hex":
                    saltBytes = Hex.decode(salt);
                    System.out.println(Arrays.toString(saltBytes));
                    break;
                case "UTF-8":
                    saltBytes = salt.getBytes();
                    break;
            }
            if (saltBytes != null) {
                data = Tools.concatBytes(data, saltBytes);
            }
        }

        // 计算SM3哈希值
        return calculateSM3Hash(data);
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
