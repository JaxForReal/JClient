package com.jaxforreal.jclient;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class ChatList extends ScrollPane {
    VBox innerContainer;

    ChatList() {
        super();
        getStyleClass().addAll("chat-list");

        innerContainer = new VBox(5);
        innerContainer.getStyleClass().add("chat-list-inner");

        setContent(innerContainer);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setHbarPolicy(ScrollBarPolicy.NEVER);

        //scroll to bottom when a new message is added
        innerContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            if(getVvalue() == 1.0) {
                layout();
                setVvalue(1);
            }
        });
        innerContainer.setPadding(new Insets(5));

    }

    void addEntry(ChatMessage message) {
        HBox chatEntry = new HBox(20);
        UserDisplay userDisplay = new UserDisplay(message);
        Text chatMessage = new Text(message.text);
        chatMessage.getStyleClass().add("chat-message");

        chatEntry.getChildren().addAll(userDisplay, chatMessage);

        innerContainer.getChildren().add(chatEntry);
    }
}
