package com.y5neko.decrypt;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.xml.bind.DatatypeConverter;

public class TripleDESExample {
    public static void main(String[] args) throws Exception {
        // 3DES 密钥和 IV
        byte[] key = "123456781234567812345678".getBytes(); // 24 字节密钥（168 位）
        byte[] iv = "abcdef12".getBytes();  // 8 字节 IV for CBC mode

        // 要加密的数据
        byte[] plaintext = "Hello, 3DES!".getBytes();

        // 加密
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new DESedeEngine()));
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);

        cipher.init(true, params); // 初始化为加密模式

        byte[] ciphertext = new byte[cipher.getOutputSize(plaintext.length)];
        int len = cipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
        cipher.doFinal(ciphertext, len);

        System.out.println("Ciphertext: " + DatatypeConverter.printHexBinary(ciphertext));

        // 解密
        cipher.init(false, params); // 初始化为解密模式

        byte[] decryptedText = new byte[cipher.getOutputSize(ciphertext.length)];
        len = cipher.processBytes(ciphertext, 0, ciphertext.length, decryptedText, 0);
        cipher.doFinal(decryptedText, len);

        System.out.println("Decrypted Text: " + new String(decryptedText).trim());
    }
}
