package com.asintoto.messagelogger.commands;


import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.managers.Manager;
import com.asintoto.messagelogger.struct.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

@Command({"messagelogger", "msglog"})
@RequiredArgsConstructor
public class LogCommand {
    private final MessageLogger plugin;


    @Command("log")
    @CommandPermission("messagelogger.command.log")
    @Description("Get the messages sent by the given player")
    public void log(BukkitCommandActor sender, OfflinePlayer target, @Default("1") @Range(min = 1) int page) {
        performLog(sender, target, page);
    }

    @Subcommand("log")
    @CommandPermission("messagelogger.command.log")
    @Description("Get the messages sent by the given player")
    public void logSub(BukkitCommandActor sender, OfflinePlayer target, @Default("1") @Range(min = 1) int page) {
        performLog(sender, target, page);
    }

    @Command("logall")
    @CommandPermission("messagelogger.command.log.all")
    @Description("Get the messages sent by all players")
    public void logSubAll(BukkitCommandActor sender, @Default("1") @Range(min = 1) int page) {
        performLogAll(sender, page);
    }

    @Subcommand("logall")
    @CommandPermission("messagelogger.command.log.all")
    @Description("Get the messages sent by all players")
    public void logSubAllSub(BukkitCommandActor sender, @Default("1") @Range(min = 1) int page) {
        performLogAll(sender, page);
    }

    @Subcommand("reload")
    @CommandPermission("messagelogger.command.reload")
    @Description("Reload the plugin")
    public void reload(BukkitCommandActor sender) {
        plugin.reload();
        String msg = plugin.getMessages().getString("admin.reload");
        sender.getSender().sendMessage(Manager.formatMessage(msg));
    }

    @DefaultFor({"~", "~ info"})
    @CommandPermission("messagelogger.command.info")
    @Description("Main MessageLogger Command")
    public void def(BukkitCommandActor sender) {
        List<String> msgs = plugin.getMessages().getStringList("admin.info");
        String version = plugin.getDescription().getVersion();
        String databaseType = plugin.getDatabaseManager().getDatabaseType().toString();
        for(String s : msgs) {
            String msg = s.replace("%version%", version).replace("%database%", databaseType);
            sender.getSender().sendMessage(Manager.formatMessage(msg));
        }
    }

    @Subcommand("export all")
    @CommandPermission("messagelogger.command.export.all")
    @Description("Export All Players Messages")
    public void exportAll(BukkitCommandActor sender, @Default("100")@Range(min = 1) int limit) {
        String msgs = plugin.getMessages().getString("admin.start-export");
        sender.getSender().sendMessage(Manager.formatMessage(msgs));

        plugin.getDatabaseManager().getAllMessages(limit).thenAccept(list -> {
            String target = "all";
            plugin.getExporter().export(list, target).thenAccept(filename -> {
                afterExport(target, filename, sender, list);
            });
        });
    }

    @Subcommand("export single")
    @CommandPermission("messagelogger.command.export.single")
    @Description("Export a Single Player's Messages")
    public void exportSingle(BukkitCommandActor sender, OfflinePlayer player, @Default("100")@Range(min = 1) int limit) {
        String msgs = plugin.getMessages().getString("admin.start-export");
        sender.getSender().sendMessage(Manager.formatMessage(msgs));

        if(player.isOnline()) {
            Player p = player.getPlayer();
            plugin.getDatabaseManager().getMessages(p).thenAccept(list -> {
                List<Message> newList = Manager.limitList(list, limit);
                plugin.getExporter().export(newList, p.getName()).thenAccept(filename -> {
                    afterExport(p.getName(), filename, sender, newList);
                });
            });
        } else {
            plugin.getDatabaseManager().getMessages(player.getName()).thenAccept(list -> {
                List<Message> newList = Manager.limitList(list, limit);
                plugin.getExporter().export(newList, player.getName()).thenAccept(filename -> {
                    afterExport(player.getName(), filename, sender, newList);
                });
            });
        }
    }

    private void afterExport(String target, String filename, BukkitCommandActor sender, List<Message> list) {
        if(plugin.getExporter().checkError(filename)) {
            String msg = plugin.getMessages().getString("error.export-error");
            sender.getSender().sendMessage(Manager.formatMessage(msg));
            return;
        }

        int size = list.size();
        String msg = plugin.getMessages().getString("admin.export-all")
                .replace("%count%", Integer.toString(size))
                .replace("%file%", filename)
                .replace("%player%", target);
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

    private void performLog(BukkitCommandActor sender, OfflinePlayer target, int page) {
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

    private void performLogAll(BukkitCommandActor sender, int page) {
        String fetch  = plugin.getMessages().getString("admin.fetching-data-all");
        sender.getSender().sendMessage(Manager.formatMessage(fetch));

        plugin.getDatabaseManager().getAllMessages().thenAccept(list -> {
            if(list.isEmpty()) {
                String msg = plugin.getMessages().getString("admin.list-empty-all");
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

            String title = plugin.getMessages().getString("log.title-all")
                    .replace("%page%", Integer.toString(page))
                    .replace("%max-page%", Integer.toString(pages));

            sender.getSender().sendMessage(Manager.formatMessage(title));

            String format = plugin.getMessages().getString("log.format");

            for (int i = (page - 1) * epp; i < page * epp; i++) {
                String message = list.get(i).getMessage();
                String date = list.get(i).getDate();
                String name = list.get(i).getPlayerName();
                String currentFormat = format
                        .replace("%message%", message)
                        .replace("%date%", Manager.timeAgo(date))
                        .replace("%player%", name);;

                sender.getSender().sendMessage(Manager.formatMessage(currentFormat));
            }
        });
    }
}
