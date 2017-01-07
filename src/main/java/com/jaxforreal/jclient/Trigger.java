package com.jaxforreal.jclient;

import java.util.regex.Pattern;

//users can add these to automatically reply to certain patterns
class Trigger {
    private Pattern triggerPattern;
    private String response;

    Trigger(Pattern triggerPattern, String response) {
        this.triggerPattern = triggerPattern;
        this.response = response;
    }

    //[[TRIGGERED]]
    boolean isTriggered(String input) {
        return triggerPattern.matcher(input).matches();
    }

    String getOutput(String senderNick, String channel) {
        return response
                .replace("%senderNick%", senderNick)
                .replace("%channel%", channel);
    }

}
