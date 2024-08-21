package com.asintoto.messagelogger.managers;

import com.asintoto.colorlib.ColorLib;
import com.asintoto.messagelogger.MessageLogger;
import com.asintoto.messagelogger.enums.DatabaseType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Manager {
    public static String formatMessage(String msg) {
        return ColorLib.setColors(msg);
    }

    public static String timeAgo(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dataTime = LocalDateTime.parse(dateTimeString, formatter);
        LocalDateTime now = LocalDateTime.now();


        long secondsInMinute = 60;
        long secondsInHour = 60 * secondsInMinute;
        long secondsInDay = 24 * secondsInHour;
        long secondsInMonth = 30 * secondsInDay;  // Approssimazione, mese di 30 giorni
        long secondsInYear = 12 * secondsInMonth; // Approssimazione, anno di 12 mesi

        long totalSeconds = ChronoUnit.SECONDS.between(dataTime, now);

        long years = totalSeconds / secondsInYear;
        totalSeconds %= secondsInYear;

        long months = totalSeconds / secondsInMonth;
        totalSeconds %= secondsInMonth;

        long days = totalSeconds / secondsInDay;
        totalSeconds %= secondsInDay;

        long hours = totalSeconds / secondsInHour;
        totalSeconds %= secondsInHour;

        long minutes = totalSeconds / secondsInMinute;
        totalSeconds %= secondsInMinute;

        long seconds = totalSeconds;

        StringBuilder timeAgo = new StringBuilder();

        if (years > 0) {
            timeAgo.append(years).append("y ");
        }
        if (months > 0) {
            timeAgo.append(months).append("m ");
        }
        if (days > 0) {
            timeAgo.append(days).append("d ");
        }
        if (hours > 0) {
            timeAgo.append(hours).append("h ");
        }
        if (minutes > 0) {
            timeAgo.append(minutes).append("m ");
        }
        if (seconds > 0) {
            timeAgo.append(seconds).append("s ");
        }

        if (timeAgo.isEmpty()) {
            String justnow = MessageLogger.getInstance().getMessages().getString("log.just-now");
            timeAgo.append(justnow);
        } else {
            String ago = MessageLogger.getInstance().getMessages().getString("log.ago");
            timeAgo.append(ago);
        }


        return timeAgo.toString().trim();
    }

    public static  <T> List<T> limitList(List<T> lista, int limit) {
        if (lista.size() <= limit) {
            return lista;
        } else {
            return lista.subList(0, limit);
        }
    }
}
