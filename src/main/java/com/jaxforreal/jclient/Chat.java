package com.jaxforreal.jclient;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//encapsulates a full chat interface for a specific channel
class Chat extends BorderPane {
    private MessageList messageList;
    private VBox userList;

    List<String> onlineUsernames = new ArrayList<>();
    String nick;
    String pass;
    String channel;

    private WebsocketService websocketService;

    private boolean readyToChat = false;
    TextArea messageTextArea;

    Chat(String nick, String pass, String channel) {
        this.nick = nick;
        this.pass = pass;
        this.channel = channel;

        try {
            setupFunctionality(nick, pass, channel);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //todo proper error handling here
        }
    }

    void connect() {
        getWebsocketService().start();

        //this task SUCCEEDS when the websocket connects, so we can use setOnSucceeded()
        getWebsocketService().getConnectTask().setOnSucceeded(event -> {
            readyToChat = true;
            //make a notification that the user is connected
            Text connected = new Text("Connected");
            connected.getStyleClass().add("connected-text");

            messageList.addOtherText(connected);
        });

    }

    private void setupFunctionality(String nick, String pass, String channel) throws URISyntaxException {
        setMaxWidth(Double.MAX_VALUE);
        setMaxHeight(Double.MAX_VALUE);


        //set up input text box
        messageTextArea = new TextArea();
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
                        getWebsocketService().getClient().sendChat(messageTextArea.getText());
                        messageTextArea.setText("");
                    }
                    event.consume();
                }
            }
        });

        messageList = new MessageList(this);

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

        getWebsocketService().getMessageService().setOnSucceeded(event -> {
            messageList.addMessage((ChatMessage) event.getSource().getValue());
            getWebsocketService().getMessageService().restart();
        });

        //print info when it comes in
        getWebsocketService().getInfoService().setOnSucceeded(event -> {
            //noinspection unchecked
            Map<String, Object> data = (Map<String, Object>) event.getSource().getValue();
            messageList.addOtherText(getInfoText(data));


            //add and remove from userList when users join and leave
            switch ((String) data.get("cmd")) {

                case "onlineSet":
                    //noinspection unchecked
                    ((List<String>) data.get("nicks")).forEach(this::addUserToList);
                    //noinspection unchecked
                    onlineUsernames.addAll((List<String>) data.get("nicks"));
                    break;

                case "onlineAdd":
                    addUserToList((String) data.get("nick"));
                    onlineUsernames.add((String) data.get("nick"));
                    break;

                case "onlineRemove":
                    //find all the users in list where nick equals the one leaving, and remove them
                    userList.getChildren().removeIf(child ->
                            (child instanceof Text) && ((Text) child).getText().equals(data.get("nick"))
                    );
                    onlineUsernames.removeIf(string -> string.equals(data.get("nick")));
                    break;
            }

            getWebsocketService().getInfoService().restart();
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

    private void addUserToList(String nick) {
        userList.getChildren().add(new UserDisplay(nick, messageTextArea));
    }

    WebsocketService getWebsocketService() {
        return websocketService;
    }
}
