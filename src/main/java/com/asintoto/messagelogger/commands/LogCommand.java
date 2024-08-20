package com.asintoto.messagelogger.commands;


import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.api.MessageLoggerApiProvider;
import com.asintoto.messagelogger.managers.Manager;
import com.asintoto.messagelogger.struct.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@RequiredArgsConstructor
public class LogCommand {
    private final MessageLogger plugin;

    @Command({"log", "messagelogger log", "msglog log"})
    @CommandPermission("messagelogger.command.log")
    @Description("Get the messages sent by the given player")
    public void log(BukkitCommandActor sender, OfflinePlayer target, @Default("1") @Range(min = 1) int page) {

        String fetch  = plugin.getMessages().getString("admin.fetching-data").replace("%player%", target.getName());
        sender.getSender().sendMessage(Manager.formatMessage(fetch));

        if(target.isOnline()) {
            Player p = target.getPlayer();
            String name = p.getName();

            plugin.getDatabaseManager().getMessages(p).thenAccept(list -> {
                perform(sender, list, name, page);
            });
        } else {
            String name = target.getName();
            plugin.getDatabaseManager().getMessages(name).thenAccept(list -> {
                perform(sender, list, name, page);
            });
        }
    }

    @Command({"messagelogger reload", "msglog reload"})
    @CommandPermission("messagelogger.command.reload")
    @Description("Reload the plugin")
    public void reload(BukkitCommandActor sender) {
        plugin.reload();
        String msg = plugin.getMessages().getString("admin.reload");
        sender.getSender().sendMessage(Manager.formatMessage(msg));
    }

    @Command({"messagelogger info", "msglog info"})
    @CommandPermission("messagelogger.command")
    @Description("Main MessageLogger Command")
    public void def(BukkitCommandActor sender) {
        String msg = plugin.getMessages().getString("admin.default");
        msg = msg.replace("%version%", plugin.getDescription().getVersion());
        sender.getSender().sendMessage(Manager.formatMessage(msg));
    }


    private int getPageNumbers(int len) {
        int epp = plugin.getConfig().getInt("log.entry-per-page");
        if(len % epp == 0) {
            return len / epp;
        } else return (len / epp) + 1;
    }

    private void perform(BukkitCommandActor sender, List<Message> list, String name, int page) {
        if(list.isEmpty()) {
            String msg = plugin.getMessages().getString("admin.list-empty").replace("%player%", name);
            sender.getSender().sendMessage(Manager.formatMessage(msg));
            return;
        }

        int pages = getPageNumbers(list.size());

        if(page > pages) {
            String msg = plugin.getMessages().getString("error.page-not-existing")
                    .replace("%max-page%", Integer.toString(pages));

            sender.getSender().sendMessage(Manager.formatMessage(msg));
            return;
        }

        int epp = plugin.getConfig().getInt("log.entry-per-page");

        String title = plugin.getMessages().getString("log.title")
                .replace("%player%", name)
                .replace("%page%", Integer.toString(page))
                .replace("%max-page%", Integer.toString(pages));

        sender.getSender().sendMessage(Manager.formatMessage(title));

        String format = plugin.getMessages().getString("log.format").replace("%player%", name);

        for (int i = (page - 1) * epp; i < page * epp; i++) {
            String message = list.get(i).getMessage();
            String date = list.get(i).getDate();
            String currentFormat = format.replace("%message%", message).replace("%date%", Manager.timeAgo(date));

            sender.getSender().sendMessage(Manager.formatMessage(currentFormat));
        }
    }
}
