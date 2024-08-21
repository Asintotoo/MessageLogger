package com.asintoto.messagelogger.enums;

public enum DatabaseType {
    SQLITE("SQLite"),
    MYSQL("MySQL"),
    INVALID("Invalid");

    private String name;

    DatabaseType(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
