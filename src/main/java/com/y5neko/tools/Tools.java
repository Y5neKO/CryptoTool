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

public class Tools {
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
}
