package com.jaxforreal.jclient;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextTransformer {
    private Chat parentChat;
    private Pattern latexPattern = Pattern.compile("\\$[^$]*\\$");

    public TextTransformer(Chat parentChat) {
        this.parentChat = parentChat;
    }

    TextFlow transform(String text) {
        TextFlow flow = processKatex(text);
        processWordTransforms(flow);
        return flow;
    }

    TextFlow processKatex(String text) {
        TextFlow outputFlow = new TextFlow();
        Matcher matcher = latexPattern.matcher(text);

        //where the last matcher.find() left off
        int previousEnd = 0;

        while (matcher.find()) {
            //non-katex string
            Text unmatched = new Text(text.substring(previousEnd, matcher.start()));
            unmatched.getStyleClass().add("plain-text");

            Text matched = new Text("(katex)");
            matched.getStyleClass().add("katex");
            //display katex source as tooltip
            Tooltip katexSource = new Tooltip(matcher.group());
            Tooltip.install(matched, katexSource);
            matched.setOnMouseClicked(e -> parentChat.messageTextArea.appendText(matcher.group()));


            outputFlow.getChildren().addAll(unmatched, matched);
            previousEnd = matcher.end();
        }

        //add final non-katex bit
        outputFlow.getChildren().add(new Text(text.substring(previousEnd, text.length())));

        return outputFlow;
    }

    //todo kms
    private void processWordTransforms(TextFlow input) {
        input.getChildren().stream()
                //only check plain text parts
                .filter(child -> child.getStyleClass().contains("plain-text"))
                .map(child -> (Text) child)
                .forEach(textNode -> {

                });
    }

    private Text processSingleWord(String text) {
        return null;
    }

    boolean isUserOnline(String user) {
        System.out.println("check");
        for (String onlineUser : parentChat.onlineUsernames) {
            if (onlineUser.equalsIgnoreCase(user)) {
                System.out.println(user);
                return true;
            }
        }
        return false;
    }
}
