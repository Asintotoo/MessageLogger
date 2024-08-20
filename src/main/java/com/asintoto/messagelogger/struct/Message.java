package com.asintoto.messagelogger.struct;

import lombok.Getter;

@Getter
public class Message {
    private final String message;
    private final String date;

    public Message(String message, String date) {
        this.date = date;
        this.message = message;
    }
}
