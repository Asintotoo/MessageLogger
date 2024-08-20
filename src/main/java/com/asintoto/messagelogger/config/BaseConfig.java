package com.asintoto.messagelogger.config;

import com.asintoto.messagelogger.MessageLogger;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class BaseConfig {
    @Getter
    private File file;
    @Getter
    private YamlConfiguration config;
    @Getter
    private String fileName;

    protected final MessageLogger plugin;

    public BaseConfig(MessageLogger plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        file = new File(plugin.getDataFolder(), fileName);

        if(!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

}
