package com.jaxforreal.jclient;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//encapsulates a full chat interface for a specific channel
class Chat extends BorderPane {
    private MessageList messageList;
    private VBox userList;

    private WebsocketService websocketService;

    private boolean readyToChat = false;

    Chat(String nick, String pass, String channel) {
        try {
            setupFunctionality(nick, pass, channel);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //todo proper error handling here
        }
    }

    void connect() {
        websocketService.start();

        //this task SUCCEEDS when the websocket connects, so we can use setOnSucceeded()
        websocketService.getConnectTask().setOnSucceeded(event -> {
            readyToChat = true;
            //make a notification that the user is connected
            Text connected = new Text("Connected");
            connected.getStyleClass().add("connected-text");

            messageList.addOtherText(connected);
        });

    }

    private void setupFunctionality(String nick, String pass, String channel) throws URISyntaxException {
        messageList = new MessageList();

        //set up input text box
        TextArea messageTextArea = new TextArea();
        messageTextArea.getStyleClass().add("message-input-text-area");
        messageTextArea.setPrefRowCount(2);
        messageTextArea.setWrapText(true);

        messageTextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isShiftDown()) {
                    //treat Shift-Enter as newline in textArea
                    messageTextArea.appendText("\n");
                } else {
                    if (readyToChat) {
                        //submit text on enter & clear input
                        websocketService.getClient().sendChat(messageTextArea.getText());
                        messageTextArea.setText("");
                    }
                    event.consume();
                }
            }
        });

        //make a notification that the user is not yet connected
        Text notConnected = new Text("Not Connected...");
        notConnected.getStyleClass().add("not-connected-text");
        messageList.addOtherText(notConnected);

        //setup the list that tracks which users are online
        userList = new VBox(5);
        userList.getStyleClass().add("user-list");
        userList.setPadding(new Insets(4));

        setBottom(messageTextArea);
        setCenter(messageList);
        setLeft(userList);

        //print message when it comes in
        websocketService = new WebsocketService(new URI("wss://hack.chat/chat-ws"), nick, pass, channel);

        websocketService.getMessageService().setOnSucceeded(event -> {
            messageList.addMessage((ChatMessage) event.getSource().getValue());
            websocketService.getMessageService().restart();
        });

        //print info when it comes in
        websocketService.getInfoService().setOnSucceeded(event -> {
            //noinspection unchecked
            Map<String, Object> data = (Map<String, Object>) event.getSource().getValue();
            messageList.addOtherText(getInfoText(data));


            //add and remove from userList when users join and leave
            switch ((String) data.get("cmd")) {
                case "onlineSet":
                    //noinspection unchecked
                    ((List<String>) data.get("nicks")).forEach(this::addUserToList);
                    break;
                case "onlineAdd":
                    addUserToList((String) data.get("nick"));
                    break;
                case "onlineRemove":
                    //find all the users in list where nick equals the one leaving, and remove them
                    userList.getChildren().stream()
                            .filter(child -> (child instanceof Text) && ((Text) child).getText().equals(data.get("nick")))
                            .forEach(child -> userList.getChildren().remove(child));
                    break;
            }

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

    void addUserToList(String nick) {
        Text newUserText = new Text(nick);
        newUserText.setFill(Util.getHashedColor(nick));
        userList.getChildren().add(newUserText);
    }
}
