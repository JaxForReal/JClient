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

        nickText.setFill(getHashedColor(message.nick));
        getChildren().addAll(tripText, nickText);

        setAlignment(Pos.TOP_RIGHT);
        setPrefWidth(150);
    }

    private Color getHashedColor(String text) {
        int hash = text.hashCode();
        /*
        Shift hash to get a certain byte in the rightmost spot.
        AND with 0xFF to ensure the byte is positive.
        Cast to (byte) to truncate it (will be in range 0 - 255)
        Cast to (double) and divide by 512 to get a value from 0 - 0.5
        This ensures the colors are darker
        */
        double r = ((double) ((byte) (hash >>> 24) & 0xFF)) / 255;
        double g = ((double) ((byte) (hash >>> 16) & 0xFF)) / 255;
        double b = ((double) ((byte) (hash >>> 8) & 0xFF)) / 255;
        return new Color(r, g, b, 1);
    }
}
