package com.jaxforreal.jclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URISyntaxException;

public class Main extends Application {

    private TabPane chatsPane;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        primaryStage.setTitle("J Client for hack.chat");

        VBox root = new VBox();
        chatsPane = new TabPane();
        root.getChildren().addAll(getMenuBar(), chatsPane);

        Scene scene = new Scene(root, 200, 200);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    private MenuBar getMenuBar() {
        MenuBar bar = new MenuBar();

        Menu join = new Menu("Join");
        MenuItem joinChannel = new MenuItem("Channel...");
        MenuItem joinPrivate = new MenuItem("Private Chat...");
        join.getItems().addAll(joinChannel, joinPrivate);

        joinChannel.setOnAction(event -> {
            VBox root = new VBox(5);

            Stage infoPrompt = new Stage();
            infoPrompt.setTitle("Join Options");
            infoPrompt.setScene(new Scene(root));

            TextField nickField = new TextField();
            nickField.setPromptText("Nickname");
            TextField passField = new TextField();
            passField.setPromptText("Password");
            TextField chanField = new TextField();
            chanField.setPromptText("Channel");

            root.getChildren().addAll(nickField, passField, chanField);

            Button submit = new Button("Join");
            submit.setOnAction(event1 -> {
                Chat newChat = new Chat(nickField.getText(), passField.getText(), chanField.getText());
                chatsPane.getTabs().add(new Tab(chanField.getText(), newChat));
                infoPrompt.close();
                newChat.connect();
            });

            root.getChildren().add(submit);

            infoPrompt.show();
        });

        bar.getMenus().add(join);
        return bar;
    }
}
