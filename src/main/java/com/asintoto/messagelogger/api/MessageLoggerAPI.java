package com.asintoto.messagelogger.api;

import com.asintoto.messagelogger.enums.DatabaseType;
import com.asintoto.messagelogger.struct.Message;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageLoggerAPI {
    public CompletableFuture<List<Message>> getMessages(Player p);

    public CompletableFuture<List<Message>> getMessages(String playerName);

    public DatabaseType getDatabaseType();
}
