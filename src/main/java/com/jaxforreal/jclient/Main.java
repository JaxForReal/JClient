package com.jaxforreal.jclient;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URISyntaxException;

public class Main extends Application {
    private ChatList chatList;
    private HackChatService chatService;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        primaryStage.setTitle("J Client for hack.chat");
        primaryStage.setScene(new Scene(getGui(), 200, 200));
        primaryStage.show();

        chatService = new HackChatService();
        chatService.setOnSucceeded(event -> {
            chatList.addEntry((ChatMessage) event.getSource().getValue());
            chatService.restart();
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
        return root;
    }
}
