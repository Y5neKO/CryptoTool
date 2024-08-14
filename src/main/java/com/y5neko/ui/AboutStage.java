package com.y5neko.ui;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AboutStage extends Stage {

    public AboutStage() {
        // 创建一个新的Stage作为“关于”窗口
        this.setTitle("关于");
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);
        this.initOwner(getOwner());

        // 创建一个VBox来布局内容
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setOnMouseClicked(e -> this.close());
        vbox.setStyle("-fx-background-color: #2b2d30; -fx-padding: 10;");;

        // 创建一个网格布局
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
//        gridPane.setVgap(10);

        vbox.getChildren().add(gridPane);

        // 添加图标（如果需要）
        ImageView imageView = new ImageView(new Image("img/icon.png", 150, 150, true, true)); // 替换为你的图标路径

        // 添加标题和描述信息
        Label titleLabel = new Label("CT v0.2");
        Label descriptionLabel = new Label("CryptoTool\n综合加解密工具\n\n\n\n\n");
        Label buildInfoLabel = new Label("Build Info: 20240815");
        Label copyrightLabel = new Label("Copyright (c) 2024, Y5neKO. All rights reserved.");
        titleLabel.setTextFill(Color.WHITE);
        descriptionLabel.setTextFill(Color.WHITE);
        buildInfoLabel.setTextFill(Color.WHITE);
        copyrightLabel.setTextFill(Color.WHITE);

        // 设置字体
        Font titleFont = Font.font("Consolas", FontWeight.BOLD, 25);
        Font descriptionFont = Font.font("Consolas", FontWeight.NORMAL, 15);
        Font buildInfoFont = Font.font("Consolas", FontWeight.NORMAL, 10);
        Font copyrightFont = Font.font("Consolas", FontWeight.NORMAL, 10);
        titleLabel.setFont(titleFont);
        descriptionLabel.setFont(descriptionFont);
        buildInfoLabel.setFont(buildInfoFont);
        copyrightLabel.setFont(copyrightFont);

        // 添加控件到网格布局中
        gridPane.add(imageView, 0, 1);  gridPane.add(titleLabel, 4, 1);
                                                            gridPane.add(descriptionLabel, 4, 2);
        gridPane.add(buildInfoLabel, 4, 3);
        gridPane.add(copyrightLabel, 4, 4);

        // 创建一个行列约束
        GridPane.setColumnSpan(imageView, 4);
        GridPane.setRowSpan(imageView, 4);

        // 设置布局和场景
        StackPane layout = new StackPane();
        layout.getChildren().add(vbox);

        // 设置圆角
        layout.setStyle("-fx-background-radius: 10; -fx-background-color: #2b2d30;");
        vbox.setStyle("-fx-background-radius: 10; -fx-background-color: #2b2d30;");

        Scene scene = new Scene(layout); // 设置场景大小和布局

        // 设置为透明填充
        this.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);

        this.setResizable(false);
        this.setScene(scene);

        // 显示“关于”窗口
        this.showAndWait();
//        ScenicView.show(scene);
    }
}
