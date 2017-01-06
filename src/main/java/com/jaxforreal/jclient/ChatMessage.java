package com.jaxforreal.jclient;

//all of the data recieved from hack.chat server when a chat message comes in
class ChatMessage {
    final String nick;
    final String trip;
    final String text;
    final long time;

    ChatMessage(String nick, String trip, String text, long time) {
        this.nick = nick;
        this.trip = trip;
        this.text = text;
        this.time = time;
    }
}
