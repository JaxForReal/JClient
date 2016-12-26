package com.jaxforreal.jclient;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

class MessageList extends ScrollPane {
    final GridPane innerContainer;

    //this is the row that the next message will be inserted into
    int nextGridRow = 0;

    MessageList() {
        super();
        getStyleClass().addAll("message-list");

        innerContainer = new GridPane();
        innerContainer.getStyleClass().add("message-list-inner");

        setContent(innerContainer);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setHbarPolicy(ScrollBarPolicy.NEVER);

        //scroll to bottom when a new message is added
        innerContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            //only scroll if bar is already at the bottom
            if(getVvalue() == 1.0) {
                layout();
                setVvalue(1);
            }
        });
        innerContainer.setPadding(new Insets(5));
        innerContainer.setHgap(10);
        innerContainer.setVgap(5);
    }

    void addMessage(ChatMessage message) {
        //a HBox to put trip next to nick
        HBox nickTripBox = new HBox(5);
        Text tripText = new Text(message.trip);
        UserDisplay userDisplay = new UserDisplay(message.nick);
        nickTripBox.getChildren().addAll(tripText, userDisplay);

        Text textDisplay = new Text(message.text);
        textDisplay.getStyleClass().add("chat-message");

        GridPane.setConstraints(nickTripBox, 0, nextGridRow);
        GridPane.setConstraints(textDisplay, 1, nextGridRow);

        nextGridRow ++;

        innerContainer.getChildren().addAll(nickTripBox, textDisplay);

        textDisplay.wrappingWidthProperty().bind(widthProperty().subtract(250));
    }

    //add text to messagelist that is not a chat-message (not associated with a nickname)
    void addOtherText(Node newNode) {
        GridPane.setConstraints(newNode, 1, nextGridRow);
        innerContainer.getChildren().add(newNode);

        nextGridRow ++;
    }
}
