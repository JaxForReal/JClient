package com.jaxforreal.jclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URISyntaxException;

public class Main extends Application {
    private MessageList messageList;
    private WebsocketService websocketService;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        primaryStage.setTitle("J Client for hack.chat");

        Chat chat = new Chat("cancer_client", "asdasd", "test");
        Scene scene = new Scene(chat, 200, 200);

        scene.getStylesheets().add("style.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        chat.connect();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }
}
