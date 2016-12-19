package com.jaxforreal.jclient;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

class ChatList extends VBox {
    ChatList() {
        super(5);
    }

    void addEntry(ChatMessage message) {
        HBox chatEntry = new HBox(20);
        UserDisplay userDisplay = new UserDisplay(message);
        Text messageDisplay = new Text(message.text);
        //messageDisplay.set

        chatEntry.getChildren().addAll(userDisplay, messageDisplay);

        getChildren().add(chatEntry);
    }
}
