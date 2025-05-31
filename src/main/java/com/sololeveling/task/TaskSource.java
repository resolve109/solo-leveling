package com.sololeveling.task;

/**
 * Represents the source of a task
 */
public enum TaskSource {
    LEAGUE_RAGING_ECHOES("Raging Echoes League"),
    LEAGUE_TRAILBLAZER("Trailblazer Reloaded League"),
    QUEST("OSRS Quest"),
    CUSTOM("Custom");

    private final String displayName;

    TaskSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
