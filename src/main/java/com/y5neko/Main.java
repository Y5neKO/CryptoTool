package com.y5neko;

import com.seeyon.ctp.util.TextEncoder;
import com.sun.istack.internal.NotNull;
import com.y5neko.asset.FinalshellInit;
import com.y5neko.asset.NavicatInit;
import com.y5neko.decrypt.AES_Decryption;
import com.y5neko.decrypt.DES_Decryption;
import com.y5neko.decrypt.SM4_Decryption;
import com.y5neko.encrypt.AES_Encryption;
import com.y5neko.encrypt.DES_Encryption;
import com.y5neko.encrypt.SM3_Encryption;
import com.y5neko.encrypt.SM4_Encryption;
import com.y5neko.tools.Tools;
import com.y5neko.ui.AboutStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bouncycastle.util.encoders.Hex;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

public class Main extends Application {
    private MenuBar menuBar;

    public static Image icon = new Image("img/icon.png");   // Image对象是通过底层的构造方法获取文件内容，所以可以直接指定基于resource目录的绝对路径

    private double xOffset = 0;
    private double yOffset = 0;

    byte[] exportData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // =============================================================Step 1: 创建一个菜单栏=============================================================

        /*
          设置一个网格视图作为菜单栏
         */
        // 创建一个GridPane并设置其列宽为百分比，以便它们平均分布
        GridPane gridPaneToolBar = new GridPane();gridPaneToolBar.setPadding(new Insets(0, 0, 0, 0));gridPaneToolBar.setHgap(10);gridPaneToolBar.setVgap(10); // 行之间的垂直间距
        // 为GridPane添加三列，并设置它们的百分比宽度
        ColumnConstraints columnToolBar1 = new ColumnConstraints(Region.USE_COMPUTED_SIZE, 33.3, Double.MAX_VALUE);columnToolBar1.setHgrow(Priority.ALWAYS);columnToolBar1.setPercentWidth(33.3);
        ColumnConstraints columnToolBar2 = new ColumnConstraints(Region.USE_COMPUTED_SIZE, 33.4, Double.MAX_VALUE);columnToolBar2.setHgrow(Priority.ALWAYS);columnToolBar2.setPercentWidth(33.4);
        ColumnConstraints columnToolBar3 = new ColumnConstraints(Region.USE_COMPUTED_SIZE, 33.3, Double.MAX_VALUE);columnToolBar3.setHgrow(Priority.ALWAYS);columnToolBar3.setPercentWidth(33.3);
        gridPaneToolBar.getColumnConstraints().addAll(columnToolBar1, columnToolBar2, columnToolBar3);
        HBox.setHgrow(gridPaneToolBar, Priority.ALWAYS);
        // 设置第一个网格为标题栏
        HBox toolBox = new HBox();
        toolBox.setSpacing(2);
        toolBox.setPadding(new Insets(3, 0, 2, 5));
        Image imageIcon = icon;
        ImageView imageViewIcon = new ImageView(imageIcon);
        imageViewIcon.setFitHeight(23);
        imageViewIcon.setPreserveRatio(true);
        buildMenu();
        toolBox.getChildren().add(imageViewIcon);
        toolBox.getChildren().add(menuBar);
        gridPaneToolBar.add(toolBox, 0, 0, 1, 1);
        GridPane.setHalignment(toolBox, HPos.LEFT);
        // 设置第二个网格为标题
        Label titleLabel = new Label("CryptoTool");
        titleLabel.setFont(new Font("Consolas Bold", 20));

        gridPaneToolBar.add(titleLabel, 1, 0, 1, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);
        GridPane.setValignment(titleLabel, VPos.CENTER);
        // 设置第三个网格为窗口操作按钮
        HBox buttonBox = new HBox();
        // 关闭按钮
        Button buttonClose = Tools.getImgButton("img/CloseButton.png");
        buttonClose.setOnAction(e -> {
            primaryStage.close();
            System.exit(0);
        });
        Button buttonMin = Tools.getImgButton("img/MinButton.png");
        buttonMin.setOnAction(e -> primaryStage.setIconified(true));
        Button buttonMax = Tools.getImgButton("img/MaxButton.png");
        buttonMax.setOnAction(e -> {
            e.consume();
            minimizeToTray(primaryStage);
        });
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(buttonClose, buttonMax, buttonMin);
        gridPaneToolBar.add(buttonBox, 2, 0, 1, 1);GridPane.setHalignment(buttonBox, HPos.RIGHT);GridPane.setValignment(buttonBox, VPos.CENTER);

        /*
          创建一个顶部模拟状态栏
         */
        HBox titleBar = new HBox();
        // 绑定拖拽事件
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
        menuBar.setOnMousePressed(this::handleMousePressed);
        menuBar.setOnMouseDragged(this::handleMouseDragged);

        // 添加一个网格视图
        titleBar.getChildren().add(gridPaneToolBar);
        titleBar.setAlignment(Pos.CENTER); // 居中布局
        titleBar.setPadding(new Insets(0, 0, 0, 0));
        titleBar.setSpacing(0);   // 设置标题栏内间距
        titleBar.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        // 设置一个BorderPane作为根视图
        BorderPane root = new BorderPane();
        root.setTop(titleBar);

        // =============================================================Step 2: 创建一个中间容器=============================================================

        // 设置一个HBox作为中间主要展示内容
        VBox centerBox = new VBox();
        centerBox.getStylesheets().add("css/TextField.css");
        centerBox.setPadding(new Insets(10, 10, 10, 10));
        centerBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        // ----------第一层加密方式和密钥----------
        Label cryptoTypeLabel = new Label("加密类型: ");
        ObservableList<String> cryptoType = FXCollections.observableArrayList("SM3", "SM4", "AES", "DES", "Finalshell", "Navicat11", "Navicat12+", "致远数据库");
        ComboBox<String> cryptoTypeComboBox = new ComboBox<>(cryptoType);
//        cryptoTypeComboBox.getSelectionModel().select(0);
        // ----------------------------------------------------------------------
        Label keyLabel = new Label("密钥: ");
        TextField keyTextField = new TextField();
        keyTextField.setPromptText("请输入密钥");
        keyTextField.setPrefWidth(200);
        // ----------------------------------------------------------------------
        Label keyTypeLabel = new Label("密钥类型: ");
        ObservableList<String> keyType = FXCollections.observableArrayList("Hex", "Base64", "UTF-8");
        ComboBox<String> keyTypeComboBox = new ComboBox<>(keyType);
        keyTypeComboBox.getSelectionModel().selectFirst();
        // ----------------------------------------------------------------------
        Label ivLabel = new Label("IV: ");
        TextField ivTextField = new TextField();
        ivTextField.setPromptText("请输入IV");
        ivTextField.setPrefWidth(200);
        // ----------------------------------------------------------------------
        Label ivTypeLabel = new Label("IV类型: ");
        ObservableList<String> ivType = FXCollections.observableArrayList("Hex", "Base64", "UTF-8");
        ComboBox<String> ivTypeComboBox = new ComboBox<>(ivType);
        ivTypeComboBox.getSelectionModel().selectFirst();


        HBox one = new HBox();
        one.getChildren().addAll(cryptoTypeLabel, cryptoTypeComboBox, keyLabel, keyTextField, keyTypeLabel, keyTypeComboBox, ivLabel, ivTextField, ivTypeLabel, ivTypeComboBox);
        one.setAlignment(Pos.CENTER);
        one.setSpacing(10);
        one.setPadding(new Insets(0, 0, 10, 0));

        HBox.setHgrow(cryptoTypeComboBox, Priority.ALWAYS);
        HBox.setHgrow(keyTextField, Priority.ALWAYS);
        HBox.setHgrow(ivTextField, Priority.ALWAYS);
        HBox.setHgrow(one, Priority.ALWAYS);

        // ----------第二层原始数据----------
        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("请输入明文/密文");
        inputTextArea.setWrapText(true);

        HBox two = new HBox();
        two.getChildren().add(inputTextArea);
        two.setPadding(new Insets(0, 0, 10, 0));

        HBox.setHgrow(inputTextArea, Priority.ALWAYS);
        HBox.setHgrow(two, Priority.ALWAYS);
        VBox.setVgrow(two, Priority.ALWAYS);

        // ----------第三层加密按钮----------
        Button encryptButton = new Button(" 加密 ");
        Button decryptButton = new Button(" 解密 ");
        Button uploadButton = new Button("上传文件");
        Button exportButton = new Button("导出结果");
        // ----------------------------------------------------------------------
        Label inputDataTypeLabel = new Label("输入数据格式: ");
        ObservableList<String> inputDataType = FXCollections.observableArrayList("Hex", "Base64", "UTF-8", "Hexdump");
        ComboBox<String> inputDataTypeComboBox = new ComboBox<>(inputDataType);
        inputDataTypeComboBox.getSelectionModel().select(2);
        // ----------------------------------------------------------------------
        Label outputDataTypeLabel = new Label("输出数据格式: ");
        ObservableList<String> outputDataType = FXCollections.observableArrayList("Hex", "Base64", "UTF-8", "Hexdump");
        ComboBox<String> outputDataTypeComboBox = new ComboBox<>(outputDataType);
        outputDataTypeComboBox.getSelectionModel().selectFirst();
        // ----------------------------------------------------------------------

        HBox three = new HBox();
        three.getChildren().addAll(uploadButton, exportButton, inputDataTypeLabel, inputDataTypeComboBox, outputDataTypeLabel, outputDataTypeComboBox, encryptButton, decryptButton);
        three.setAlignment(Pos.CENTER);
        three.setSpacing(10);
        three.setPadding(new Insets(0, 0, 10, 0));

        HBox.setHgrow(three, Priority.ALWAYS);

        // ----------第四层加密结果----------
        TextArea outputTextArea = new TextArea();
//        outputTextArea.editableProperty().set(false);
        outputTextArea.setWrapText(true);

        HBox four = new HBox();
        four.getChildren().add(outputTextArea);
        four.setPadding(new Insets(0, 0, 10, 0));

        HBox.setHgrow(outputTextArea, Priority.ALWAYS);
        VBox.setVgrow(four, Priority.ALWAYS);

        // ----------第五层其他设置----------
        Label modeLabel = new Label("模式: ");
        ObservableList<String> mode = FXCollections.observableArrayList("CBC", "ECB", "CFB", "OFB", "CTR", "GCM");
        ComboBox<String> modeComboBox = new ComboBox<>(mode);
        modeComboBox.getSelectionModel().selectFirst();
        // ----------------------------------------------------------------------
        Label paddingLabel = new Label("填充: ");
        ObservableList<String> padding = FXCollections.observableArrayList("PKCS7Padding", "ZeroPadding", "ISO10126d2Padding", "ANSIX923Padding");
        ComboBox<String> paddingComboBox = new ComboBox<>(padding);
        paddingComboBox.getSelectionModel().selectFirst();
        // ----------------------------------------------------------------------
        Label macLenLabel = new Label("标签长度: ");
        TextField macLenTextField = new TextField();
        macLenTextField.setPromptText("128/96/64");
        macLenTextField.setText("128");
        macLenTextField.setPrefWidth(100);
        // ----------------------------------------------------------------------
        Label blockSizeLabel = new Label("块大小: ");
        TextField blockSizeTextField = new TextField();
        blockSizeTextField.setText("8");
        blockSizeTextField.setPromptText("仅OFB、CFB");
        blockSizeTextField.setPrefWidth(100);
        // ----------------------------------------------------------------------
        Label saltLabel = new Label("盐值: ");
        TextField saltTextField = new TextField();
        saltTextField.setPromptText("请输入盐值(非必须)");
        HBox.setHgrow(saltTextField, Priority.ALWAYS);
        saltTextField.setPrefWidth(200);
        // ----------------------------------------------------------------------
        Label saltTypeLabel = new Label("盐值类型: ");
        ObservableList<String> saltType = FXCollections.observableArrayList("Hex", "Base64", "UTF-8");
        ComboBox<String> saltTypeComboBox = new ComboBox<>(saltType);
        saltTypeComboBox.getSelectionModel().selectFirst();
        // ----------------------------------------------------------------------

        HBox five = new HBox();
        five.getChildren().addAll(modeLabel, modeComboBox, paddingLabel, paddingComboBox, macLenLabel, macLenTextField, blockSizeLabel, blockSizeTextField, saltLabel, saltTextField, saltTypeLabel, saltTypeComboBox);
        five.setAlignment(Pos.CENTER);
        five.setSpacing(10);
        five.setPadding(new Insets(0, 0, 10, 0));

        // 最后手动排序所有层数
        centerBox.getChildren().addAll(one, five, two, three, four);
        // =============================================================Step 3: 创建一个底部栏=============================================================

        root.setCenter(centerBox);
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(2, 5, 2, 5));
        bottomBox.setStyle("-fx-background-color: #99ccff;");
        bottomBox.setAlignment(Pos.CENTER_RIGHT);
        Label bottomLabel = new Label("Powered by Y5neKO");
        bottomBox.getChildren().add(bottomLabel);
        root.setBottom(bottomBox);

        // =============================================================Step 4: 外观设计=============================================================
        /*
          圆角设计
         */
        javafx.scene.shape.Rectangle clipRectangle = new Rectangle();
        clipRectangle.setArcWidth(20);
        clipRectangle.setArcHeight(20);
        clipRectangle.setWidth(1300);
        clipRectangle.setHeight(800);
        root.setClip(clipRectangle);

        // =============================================================Step 5: 处理Scene和Stage=============================================================
        /*
          开始处理Stage和放入Scene
         */
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);   // 圆角|透明
        scene.getStylesheets().add("css/Style.css");
        // 设置stage为无状态栏型
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT); // 圆角|透明
        primaryStage.setScene(scene);
        primaryStage.setTitle("CryptoTool");
        primaryStage.setHeight(800);
        primaryStage.setWidth(1300);
        primaryStage.getIcons().add(icon);

        primaryStage.show();
//        ScenicView.show(scene);

        // =============================================================Step 6: 处理监听事件=============================================================
        // disable初始化
        cryptoTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("SM3")){
                setDisableFalse(centerBox);
                keyTypeComboBox.setDisable(true);
                ivTypeComboBox.setDisable(true);
                keyTextField.setDisable(true);
                ivTextField.setDisable(true);
                decryptButton.setDisable(true);
                modeComboBox.setDisable(true);
                paddingComboBox.setDisable(true);
                blockSizeTextField.setDisable(true);
                macLenTextField.setDisable(true);
                inputTextArea.setPromptText("SM3是中华人民共和国政府采用的一种密码散列函数标准，由国家密码管理局于2010年12月17日发布。相关标准为“GM/T 0004-2012 《SM3密码杂凑算法》”。在商用密码体系中，SM3主要用于数字签名及验证、消息认证码生成及验证、随机数生成等，其算法公开。据国家密码管理局表示，其安全性及效率与SHA-256相当。");
            } else if (newValue.equals("SM4")){
                setDisableFalse(centerBox);
                saltTextField.setDisable(true);
                inputTextArea.setPromptText("SM4.0（原名SMS4.0）由国家密码管理局于2012年3月21日发布。相关标准为 GM/T 0002-2012《SM4分组密码算法》（原SMS4分组密码算法）。分组长度与密钥长度均为128bit（即16Byte）。");
            } else if (newValue.equals("AES")) {
                setDisableFalse(centerBox);
                inputTextArea.setPromptText("密码学中的高级加密标准（Advanced Encryption Standard，AES），又称Rijndael加密法，是美国联邦政府采用的一种区块加密标准,采用对称分组密码体制，密钥长度支持为128/192/256bits。");
            } else if (newValue.equals("DES")) {
                setDisableFalse(centerBox);
                macLenTextField.setDisable(true);
                inputTextArea.setPromptText("DES全称为Data Encryption Standard，即数据加密标准，是一种使用密钥加密的块算法。支持3DES双倍和三倍的密钥。密钥长度分别为支持为128/192位。");
            } else if (newValue.equals("Finalshell")) {
                setDisableFalse(centerBox);
                keyTextField.setDisable(true);
                ivTextField.setDisable(true);
                keyTypeComboBox.setDisable(true);
                ivTypeComboBox.setDisable(true);
                modeComboBox.setDisable(true);
                paddingComboBox.setDisable(true);
                macLenTextField.setDisable(true);
                blockSizeTextField.setDisable(true);
                saltTextField.setDisable(true);
                saltTypeComboBox.setDisable(true);
                inputDataTypeComboBox.setDisable(true);
                outputDataTypeComboBox.setDisable(true);
                inputDataTypeComboBox.getSelectionModel().select("UTF-8");
                outputDataTypeComboBox.getSelectionModel().select("UTF-8");
                inputTextArea.setPromptText("支持Finalshell加解密");
            } else if (newValue.equals("Navicat11") || newValue.equals("Navicat12+")) {
                setDisableFalse(centerBox);
                keyTextField.setDisable(true);
                ivTextField.setDisable(true);
                keyTypeComboBox.setDisable(true);
                ivTypeComboBox.setDisable(true);
                modeComboBox.setDisable(true);
                paddingComboBox.setDisable(true);
                macLenTextField.setDisable(true);
                blockSizeTextField.setDisable(true);
                saltTextField.setDisable(true);
                saltTypeComboBox.setDisable(true);
                inputDataTypeComboBox.setDisable(true);
                outputDataTypeComboBox.setDisable(true);
                inputDataTypeComboBox.getSelectionModel().select("UTF-8");
                outputDataTypeComboBox.getSelectionModel().select("UTF-8");
                inputTextArea.setPromptText("支持Navicat11/12+加解密");
            } else if (newValue.equals("致远数据库")) {
                setDisableFalse(centerBox);
                keyTextField.setDisable(true);
                ivTextField.setDisable(true);
                keyTypeComboBox.setDisable(true);
                ivTypeComboBox.setDisable(true);
                modeComboBox.setDisable(true);
                paddingComboBox.setDisable(true);
                macLenTextField.setDisable(true);
                blockSizeTextField.setDisable(true);
                saltTextField.setDisable(true);
                saltTypeComboBox.setDisable(true);
                inputDataTypeComboBox.setDisable(true);
                outputDataTypeComboBox.setDisable(true);
                inputDataTypeComboBox.getSelectionModel().select("UTF-8");
                outputDataTypeComboBox.getSelectionModel().select("UTF-8");
                inputTextArea.setPromptText("致远数据库加解密");
            }
        });

        // 加密按钮逻辑
        encryptButton.setOnAction(event -> {
            byte[] encrypted_data;
            // 获取待加密的原始数据
            String decrypted_data = inputTextArea.getText();
            // 获取加密类型
            String cryptoType1 = cryptoTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取盐值
            String salt1 = saltTextField.getText();
            // 获取盐值类型
            String saltType1 = saltTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取密钥
            String key1 = keyTextField.getText();
            // 获取密钥类型
            String keyType1 = keyTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取IV
            String iv1 = ivTextField.getText();
            // 获取IV类型
            String ivType1 = ivTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取模式
            String mode1 = modeComboBox.getSelectionModel().getSelectedItem();
            // 获取输入格式
            String decryptedDataType1 = inputDataTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取输出格式
            String encryptedDataType1 = outputDataTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取填充方式
            String padding1 = paddingComboBox.getSelectionModel().getSelectedItem();
            // 获取块大小
            String blockSize1 = blockSizeTextField.getText();
            // 获取认证标签长度
            String macLen1 = macLenTextField.getText();
            try {
                // 排除null
                if (cryptoType1 == null) {
                    cryptoType1 = "";
                }
                // 判断加密过程
                if (cryptoType1.equals("SM3")) {
                    encrypted_data = SM3_Encryption.sm3Encrypt(decrypted_data, decryptedDataType1, salt1, saltType1);
                }
                else if (cryptoType1.equals("SM4")){
                    encrypted_data = SM4_Encryption.sm4Encrypt(decrypted_data, decryptedDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1);
                }
                else if (cryptoType1.equals("AES")) {
                    encrypted_data = AES_Encryption.aesEncrypt(decrypted_data, decryptedDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1, macLen1);
                }
                else if (cryptoType1.equals("DES")){
                    encrypted_data = DES_Encryption.desEncrypt(decrypted_data, decryptedDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1);
                }
                else if (cryptoType1.equals("Finalshell")) {
                    encrypted_data = FinalshellInit.encodePass(decrypted_data).getBytes();
                }
                else if (cryptoType1.equals("Navicat11")) {
                    NavicatInit navicat = new NavicatInit(11);
                    encrypted_data = navicat.encrypt(decrypted_data).getBytes();
                }
                else if (cryptoType1.equals("Navicat12+")) {
                    NavicatInit navicat = new NavicatInit(12);
                    encrypted_data = navicat.encrypt(decrypted_data).getBytes();
                } else if (cryptoType1.equals("致远数据库")) {
                    encrypted_data = TextEncoder.encode(decrypted_data).getBytes();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("错误");
                    alert.setHeaderText("未选择加密方式");
                    alert.setContentText("请选择加密方式");
                    alert.showAndWait();
                    return;
                }
                // 输出格式
                switch (encryptedDataType1) {
                    case "Hex":
                        outputTextArea.setText(Hex.toHexString(encrypted_data));
                        break;
                    case "Base64":
                        outputTextArea.setText(Base64.getEncoder().encodeToString(encrypted_data));
                        break;
                    case "UTF-8":
                        outputTextArea.setText(new String(encrypted_data));
                        break;
                    case "Hexdump":
                        outputTextArea.setText(Tools.toHexDump(encrypted_data));
                        break;
                }
                exportData = encrypted_data;
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("加密失败, 原因如下: ");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        // 处理解密按钮
        decryptButton.setOnAction(event -> {
            byte[] decrypted_data;
            // 获取加密的原始数据
            String encrypted_data = inputTextArea.getText();
            // 获取加密类型
            String cryptoType1 = cryptoTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取盐值
            String salt1 = saltTextField.getText();
            // 获取盐值类型
            String saltType1 = saltTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取密钥
            String key1 = keyTextField.getText();
            // 获取密钥类型
            String keyType1 = keyTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取IV
            String iv1 = ivTextField.getText();
            // 获取IV类型
            String ivType1 = ivTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取模式
            String mode1 = modeComboBox.getSelectionModel().getSelectedItem();
            // 获取输入格式
            String inputDataType1 = inputDataTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取输出格式
            String outputDataType1 = outputDataTypeComboBox.getSelectionModel().getSelectedItem();
            // 获取填充方式
            String padding1 = paddingComboBox.getSelectionModel().getSelectedItem();
            // 获取块大小
            String blockSize1 = blockSizeTextField.getText();
            // 获取认证标签长度
            String macLen1 = macLenTextField.getText();
            // 未输入数据的情况
            if (encrypted_data.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("解密失败, 原因如下: ");
                alert.setContentText("请输入密文!");
                alert.showAndWait();
                return;
            }
            System.out.println(encrypted_data);
            try {
                // 排除null
                if (cryptoType1 == null) {
                    cryptoType1 = "";
                }
                // 判断加密过程
                if (cryptoType1.equals("SM4")){
                    decrypted_data = SM4_Decryption.sm4Decrypt(encrypted_data, inputDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1);
                }
                else if (cryptoType1.equals("AES")) {
                    decrypted_data = AES_Decryption.aesDecrypt(encrypted_data, inputDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1, macLen1);
                }
                else if (cryptoType1.equals("DES")) {
                    decrypted_data = DES_Decryption.desDecrypt(encrypted_data, inputDataType1, salt1, saltType1, key1, keyType1, mode1, padding1, blockSize1, iv1, ivType1);
                }
                else if (cryptoType1.equals("Finalshell")) {
                    decrypted_data = FinalshellInit.decodePass(encrypted_data).getBytes();
                }
                else if (cryptoType1.equals("Navicat11")) {
                    NavicatInit navicat = new NavicatInit(11);
                    decrypted_data = navicat.decrypt(encrypted_data).getBytes();
                }
                else if (cryptoType1.equals("Navicat12+")) {
                    NavicatInit navicat = new NavicatInit(12);
                    decrypted_data = navicat.decrypt(encrypted_data).getBytes();
                }
                else if (cryptoType1.equals("致远数据库")) {
                    decrypted_data = TextEncoder.decode(encrypted_data).getBytes();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("错误");
                    alert.setHeaderText("未选择解密方式");
                    alert.setContentText("请选择解密方式");
                    alert.showAndWait();
                    return;
                }
                // 输出格式
                switch (outputDataType1) {
                    case "Hex":
                        outputTextArea.setText(Hex.toHexString(decrypted_data));
                        break;
                    case "Base64":
                        outputTextArea.setText(Base64.getEncoder().encodeToString(decrypted_data));
                        break;
                    case "UTF-8":
                        outputTextArea.setText(new String(decrypted_data));
                        break;
                    case "Hexdump":
                        outputTextArea.setText(Tools.toHexDump(decrypted_data));
                        break;
                }
                exportData = decrypted_data;
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("错误");
                alert.setHeaderText("解密失败, 原因如下: ");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        // 处理拖拽文件
        inputTextArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    inputTextArea.setText(content);
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("错误");
                    alert.setHeaderText("读取文件失败, 原因如下: ");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // 处理上传按钮
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择文件");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有文件", "*.*"));
            inputDataTypeComboBox.getSelectionModel().select("Hex");

            // 打开文件选择对话框
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                inputTextArea.setText("Selected file: " + file.getName());
                try {
                    // 读取文件内容并以hex显示在 TextArea 中
                    String content = Hex.toHexString(Files.readAllBytes(file.toPath()));
                    inputTextArea.setText(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 处理导出按钮
        exportButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存文件");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有文件", "*.*"));
            // 获取输出格式
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                if (outputDataTypeComboBox.getSelectionModel().getSelectedItem().equals("UTF-8")){
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        // 将字节数组写入文件
                        fos.write(exportData);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try (FileWriter writer = new FileWriter(file)) {
                    // 将文本区域内容写入文件
                    writer.write(outputTextArea.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 构建顶部菜单
     */
    private void buildMenu(){
        menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: transparent;");
        menuBar.setPadding(new Insets(0));
        Menu settingMenu = new Menu("设置");
        Menu helpMenu = new Menu("帮助");
        menuBar.getMenus().addAll(settingMenu, helpMenu);
        // ----------第一个按钮----------
        MenuItem settingProxy = new MenuItem("主题设置(暂不支持)");
        settingMenu.getItems().addAll(settingProxy);
        settingProxy.setOnAction(event -> System.out.println("主题设置"));
        // ----------第二个按钮----------
        MenuItem about = new MenuItem("关于");
        helpMenu.getItems().addAll(about);
        about.setOnAction(event -> new AboutStage());
        // ----------第三个按钮----------
//        MenuItem checkUpdate = new MenuItem("检查更新");
//        helpMenu.getItems().add(checkUpdate);
//        checkUpdate.setOnAction(event -> System.out.println("检查更新"));
        // ----------第四个按钮----------
        MenuItem plugins = new MenuItem("插件(暂不支持)");
        settingMenu.getItems().add(plugins);
    }

    /**
     * 处理鼠标按下事件
      */
    private void handleMousePressed(MouseEvent event) {
        // 获取鼠标相对于窗口的坐标
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * 处理鼠标拖动事件
     * @param event 鼠标事件
     */
    private void handleMouseDragged(MouseEvent event) {
        // 计算窗口的新位置（基于鼠标移动的距离）
        double newX = event.getScreenX() - xOffset;
        double newY = event.getScreenY() - yOffset;

        // 移动窗口到新位置
        Stage stage = (Stage) ((event.getSource() instanceof Node) ? ((Node) event.getSource()).getScene().getWindow() : null);
        if (stage != null) {
            stage.setX(newX);
            stage.setY(newY);
        }
    }

    /**
     * 将JavaFX Stage最小化到系统托盘
     * @param primaryStage JavaFX Stage对象
     */
    private void minimizeToTray(Stage primaryStage) {
        Platform.setImplicitExit(false);
        // 隐藏JavaFX Stage
        primaryStage.hide();

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(icon, null);

        TrayIcon trayIcon = getTrayIcon(primaryStage, bufferedImage);
        trayIcon.setImageAutoSize(true);

        // 检查系统托盘是否可用并添加图标
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * 获取TrayIcon对象
     * @param primaryStage JavaFX Stage对象
     * @param bufferedImage BufferedImage对象
     * @return TrayIcon对象
     */
    private static @NotNull TrayIcon getTrayIcon(Stage primaryStage, BufferedImage bufferedImage) {
        PopupMenu popup = new PopupMenu();
        java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        popup.add(exitItem);

        // 创建TrayIcon并添加事件监听器
        TrayIcon trayIcon = new TrayIcon(bufferedImage, "RadiantKnightExploit", popup);
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // 双击托盘图标时恢复窗口
                    Platform.runLater(primaryStage::show);
                    SystemTray.getSystemTray().remove(trayIcon);
                }
            }
        });
        return trayIcon;
    }

    /**
     * 递归启用所有节点
     * @param parent 父节点
     */
    private static void setDisableFalse(Parent parent){
        for (Node node : parent.getChildrenUnmodifiable()){
            node.setDisable(false);
            if (node instanceof Parent){
                setDisableFalse((Parent) node);
            }
        }
    }
}