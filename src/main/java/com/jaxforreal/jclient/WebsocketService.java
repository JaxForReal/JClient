package com.jaxforreal.jclient;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

//exposes the hackChatClient in a JavaFX threadsafe way
class WebsocketService {
    private final Service<ChatMessage> messageService;
    private final Service<Map<String, Object>> infoService;

    private final HackChatClient client;

    private ChatMessage newMessage;
    private boolean isMessageReady;

    private Map<String, Object> newInfo;
    private boolean isInfoReady;

    private final Task<Void> connectTask;
    private boolean isConnected = false;

    WebsocketService(URI uri, String nick, String pass, String channel) {
        client = new HackChatClient(uri, nick, pass, channel) {

            @Override
            public void onChat(String text, String nick, String trip, long time) {
                newMessage = new ChatMessage(nick, trip, text, time);
                isMessageReady = true;
            }

            @Override
            public void onOtherMessage(Map<String, Object> data) {
                newInfo = data;
                isInfoReady = true;
            }

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                super.onOpen(serverHandshake);
                isConnected = true;
            }
        };

        messageService = new MessageService();
        infoService = new InfoService();

        connectTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                //noinspection StatementWithEmptyBody
                while (!isConnected) {
                    if (isCancelled()) {
                        break;
                    }
                }
                return null;
            }
        };
    }

    public Task<Void> getConnectTask() {
        return connectTask;
    }

    private class MessageService extends Service<ChatMessage> {

        @Override
        protected Task<ChatMessage> createTask() {
            return new Task<ChatMessage>() {
                @Override
                protected ChatMessage call() throws Exception {
                    //wait for message to be ready
                    while (!isMessageReady) {
                        if (isCancelled()) {
                            break;
                        }
                    }
                    isMessageReady = false;
                    return newMessage;
                }
            };
        }
    }

    private class InfoService extends Service<Map<String, Object>> {

        @Override
        protected Task<Map<String, Object>> createTask() {
            return new Task<Map<String, Object>>() {
                @Override
                protected Map<String, Object> call() throws Exception {
                    //wait for message to be ready
                    while (!isInfoReady) {
                        if (isCancelled()) {
                            break;
                        }
                    }
                    isInfoReady = false;
                    return newInfo;
                }
            };
        }
    }

    void start() {
        messageService.start();
        infoService.start();

        new Thread(connectTask).start();

        client.connect();
    }

    Service<ChatMessage> getMessageService() {
        return messageService;
    }

    Service<Map<String, Object>> getInfoService() {
        return infoService;
    }

    HackChatClient getClient() {
        return client;
    }
}
