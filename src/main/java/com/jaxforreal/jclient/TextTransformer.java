package com.jaxforreal.jclient;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class TextTransformer {
    private Chat parentChat;
    private Pattern latexPattern = Pattern.compile("\\s?\\$[^$]*\\$\\s?");

    TextTransformer(Chat parentChat) {
        this.parentChat = parentChat;
    }

    //this takes message text and transforms it into richtext (TextFlow) with latex and stuff like that
    TextFlow transform(String text) {
        Matcher latexMatcher = latexPattern.matcher(text);

        //this separates latex parts into their own Word object
        List<Word> latexSplit = new ArrayList<>();

        //where the last regex find ended
        int lastFindEndLocation = 0;
        while (latexMatcher.find()) {
            latexSplit.add(new Word(false, text.substring(lastFindEndLocation, latexMatcher.start())));
            latexSplit.add(new Word(true, latexMatcher.group().trim()));
            lastFindEndLocation = latexMatcher.end();
        }

        latexSplit.add(new Word(false, text.substring(lastFindEndLocation, text.length())));

        //this separates latex and normal words (space separated)
        List<Word> fullSplit = new ArrayList<>();

        //split non-latex words into their parts and add to fullSplit
        //do nothing to latex words, and add straight away
        latexSplit.forEach(word -> {
            if (word.isLatex) {
                fullSplit.add(word);
            } else {
                Arrays.stream(word.text.split(" "))
                        .forEach(innerWord -> fullSplit.add(new Word(false, innerWord)));
            }
        });

        //map words to text nodes using getTextNode()
        TextFlow output = new TextFlow();
        output.getChildren().addAll(fullSplit.stream().map(this::getTextNode).collect(Collectors.toList()));
        return output;
    }

    private Text getTextNode(Word word) {
        word.text += " ";

        if (word.isLatex) {
            return getLatexTextNode(word.text);
        } else if (isUserOnline(word.text.trim())) {
            return getUserTextNode(word.text);
        } else {
            return new Text(word.text);
        }
    }

    private Text getLatexTextNode(String string) {
        Text text = new Text("latex ");
        Tooltip latexTooltip = new Tooltip(string);
        Tooltip.install(text, latexTooltip);

        text.getStyleClass().add("latex");
        return text;
    }

    private Text getUserTextNode(String user) {
        user = user.trim();

        //remove @ prefix if applicable
        user = user.startsWith("@") ? user.substring(1) : user;
        return new UserDisplay(user, parentChat.messageTextArea);
    }

    private boolean isUserOnline(String user) {
        //remove @ prefix if applicable
        user = user.startsWith("@") ? user.substring(1) : user;
        for (String onlineUser : parentChat.onlineUsernames) {
            if (onlineUser.equalsIgnoreCase(user)) {
                System.out.println(user);
                return true;
            }
        }
        return false;
    }

    private class Word {
        boolean isLatex;
        String text;

        Word(boolean isLatex, String text) {
            this.isLatex = isLatex;
            this.text = text;
        }
    }
}
