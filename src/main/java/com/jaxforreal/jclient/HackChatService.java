package com.jaxforreal.jclient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.net.URI;
import java.net.URISyntaxException;

//A JavaFX service for retrieving hack.chat messages
public class HackChatService extends Service<ChatMessage> {
    private HackChatClient client;

    private BooleanProperty isMessageReadyProperty = new SimpleBooleanProperty(false);
    private Property<ChatMessage> chatMessageProperty = new SimpleObjectProperty<>(null);

    public HackChatService (String uri, String nick, String pass, String channel) {
        try {
            client = new HackChatClient(new URI(uri), nick, pass, channel) {
                @Override
                public void onChat(String text, String nick, String trip, long time) {
                    chatMessageProperty.setValue(new ChatMessage(nick, trip, text, time));
                    isMessageReadyProperty.set(true);
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.connect();
    }

    @Override
    protected Task<ChatMessage> createTask() {
        return new Task<ChatMessage>() {
            @Override
            protected ChatMessage call() throws Exception {
                System.out.println("call task");
                //wait for message to be ready
                while(!isMessageReadyProperty.get()) {
                    if(isCancelled()) {
                        break;
                    }
                }
                isMessageReadyProperty.set(false);
                return chatMessageProperty.getValue();
            }
        };
    }

    public HackChatClient getClient() {
        return client;
    }
}
