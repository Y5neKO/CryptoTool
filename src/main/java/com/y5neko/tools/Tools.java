package com.y5neko.tools;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Tools {
    /**
     * 获取图片按钮
     * @param imgPath 图片路径
     * @return 图片按钮
     */
    public static Button getImgButton(String imgPath) {
            Button button = new Button();
            Image imageClose = new Image(imgPath);
            ImageView imageViewClose = new ImageView(imageClose);
            imageViewClose.setFitHeight(20);
            imageViewClose.setPreserveRatio(true);
            button.setGraphic(imageViewClose);
            button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // 仅显示图形内容
            return button;
        }

    /**
     * 输出hexdump格式
     * @param bytes 字节数组
     * @return hexdump格式字符串
     */
    public static String toHexDump(byte[] bytes) {
        StringBuilder hexDump = new StringBuilder();
        int length = bytes.length;
        for (int i = 0; i < length; i += 16) {
            // 打印地址（偏移量）
            hexDump.append(String.format("%08X: ", i));
            // 打印十六进制内容
            for (int j = 0; j < 16; j++) {
                if (i + j < length) {
                    hexDump.append(String.format("%02X ", bytes[i + j]));
                } else {
                    hexDump.append("   "); // 不足16字节时，补空格
                }
            }
            // 插入分隔符
            hexDump.append("|");
            // 打印可读字符
            for (int j = 0; j < 16; j++) {
                if (i + j < length) {
                    byte b = bytes[i + j];
                    if (b >= 32 && b <= 126) {
                        hexDump.append((char) b); // 可打印字符
                    } else {
                        hexDump.append("."); // 非可打印字符
                    }
                }
            }
            hexDump.append("\n");
        }
        return hexDump.toString();
    }

    /**
     * 将hexdump格式字符串转换为字节数组
     * @param hexDump hexdump格式字符串
     * @return 字节数组
     */
    public static byte[] fromHexDump(String hexDump) {
        StringBuilder hexString = new StringBuilder();
        String[] lines = hexDump.split("\n");

        for (String line : lines) {
            // 只处理十六进制部分，跳过偏移量和可读字符部分
            String hexPart = line.substring(9, 9 + 16 * 3).replaceAll(" ", "");
            hexString.append(hexPart);
        }

        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                       + Character.digit(hexString.charAt(i + 1), 16));
        }

        return byteArray;
    }

    /**
     * 判断字符串是否为十六进制字符串
     * @param str 字符串
     * @return true表示是十六进制字符串，false表示不是十六进制字符串
     */
    public static boolean isHexString(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 正则表达式检查是否为十六进制字符串
        return str.matches("^[0-9a-fA-F]+$");
    }

    /**
     * 拼接原始数据和盐值
     * @param data 原始数据
     * @param salt 盐值
     * @return 拼接后的数据
     */
    public static byte[] concatBytes(byte[] data, byte[] salt) {
        byte[] result = Arrays.copyOf(data, data.length + salt.length);
        System.arraycopy(salt, 0, result, data.length, salt.length);
        return result;
    }
}
