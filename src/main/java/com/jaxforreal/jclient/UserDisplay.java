package com.jaxforreal.jclient;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class UserDisplay extends HBox {

    UserDisplay(ChatMessage message) {
        super(5);
        getStyleClass().add("user-display");

        Text nickText = new Text(message.nick);
        nickText.getStyleClass().add("nick-text");

        Text tripText = new Text(message.trip);
        tripText.getStyleClass().add("trip-text");

        nickText.setFill(Util.getHashedColor(message.nick));
        getChildren().addAll(tripText, nickText);

        setAlignment(Pos.TOP_RIGHT);
        //setPrefWidth(150);
    }
}
