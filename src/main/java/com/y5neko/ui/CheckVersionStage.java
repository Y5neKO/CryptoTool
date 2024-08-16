package com.y5neko.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class CheckVersionStage extends Stage {
    public CheckVersionStage(HashMap<String, String> checkVersionInfo) {
        // 创建一个新的Stage作为“检查更新”窗口
        this.setTitle("检查更新");
        this.initOwner(getOwner());

        // 创建一个VBox来布局内容
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setMinSize(300, 150);
        vbox.setAlignment(Pos.CENTER);

        // ----------共享组件----------
        Font _25Font = Font.font("Consolas", FontWeight.BOLD, 25);
        Font _15Font = Font.font("Consolas", FontWeight.BOLD, 15);
        Font _10Font = Font.font("Consolas", FontWeight.NORMAL, 15);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: rgba(255,0,0,0.53); -fx-text-fill: white; -fx-font-size: 10pt;");
        cancelButton.setOnAction(e -> this.close());

        // ----------处理是否更新----------
        // 不需要更新
        if (checkVersionInfo.get("isNewVersion").equals("true")){
            Label isNewVersionLabel = new Label("当前已是最新版\n\n\n\n");
            isNewVersionLabel.setFont(_15Font);
            vbox.getChildren().addAll(isNewVersionLabel, cancelButton);
        }
        // 需要更新
        else if (checkVersionInfo.get("isNewVersion").equals("false")){
            Label isNewVersionLabel = new Label("发现新版本：" + checkVersionInfo.get("latestVersion"));
            Label descriptionLabel = new Label(checkVersionInfo.get("description"));
            isNewVersionLabel.setFont(_15Font);
            descriptionLabel.setFont(_10Font);
            // ----------创建下载按钮----------
            Button downloadButton = new Button("Download");
            downloadButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 10pt;");
            downloadButton.setOnAction(e -> {
                // 打开浏览器下载最新版本
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(checkVersionInfo.get("downloadUrl")));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                this.close();
            });
            // ----------最底层按钮----------
            HBox buttonsHBox = new HBox(10);
            buttonsHBox.setAlignment(Pos.CENTER);
            buttonsHBox.getChildren().addAll(cancelButton, downloadButton);

            vbox.getChildren().addAll(isNewVersionLabel, descriptionLabel, buttonsHBox);
        }
        // 未知错误
        else if (checkVersionInfo.get("isNewVersion").equals("unkonwn")){
            Label isNewVersionLabel = new Label("检查失败：网络错误，请自行查看发布页面\nhttps://github.com/Y5neKO/ShiroEXP/releases\n\n\n");
            isNewVersionLabel.setFont(_15Font);
            vbox.getChildren().addAll(isNewVersionLabel, cancelButton);
        }
        Scene scene = new Scene(vbox);
        this.setScene(scene);
        this.getIcons().add(new Image("img/icon.png"));
        this.setResizable(false);
        this.showAndWait();
    }
}
