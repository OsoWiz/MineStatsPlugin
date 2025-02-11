package dev.osowiz.speedrunstats.enums;

public enum Category {

    STANDARD("standard"),
    UNKNOWN("unknown");


    private final String name;
    Category(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
