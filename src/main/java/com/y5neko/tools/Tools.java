package com.y5neko.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    /**
     * 获取填充方式
     * @param paddingType 填充方式
     * @return 填充方式实例
     */
    public static org.bouncycastle.crypto.paddings.BlockCipherPadding getPadding(String paddingType) {
        switch (paddingType) {
            case "PKCS7Padding":
                return new PKCS7Padding();
            case "ZeroPadding":
                return new ZeroBytePadding();
            case "ISO10126d2Padding":
                return new ISO10126d2Padding();
            case "ANSIX923Padding":
                return new X923Padding();
            default:
                throw new IllegalArgumentException("Unsupported padding type: " + paddingType);
        }
    }

    /**
     * 获取固定配置文件中的属性值
     * @param key 属性名
     * @return 属性值
     * @throws IOException 如果读取配置文件失败
     */
    public static String getProperty(String key) throws IOException {
        // 从配置文件中获取属性值
        Properties properties = new Properties();
        properties.load(Tools.class.getClassLoader().getResourceAsStream("info.properties"));
        return properties.getProperty(key);
    }

    /**
     * 通过github api 获取最新版本
     * @return 最新版本信息
     */
    public static HashMap<String, String> checkVersion() {
        String GITHUB_API_URL = "https://api.github.com/repos/Y5neKO/CryptoTool/releases/latest";
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                // 解析JSON响应
                JSONObject jsonObject = JSONObject.parseObject(content.toString());
                String latestVersion = jsonObject.getString("tag_name");
                String nowVersion = Tools.getProperty("version");
                HashMap<String, String> checkResult = new HashMap<>();
                if (!latestVersion.contains(nowVersion)) {
                    System.out.println(latestVersion);
                    checkResult.put("latestVersion", latestVersion);
                    checkResult.put("isNewVersion", "false");
                } else {
                    checkResult.put("latestVersion", latestVersion);
                    checkResult.put("isNewVersion", "true");
                }
                checkResult.put("description", new String(jsonObject.getString("body").getBytes(), StandardCharsets.UTF_8).replace("\\r\\n", "\\n"));

                JSONArray assetsArray = jsonObject.getJSONArray("assets");
                JSONObject firstAsset = assetsArray.getJSONObject(0);
                String downloadUrl = firstAsset.getString("browser_download_url");
                checkResult.put("downloadUrl", downloadUrl);
                return checkResult;
            } else {
                HashMap<String, String> checkResult = new HashMap<>();
                checkResult.put("isNewVersion", "unkonwn");
                return checkResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, String> checkResult = new HashMap<>();
            checkResult.put("isNewVersion", "unkonwn");
            return checkResult;
        }
    }

    /**
     * 创建文件夹
     * @param dirName 文件夹名
     */
    public static void mkdir(String dirName) {
        // 要创建的文件夹路径
        Path directoryPath = Paths.get(dirName);
        try {
            // 创建文件夹
            if (Files.notExists(directoryPath)) {
                Files.createDirectories(directoryPath); // 创建多级目录
                System.out.println("Directory created successfully: " + directoryPath.toString());
            } else {
                System.out.println("Directory already exists: " + directoryPath.toString());
            }
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
        }
    }

    /**
     * 根据类名在列表中查找Class对象
     *
     * @param classes 类列表
     * @param className 要查找的类名（全限定名）
     * @return 包含找到的Class对象的Optional，如果没有找到则为Optional.empty()
     */
    public static Optional<Class<?>> findClassByName(List<Class<?>> classes, String className) {
        for (Class<?> clazz : classes) {
            if (clazz.getName().equals(className)) {
                return Optional.of(clazz);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据类名在列表中查找Class对象
     * @param plugins 插件列表
     * @param className 类名
     * @return 包含找到的Class对象
     */
    public static Class<?> getClassByName(List<Class<?>> plugins, String className) {
        for (Class<?> clazz : plugins) {
            if (clazz.getName().equals(className) || clazz.getSimpleName().equals(className)) {
                return clazz;
            }
        }
        return null; // 如果找不到匹配的类，则返回null
    }

    /**
     * 查找静态字段
     * @param clazz 类
     * @param fieldName 字段名
     * @return 字段值
     */
    public static String findDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return (String) clazz.getDeclaredField(fieldName).get(null);
        } catch (Exception e) {
            return "获取失败";
        }
    }

    // 计算字符串中子串的出现次数
    public static int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }

    public static String returnIdentifier(String text) {
        // 识别不同类型的换行符
        int unixLineBreaks = countOccurrences(text, "\n") - countOccurrences(text, "\r\n");
        int windowsLineBreaks = countOccurrences(text, "\r\n");

        // 替换所有换行符为统一格式
        return text.replace("\r\n", "\n");
    }
}
