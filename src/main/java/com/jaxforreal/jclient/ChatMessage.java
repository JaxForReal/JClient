package com.jaxforreal.jclient;

public class ChatMessage {
    public String nick;
    public String trip;
    public String text;
    public long time;

    public ChatMessage(String nick, String trip, String text, long time) {
        this.nick = nick;
        this.trip = trip;
        this.text = text;
        this.time = time;
    }
}
