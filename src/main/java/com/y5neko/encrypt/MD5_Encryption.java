package com.y5neko.encrypt;

import com.y5neko.tools.Tools;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.util.encoders.Hex;

import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

public class MD5_Encryption {
    public static byte[] md5Encrypt(String paramData, String inputType, String salt, String saltType) {
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

        MD5Digest md5Digest = new MD5Digest();
        if (data != null) {
            md5Digest.update(data, 0, data.length);
        }
        byte[] md5Hash = new byte[md5Digest.getDigestSize()];
        md5Digest.doFinal(md5Hash, 0);
        return md5Hash;
    }
}
