package com.jaxforreal.jclient;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChatList extends VBox {
    public ChatList() {
        super(5);
    }

    public void addEntry(ChatMessage message) {
        HBox chatEntry = new HBox(20);
        UserDisplay userDisplay = new UserDisplay(message);
        chatEntry.getChildren().addAll(userDisplay, new Text(message.text));

        getChildren().add(chatEntry);
    }
}
