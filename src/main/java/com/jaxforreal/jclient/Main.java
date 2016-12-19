package com.jaxforreal.jclient;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;

public class Main extends Application {
    private ChatList chatList;
    private ChatService chatService;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        primaryStage.setTitle("J Client for hack.chat");
        primaryStage.setScene(new Scene(getGui(), 200, 200));
        primaryStage.show();

        chatService = new ChatService(new URI("wss://hack.chat/chat-ws"), "jclient", "yo", "test");

        chatService.getMessageService().setOnSucceeded(event -> {
            chatList.addEntry((ChatMessage) event.getSource().getValue());
            chatService.getMessageService().restart();
        });

        chatService.getInfoService().setOnSucceeded(event -> {
            System.out.println(event.getSource().getValue().toString());
            chatService.getInfoService().restart();
        });
        chatService.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Parent getGui() {
        BorderPane root = new BorderPane();
        chatList = new ChatList();

        TextArea messageTextArea = new TextArea();
        messageTextArea.setPrefRowCount(2);
        messageTextArea.setWrapText(true);
        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    //treat Shift-Enter as newline
                    messageTextArea.appendText("\n");
                } else {
                    //submit text on enter & clear input
                    chatService.getClient().sendChat(messageTextArea.getText());
                    messageTextArea.setText("");
                    event.consume();
                }
            }
        });
        root.setBottom(messageTextArea);
        root.setCenter(chatList);

        Menu fileMenu = new Menu("File");
        MenuBar menuBar = new MenuBar(fileMenu);
        root.setTop(menuBar);

        return root;
    }
}
