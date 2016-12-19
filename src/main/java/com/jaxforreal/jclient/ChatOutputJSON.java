package com.jaxforreal.jclient;

//gets serialized to JSON to send messages
class ChatOutputJSON {
    @SuppressWarnings("unused")
    public final String cmd = "chat";
    @SuppressWarnings("WeakerAccess")
    public final String text;

    public ChatOutputJSON(String text) {
        this.text = text;
    }
}
