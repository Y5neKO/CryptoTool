package com.y5neko.asset;

import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


public class FinalshellInit {
    static int Size = 8;
    static SecureRandom sr;

    public static boolean checkStr(String str) {
        if (str == null) {
            return true;
        }
        String s2 = str.trim();
        return "".equals(s2);
    }

    public static String decodePass(String data) throws Exception {
        if (data == null) {
            return null;
        }
        String rs = "";
        if (!checkStr(data)) {
            byte[] buf = new BASE64Decoder().decodeBuffer(data);
            byte[] head = new byte[Size];
            System.arraycopy(buf, 0, head, 0, head.length);
            byte[] d = new byte[buf.length - head.length];
            System.arraycopy(buf, head.length, d, 0, d.length);
            byte[] bt = desDecode(d, ranDomKey(head));
            rs = new String(bt, StandardCharsets.UTF_8);
        }
        return rs;
    }

    public static String encodePass(String content) throws Exception {
        byte[] head = generateByte(Size);
        byte[] d = desEncode(content.getBytes(StandardCharsets.UTF_8), head);
        byte[] result = new byte[head.length + d.length];
        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(d, 0, result, head.length, d.length);
        String base64 = new BASE64Encoder().encodeBuffer(result);
        String rs = base64.replace(IOUtils.LINE_SEPARATOR_UNIX, "");
        return rs;
    }

    static byte[] ranDomKey(byte[] head) throws NoSuchAlgorithmException, IOException {
        long ks = 3680984568597093857L / new Random(head[5]).nextInt(127);
        Random random = new Random(ks);
        byte b = head[0];
        for (int i = 0; i < b; i++) {
            random.nextLong();
        }
        long n = random.nextLong();
        Random r2 = new Random(n);
        long[] ld = {head[4], r2.nextLong(), head[7], head[3], r2.nextLong(), head[1], random.nextLong(), head[2]};
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        int j = ld.length;
        byte b2 = 0;
        while (true) {
            byte b3 = b2;
            if (b3 < j) {
                long l = ld[b3];
                try {
                    dos.writeLong(l);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                b2 = (byte) (b3 + 1);
            } else {
                break;
            }
        }
        dos.close();
        byte[] keyData = bos.toByteArray();
        return md5(keyData);
    }

    public static byte[] md5(byte[] data) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5").digest(data);
    }

    static byte[] generateByte(int len) {
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = (byte) new Random().nextInt(127);
        }
        return data;
    }

    public static byte[] desEncode(byte[] data, byte[] head) throws Exception {
        DESKeySpec dks = new DESKeySpec(ranDomKey(head));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, secretKey, sr);
        return cipher.doFinal(data);
    }

    public static byte[] desDecode(byte[] data, byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, secretKey, sr);
        return cipher.doFinal(data);
    }
}