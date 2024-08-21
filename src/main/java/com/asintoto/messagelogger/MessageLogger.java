package com.asintoto.messagelogger;

import com.asintoto.basic.BasicPlugin;
import com.asintoto.basic.utils.Common;
import com.asintoto.messagelogger.api.MessageLoggerAPI;
import com.asintoto.messagelogger.commands.LogCommand;
import com.asintoto.messagelogger.config.impl.MessagesConfig;
import com.asintoto.messagelogger.enums.DatabaseType;
import com.asintoto.messagelogger.managers.DatabaseManager;
import com.asintoto.messagelogger.managers.Exporter;
import com.asintoto.messagelogger.managers.Manager;
import com.asintoto.messagelogger.struct.CommandMessages;
import com.asintoto.messagelogger.struct.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class MessageLogger extends BasicPlugin {
    @Getter
    private BukkitCommandHandler commandHandler;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private YamlConfiguration messages;
    @Getter
    private MessageLoggerAPI api;
    @Getter
    private Exporter exporter;

    private MessagesConfig messagesConfig;

    @Override
    public void onPluginEnable() {
        loadConfig();

        this.databaseManager = new DatabaseManager(this);
        this.exporter = new Exporter(this);

        registerCommand();
        setupApi();

        String msg = messages.getString("system.on-enable");
        Common.message(Manager.formatMessage(msg));
    }

    @Override
    public void onPluginDisable() {
        databaseManager.closeConnection();

        String msg = messages.getString("system.on-disable");
        Common.message(Manager.formatMessage(msg));
    }

    private void registerCommand() {
        this.commandHandler = BukkitCommandHandler.create(this);

        commandHandler.getTranslator().add(new CommandMessages(this));
        commandHandler.setLocale(new Locale("en", "US"));

        commandHandler.register(new LogCommand(this));
        commandHandler.registerBrigadier();
    }

    private void loadConfig() {
        saveDefaultConfig();
        messagesConfig = new MessagesConfig(this);
        messages = messagesConfig.getConfig();
    }

    public void reload() {
        messagesConfig.reload();
        messages = messagesConfig.getConfig();
    }

    public static MessageLogger getInstance() {
        return getPlugin(MessageLogger.class);
    }

    private void setupApi() {
        api = new MessageLoggerAPI() {
            @Override
            public CompletableFuture<List<Message>> getMessages(Player p) {
                return databaseManager.getMessages(p);
            }

            @Override
            public CompletableFuture<List<Message>> getMessages(String playerName) {
                return databaseManager.getMessages(playerName);
            }

            @Override
            public DatabaseType getDatabaseType() {
                return databaseManager.getDatabaseType();
            }
        };

        Bukkit.getServicesManager().register(MessageLoggerAPI.class, api, this, ServicePriority.High);
    }
}
