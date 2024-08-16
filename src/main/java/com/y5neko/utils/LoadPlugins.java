package com.y5neko.utils;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class LoadPlugins extends Application {
    // 插件目录
    String pluginPath = "plugins";
    // 创建File对象
    File directory = new File(pluginPath);
    // 创建一个字符串数组用以储存插件名
    String[] pluginNames = new String[0];
    // 插件名索引
    int index = 0;

    /**
     * 加载插件
     */
    public LoadPlugins() {
        // 遍历目录下的所有文件
        if (directory.isDirectory()) {
            // 获取目录下的所有文件
            File[] files = directory.listFiles();
            if (files != null) {
                // ----------计数----------
                pluginNames = new String[files.length];
                // -----------------------
                for (File file : files) {
                    // 只处理文件，忽略子文件夹
                    if (file.isFile()) {
                        // 获取文件名并去除后缀
                        String fileName = file.getName();
                        int lastDotIndex = fileName.lastIndexOf('.');
                        if (lastDotIndex != -1) {
                            fileName = fileName.substring(0, lastDotIndex);
                        }
                        // 打印文件名（不含后缀）
                        pluginNames[index] = fileName;
                        index = index + 1;
                    }
                }
            } else {
                System.out.println("The directory is empty or an I/O error occurred.");
            }
        } else {
            System.out.println("The specified path is not a directory.");
        }
    }

    /**
     * 获取所有的插件文件名
     * @return 插件文件名数组
     */
    public String[] getPluginNames(){
        return pluginNames;
    }

    /**
     * 加载所有插件
     */
    public static List<Class<?>> loadPlugin(){
        // 创建一个列表，接收所有的插件类
        List<Class<?>> plugins = new ArrayList<>();

        // 获取所有的插件类名
        LoadPlugins loadPlugins = new LoadPlugins();
        List<String> pluginNamesList = new ArrayList<>();
        for (int plguginNameIndex = 0; plguginNameIndex < loadPlugins.getPluginNames().length; plguginNameIndex++) {
            pluginNamesList.add("com.y5neko.plugin." + loadPlugins.getPluginNames()[plguginNameIndex]);
        }

        System.out.println("所有的插件完全限定名："+pluginNamesList);

        for (int pluginIndex = 0; pluginIndex < pluginNamesList.size(); pluginIndex++) {
            try {
                // 将插件JAR文件转换为URL
                File pluginFile = new File("plugins/" + loadPlugins.getPluginNames()[pluginIndex] + ".jar");
                System.out.println("当前处理："+"plugins/" + loadPlugins.getPluginNames()[pluginIndex] + ".jar");
                URL pluginURL = pluginFile.toURI().toURL();


                // 创建URLClassLoader加载JAR
                URLClassLoader classLoader = new URLClassLoader(new URL[] { pluginURL });
                // 加载插件类
                Class<?> pluginClass = classLoader.loadClass(pluginNamesList.get(pluginIndex));

                if (Plugins.class.isAssignableFrom(pluginClass)){
                    plugins.add(pluginClass);
                } else {
                    System.out.println("未实现接口");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return plugins;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<Class<?>> plugins = loadPlugin();
        System.out.println("所有插件："+plugins);
        int count = 1;

        // 开始调用插件
        for (Class<?> plugin : plugins) {
            // 获取插件实例
            Constructor<?> constructor = plugin.getConstructor();
            Object instance = constructor.newInstance();
            Plugins pluginInstance = (Plugins) instance;
            // 获取插件信息
            System.out.println("插件名称: "+pluginInstance.getPluginName());
            System.out.println("插件版本: "+pluginInstance.getPluginVersion());
            System.out.println("插件描述: "+pluginInstance.getPluginDescription());
            System.out.println("插件作者: "+pluginInstance.getAuthor());
            System.out.println("插件索引: "+count);
            // 调用插件的showStage方法
            Method methodShow = plugin.getMethod("showStage");
            methodShow.invoke(instance);
            count = count + 1;
        }
    }
}
