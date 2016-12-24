package com.jaxforreal.jclient;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//encapsulates a full chat interface for a specific channel
class Chat extends BorderPane {
    private MessageList messageList;
    private WebsocketService websocketService;

    private boolean readyToChat = false;

    Chat(String nick, String pass, String channel) {
        try {
            setupFunctionality();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //todo proper error handling here
        }
    }

    void connect() {
        websocketService.start();
        websocketService.getConnectTask().setOnSucceeded(event -> {
            System.out.println("connect event");
            readyToChat = true;
            //make a notification that the user is not yet connected
            Text connected = new Text("Connected");
            connected.getStyleClass().add("connected-text");

            messageList.innerContainer.getChildren().add(connected);
        });

    }

    private void setupFunctionality() throws URISyntaxException {
        messageList = new MessageList();

        //set up input text box
        TextArea messageTextArea = new TextArea();
        messageTextArea.getStyleClass().add("message-input-text-area");
        messageTextArea.setPrefRowCount(2);
        messageTextArea.setWrapText(true);

        //make a notification that the user is not yet connected
        Text notConnected = new Text("Not Connected...");
        notConnected.getStyleClass().add("not-connected-text");

        messageList.innerContainer.getChildren().add(notConnected);

        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    //treat Shift-Enter as newline in textArea
                    messageTextArea.appendText("\n");
                } else {
                    if(readyToChat) {
                        //submit text on enter & clear input
                        websocketService.getClient().sendChat(messageTextArea.getText());
                        messageTextArea.setText("");
                    }
                    event.consume();
                }
            }
        });

        setBottom(messageTextArea);
        setCenter(messageList);

        //print message when it comes in
        websocketService = new WebsocketService(new URI("wss://hack.chat/chat-ws"), "totally_not_logging", "yo", "test");
        websocketService.getMessageService().setOnSucceeded(event -> {
            messageList.addEntry((ChatMessage) event.getSource().getValue());
            websocketService.getMessageService().restart();
        });

        //print info when it comes in
        websocketService.getInfoService().setOnSucceeded(event -> {
            //noinspection unchecked
            messageList.innerContainer.getChildren().add(getInfoText((Map<String, Object>) event.getSource().getValue()));
            websocketService.getInfoService().restart();
        });
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
                //noinspection unchecked
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
