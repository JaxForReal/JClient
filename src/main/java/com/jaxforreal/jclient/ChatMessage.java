package com.jaxforreal.jclient;

// encapsulates all the data send in a chat message
class ChatMessage {
    final String nick;
    final String trip;
    final String text;
    private final long time;

    ChatMessage(String nick, String trip, String text, long time) {
        this.nick = nick;
        this.trip = trip;
        this.text = text;
        this.time = time;
    }
}
