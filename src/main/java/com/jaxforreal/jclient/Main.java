package com.jaxforreal.jclient;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main extends Application {
    private ChatList chatList;
    private ChatService chatService;

    @Override
    public void start(Stage primaryStage) throws URISyntaxException {
        primaryStage.setTitle("J Client for hack.chat");
        primaryStage.setScene(getGui());
        primaryStage.show();

        chatService = new ChatService(new URI("wss://hack.chat/chat-ws"), "jclient", "yo", "test");

        chatService.getMessageService().setOnSucceeded(event -> {
            chatList.addEntry((ChatMessage) event.getSource().getValue());
            chatService.getMessageService().restart();
        });

        chatService.getInfoService().setOnSucceeded(event -> {
            chatList.innerContainer.getChildren().add(getInfoText((Map<String, Object>) event.getSource().getValue()));
            chatService.getInfoService().restart();
        });

        chatService.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        System.exit(0);
    }

    private Scene getGui() {
        BorderPane root = new BorderPane();
        //root.getStyleClass().add("root");

        chatList = new ChatList();

        TextArea messageTextArea = new TextArea();
        messageTextArea.getStyleClass().add("message-input-text-area");
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

        Scene scene = new Scene(root, 200, 200);
        scene.getStylesheets().add("style.css");

        return scene;
    }

    private Text getInfoText(Map<String, Object> data) {
        Text infoText = new Text();
        //infoText.getStyleClass().add("server-message");

        switch ((String) data.get("cmd")) {
            case "onlineAdd":
                infoText.setText(data.get("nick") + " joined");
                infoText.getStyleClass().add("online-add");
                break;
            case "onlineRemove":
                infoText.setText(data.get("nick") + " left");
                infoText.getStyleClass().add("online-remove");
                break;
            case "onlineSet":
                infoText.setText("Online users: " + ((List<String>) data.get("nicks")).stream().collect(Collectors.joining(", ")));
                infoText.getStyleClass().add("online-set");
                break;
            case "info":
                infoText.setText((String) data.get("text"));
                infoText.getStyleClass().add("server-info");
                break;
            case "warn":
                infoText.setText((String) data.get("text"));
                infoText.getStyleClass().add("server-warn");
                break;
        }
        return infoText;
    }
}
