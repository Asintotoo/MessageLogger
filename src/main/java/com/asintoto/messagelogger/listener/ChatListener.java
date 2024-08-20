package com.asintoto.messagelogger.listener;

import com.asintoto.basic.interfaces.AutoRegister;
import com.asintoto.messagelogger.MessageLogger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AutoRegister
public class ChatListener implements Listener {
    private final MessageLogger plugin = MessageLogger.getInstance();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        plugin.getDatabaseManager().addMessage(p, message, date);
    }
}
