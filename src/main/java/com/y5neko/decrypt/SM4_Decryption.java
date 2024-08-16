package com.y5neko.decrypt;

import com.y5neko.tools.Tools;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

import static com.y5neko.tools.Tools.getPadding;

public class SM4_Decryption {
    public static byte[] sm4Decrypt(String paramData, String inputType, String salt, String saltType, String key, String keyType, String mode, String padding, String blocksize, String iv, String ivType) throws InvalidCipherTextException {
         Security.addProvider(new BouncyCastleProvider());
        // 创建SM4算法实例
        BlockCipher engine = new SM4Engine();
        PaddedBufferedBlockCipher cipher = null;
        org.bouncycastle.crypto.paddings.BlockCipherPadding paddingType;

        byte[] data = null;
        byte[] saltBytes = null;
        byte[] keyBytes = null;
        byte[] ivBytes = null;
        SecretKey secretKey = null;

        // 处理原始数据为字节数组
        if (inputType != null) {
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

        // 处理密钥
        if (key != null) {
            switch (keyType) {
                case "Hex":
                    keyBytes = Hex.decode(key);
                    break;
                case "Base64":
                    keyBytes = Base64.getDecoder().decode(key);
                    break;
                case "UTF-8":
                    keyBytes = key.getBytes();
                    break;
            }
            if (keyBytes != null) {
                secretKey = new SecretKeySpec(keyBytes, "SM4");
            }
        }

        // 处理填充方式
        paddingType = getPadding(padding);

        // 处理模式
        switch (mode) {
            case "CBC":
                cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(engine), paddingType);
                break;
            case "ECB":
                cipher = new PaddedBufferedBlockCipher(engine, paddingType);
                break;
            case "CFB":
                cipher = new PaddedBufferedBlockCipher(new CFBBlockCipher(engine, Integer.parseInt(blocksize)), paddingType);
                break;
            case "OFB":
                cipher = new PaddedBufferedBlockCipher(new OFBBlockCipher(engine, Integer.parseInt(blocksize)), paddingType);
                break;
            case "CTR":
                cipher = new PaddedBufferedBlockCipher(new SICBlockCipher(engine), paddingType);
                break;
        }

        // 处理IV
        if (iv != null) {
            switch (ivType) {
                case "Hex":
                    ivBytes = Hex.decode(iv);
                    break;
                case "Base64":
                    ivBytes = Base64.getDecoder().decode(iv);
                    break;
                case "UTF-8":
                    ivBytes = iv.getBytes();
            }
            // 首先处理GCM情况
            if (mode.equals("GCM")) {
                GCMBlockCipher gcm = new GCMBlockCipher(engine);
                CipherParameters params = new AEADParameters(new KeyParameter(secretKey.getEncoded()), 128, ivBytes);
                gcm.init(false, params);

                byte[] cipherText = new byte[gcm.getOutputSize(data.length)];
                int len = gcm.processBytes(data, 0, data.length, cipherText, 0);
                gcm.doFinal(cipherText, len);
                return cipherText;
            }
            // 排除ECB模式并初始化cipher
            if (cipher != null && !mode.equals("ECB")) {
                cipher.init(false, new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), ivBytes));
            } else if(mode.equals("ECB")){
                cipher.init(false, new KeyParameter(secretKey.getEncoded()));
            }
            // 开始解密
            byte[] cipherText = new byte[cipher.getOutputSize(data.length)];
            int outputLen = cipher.processBytes(data, 0, data.length, cipherText, 0);
            cipher.doFinal(cipherText, outputLen);
            return cipherText;
        }
        return null;
    }
}
