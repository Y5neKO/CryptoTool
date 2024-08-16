package com.y5neko.asset;

import com.seeyon.ctp.util.TextEncoder;

public class SeeyonInit {
    private static final int[] PASSWORD_MASK_ARRAY = {19, 78, 10, 15, 100, 213, 43, 23};

    private static String passwordDecode(String paramString) {
        if (paramString != null && paramString.startsWith("___")) {
            String paramString2 = paramString.substring(3);
            StringBuilder localStringBuilder = new StringBuilder();
            int i = 0;
            for (int j = 0; j <= paramString2.length() - 4; j += 4) {
                if (i == PASSWORD_MASK_ARRAY.length) {
                    i = 0;
                }
                String str = paramString2.substring(j, j + 4);
                int k = Integer.parseInt(str, 16) ^ PASSWORD_MASK_ARRAY[i];
                localStringBuilder.append((char) k);
                i++;
            }
            paramString = localStringBuilder.toString();
        }
        return paramString;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage : PassDecode.jar <Method> <Password>");
            System.out.println("--Method [seeyon,fineReport]");
        } else if ("seeyon".equals(args[0])) {
            System.out.println(TextEncoder.decode(args[1]));
            System.out.println(TextEncoder.encode("Test123"));
        } else if ("fineReport".equals(args[0])) {
            String pass = passwordDecode(args[0]);
            System.out.println(pass);
        }
    }

}
