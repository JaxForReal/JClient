package com.jaxforreal.jclient;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

//This is the main pane in a Chat node. It is the vertical list of messages people have sent,
//including their nicks and trips on the side.
//needs to be passed the parentChat so it can use the messageinput, etc.
class MessageList extends ScrollPane {
    private final GridPane innerContainer;
    private final Chat parentChat;
    //this is the row that the next message will be inserted into
    private int nextGridRow = 0;
    private TextTransformer transformer;

    MessageList(Chat parentChat) {
        super();
        getStyleClass().addAll("message-list");

        this.parentChat = parentChat;
        this.transformer = new TextTransformer(parentChat);

        innerContainer = new GridPane();
        innerContainer.getStyleClass().add("message-list-inner");

        setContent(innerContainer);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);

        //scroll to bottom when a new message is add
        innerContainer.getChildren().addListener((ListChangeListener<Node>) c -> {
            //only scroll if bar is already at the bottom
            if (getVvalue() == 1.0) {
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
        tripText.getStyleClass().add("trip-text");
        UserDisplay userDisplay = new UserDisplay(message.nick, parentChat.messageTextArea);
        nickTripBox.getChildren().addAll(tripText, userDisplay);
        nickTripBox.setAlignment(Pos.TOP_RIGHT);

        TextFlow text = transformer.transform(message.text);
        text.prefWidthProperty().bind(widthProperty().subtract(250));

        GridPane.setConstraints(nickTripBox, 0, nextGridRow);
        GridPane.setConstraints(text, 1, nextGridRow);

        nextGridRow++;

        innerContainer.getChildren().addAll(nickTripBox, text);
    }

    //add text to messagelist that is not a chat-message (not associated with a nickname)
    //aka info text like join/leave
    void addOtherText(Node newNode) {
        GridPane.setConstraints(newNode, 1, nextGridRow);
        innerContainer.getChildren().add(newNode);

        nextGridRow++;
    }
}
