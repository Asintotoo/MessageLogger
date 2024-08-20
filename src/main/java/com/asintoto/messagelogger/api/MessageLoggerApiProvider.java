package com.asintoto.messagelogger.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MessageLoggerApiProvider {
    public static MessageLoggerAPI getAPI() {
        RegisteredServiceProvider<MessageLoggerAPI> provider = Bukkit.getServicesManager().getRegistration(MessageLoggerAPI.class);

        return provider.getProvider();
    }
}
