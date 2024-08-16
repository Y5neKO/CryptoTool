package com.y5neko.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public class PluginsStage extends Stage {
    /**
     * 创建一个POJO类，表示表格中的一行数据，包含插件名称、版本、作者和描述的字段
     */
    public static class PluginInfo {
        private String name;
        private String version;
        private String author;
        private String description;

        public PluginInfo(String name, String version, String author, String description) {
            this.name = name;
            this.version = version;
            this.author = author;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getAuthor() {
            return author;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 构造函数，创建一个TableView并设置数据
     * @param pluginInfos 插件信息列表
     */
    public PluginsStage(List<String[]> pluginInfos) {
        setTitle("插件列表");

        // 创建TableView
        TableView<PluginInfo> tableView = new TableView<>();

        // 创建列
        TableColumn<PluginInfo, String> nameColumn = createCenterAlignedColumn("插件名称", "name");
        TableColumn<PluginInfo, String> versionColumn = createCenterAlignedColumn("插件版本", "version");
        TableColumn<PluginInfo, String> authorColumn = createCenterAlignedColumn("插件作者", "author");
        TableColumn<PluginInfo, String> descriptionColumn = createWrapTextCenterAlignedColumn("插件描述", "description");


        // 将列添加到TableView
        tableView.getColumns().addAll(nameColumn, versionColumn, authorColumn, descriptionColumn);

        // 从List<String[]>转换为ObservableList<PluginInfo>
        ObservableList<PluginInfo> data = FXCollections.observableArrayList();

        for (String[] info : pluginInfos) {
            data.add(new PluginInfo(info[0], info[1], info[2], info[3]));
        }

        // 设置TableView的数据
        tableView.setItems(data);

        // 自动调整列宽，将剩余宽度分配给第四列
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 设置布局
        VBox vbox = new VBox(tableView);
        vbox.setPrefHeight(300); // 设置VBox的高度为表格高度
        vbox.setPrefWidth(600); // 设置宽度，可以根据需要调整

        // 创建Scene并设置到Stage
        Scene scene = new Scene(vbox);
        setScene(scene);
        getIcons().add(new Image("img/icon.png"));
        show();
    }


    /**
     * 创建居中对齐的TableColumn
     * @param title 标题
     * @param property 属性名
     * @return TableColumn
     */
    private TableColumn<PluginInfo, String> createCenterAlignedColumn(String title, String property) {
        TableColumn<PluginInfo, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(column1 -> new CenterAlignedCell());
        return column;
    }

    /**
     * 创建自动换行并居中对齐的TableColumn
     * @param title 标题
     * @param property 属性名
     * @return TableColumn
     */
    private TableColumn<PluginInfo, String> createWrapTextCenterAlignedColumn(String title, String property) {
        TableColumn<PluginInfo, String> column = new TableColumn<>(title);
        column.setResizable(true);
        column.setPrefWidth(200); // 设置宽度，可以根据需要调整
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(column1 -> new WrapTextCenterAlignedCell());
        return column;
    }

    /**
     * 自定义TableCell，用于居中对齐的普通列
     */
    private static class CenterAlignedCell extends TableCell<PluginInfo, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item);
                setAlignment(Pos.CENTER);
                setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            }
        }
    }

    /**
     * 自定义TableCell，用于自动换行和居中对齐的描述列
     */
    private static class WrapTextCenterAlignedCell extends TableCell<PluginInfo, String> {
        private final Text text = new Text();

        public WrapTextCenterAlignedCell() {
            text.wrappingWidthProperty().bind(widthProperty());
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
                text.setText(item);
                setGraphic(text);
                setAlignment(Pos.CENTER);
                setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            }
        }
    }
}
