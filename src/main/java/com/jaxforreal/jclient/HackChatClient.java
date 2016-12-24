package com.jaxforreal.jclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

//websocket client for hack.chat
abstract class HackChatClient extends WebSocketClient{
    private final String nick;
    private final String pass;
    private final String channel;
    private final Thread pingThread;

    //used to deserialize json
    private final ObjectMapper mapper;

    HackChatClient(URI uri, String nick, String pass, String channel) {
        super(uri);
        this.nick = nick;
        this.pass = pass;
        this.channel = channel;

        mapper = new ObjectMapper();

        //make a new thread that pings the server every 10 seconds
        pingThread = new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                HackChatClient.this.send("{\"cmd\": \"ping\"}");
                //System.out.println("pinging");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    HackChatClient.this.close();
                }
            }
        });
    }

    @Override
    public void onMessage(String message) {
        Map<String, Object> messageData = new HashMap<>();

        try {
            messageData = mapper.readValue(message, messageData.getClass());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (messageData.get("cmd").equals("chat")) {
            onChat((String) messageData.get("text"), (String) messageData.get("nick"), (String) messageData.get("trip"), (long) messageData.get("time"));
        } else {
            onOtherMessage(messageData);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        //send the command to join specified channel
        String joinMessage = "{\"cmd\": \"join\", \"channel\": \"" + channel + "\", \"nick\": \"" + nick + "#" + pass + "\"}";
        send(joinMessage);

        pingThread.start();
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        pingThread.interrupt();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void connect() {
        try {
            setupSSL();
            super.connect();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            setupSSL();
            super.run();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean connectBlocking() throws InterruptedException {
        try {
            setupSSL();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return false;
        }
        return super.connectBlocking();
    }

    //sends the message to chat
    public void sendChat(String message) {
        try {
            String json = mapper.writeValueAsString(new ChatOutputJSON(message));
            super.send(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            close();
        }
    }

    //this method is needed to get keys for wss:// (websocket secure)
    private void setupSSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context;
        context = SSLContext.getInstance("TLS");
        context.init(null, null, null); //null, null, null is the default ssl context
        setWebSocketFactory(new DefaultSSLWebSocketClientFactory(context));
    }

    //fired when someone speaks in chat
    public abstract void onChat(String text, String nick, String trip, long time);

    //this is fired when a message is received where the command is not "chat"
    //examples {cmd: onlineAdd ...} or {cmd: info ...}
    public void onOtherMessage(Map<String, Object> data) {
    }
}