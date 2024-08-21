package com.asintoto.messagelogger.struct;

import lombok.Getter;

@Getter
public class Message {
    private final String message;
    private final String date;
    private final String playerName;

    public Message(String message, String date, String playerName) {
        this.date = date;
        this.message = message;
        this.playerName = playerName;
    }

    public Message(String message, String date) {
        this(message, date, "unknown");
    }
}
