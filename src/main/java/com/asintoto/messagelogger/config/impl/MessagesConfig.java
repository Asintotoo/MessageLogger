package com.asintoto.messagelogger.config.impl;

import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.config.BaseConfig;

public class MessagesConfig extends BaseConfig {
    public MessagesConfig(MessageLogger plugin) {
        super(plugin, "messages.yml");
    }
}
