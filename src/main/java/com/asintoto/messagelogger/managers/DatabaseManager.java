package com.asintoto.messagelogger.managers;

import com.asintoto.basic.utils.Common;
import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.enums.DatabaseType;
import com.asintoto.messagelogger.struct.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private final MessageLogger plugin;
    Connection connection;

    public DatabaseManager(MessageLogger plugin) {
        this.plugin = plugin;

        DatabaseType type = getDatabaseType();
        String path = plugin.getConfig().getString("database.database-name");

        if (type == DatabaseType.SQLITE) {

            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + path + ".db");
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                return;
            }

        } else {
            String databaseName = plugin.getConfig().getString("database.database-name");
            String ipAddress = plugin.getConfig().getString("database.ip-address");
            String port = Integer.toString(plugin.getConfig().getInt("database.port"));
            String user = plugin.getConfig().getString("database.user");
            String password = plugin.getConfig().getString("database.password");

            if (type == DatabaseType.MYSQL) {

                String url = "jdbc:mysql://" + ipAddress + ":" + port + "/" + databaseName;


                try {
                    connection = DriverManager.getConnection(url, user, password);
                } catch (SQLException e) {
                    String msg = plugin.getMessages().getString("error.database-error");
                    Common.message(Manager.formatMessage(msg));
                    e.printStackTrace();
                    return;
                }

            } else {
                connection = null;
                String msg = plugin.getMessages().getString("error.invalid-database-type");
                Common.message(Manager.formatMessage(msg));
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }

        createTable();
    }


    public DatabaseType getDatabaseType() {
        String type = plugin.getConfig().getString("database.type");
        return switch (type.toLowerCase()) {
            case "sqlite" -> DatabaseType.SQLITE;
            case "mysql" -> DatabaseType.MYSQL;
            default -> DatabaseType.INVALID;
        };
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            String msg = plugin.getMessages().getString("error.database-error");
            Common.message(Manager.formatMessage(msg));
            e.printStackTrace();
        }
    }

    private void createTable() {
        CompletableFuture.runAsync(() -> {
            try {
                Statement statement = connection.createStatement();
                statement.execute("""
                    CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL,
                    username TEXT NOT NULL,
                    message TEXT,
                    date TEXT)
                    """);
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addPlayer(Player player) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, player.getName());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
            }
        });
    }



    public CompletableFuture<Void> addMessage(Player p, String message, String date) {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages (uuid, username, message, date) VALUES (?, ?, ?, ?)")) {

                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setString(2, p.getName());
                preparedStatement.setString(3, message);
                preparedStatement.setString(4, date);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<List<Message>> getMessages(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            List<Message> messages = new ArrayList<>();
            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT message, date FROM messages WHERE username = ? ORDER BY date DESC");
                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    messages.add(new Message(rs.getString("message"), rs.getString("date")));
                }
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
            }
            return messages;
        });
    }

    public CompletableFuture<List<Message>> getMessages(Player p) {
        return CompletableFuture.supplyAsync(() -> {
            List<Message> messages = new ArrayList<>();
            try {
                PreparedStatement stmt = connection.prepareStatement("SELECT message, date FROM messages WHERE uuid = ? ORDER BY date DESC");
                stmt.setString(1, p.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    messages.add(new Message(rs.getString("message"), rs.getString("date")));
                }
            } catch (SQLException e) {
                String msg = plugin.getMessages().getString("error.database-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
            }
            return messages;
        });
    }
}
