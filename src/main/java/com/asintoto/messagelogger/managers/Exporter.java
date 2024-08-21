package com.asintoto.messagelogger.managers;

import com.asintoto.basic.utils.Common;
import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.struct.Message;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class Exporter {
    private final MessageLogger plugin;

    public CompletableFuture<String> export(List<Message> list, String target) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File outFolder = new File(plugin.getDataFolder(), "out");
                if (!outFolder.exists()) {
                    outFolder.mkdirs();
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                String date = dtf.format(now);

                String fileName = date + "_" + target + ".txt";
                File logFile = new File(outFolder, fileName);

                try (FileWriter writer = new FileWriter(logFile)) {
                    for (Message message : list) {
                        writer.write("[" + message.getDate() + "]" + " " + message.getPlayerName() + ": " + message.getMessage() + "\n");
                    }
                }

                return fileName;

            } catch (IOException e) {
                String msg = plugin.getMessages().getString("error.export-error");
                Common.message(Manager.formatMessage(msg));
                e.printStackTrace();
                return "ERROR";
            }
        });
    }

    public boolean checkError(String filename) {
        return filename.equalsIgnoreCase("ERROR");
    }
}
