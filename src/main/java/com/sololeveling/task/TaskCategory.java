package com.sololeveling.task;

/**
 * Represents the category of a task
 */
public enum TaskCategory {
    COMBAT("Combat"),
    SKILLING("Skilling"),
    EXPLORATION("Exploration"),
    MINIGAME("Minigame"),
    QUEST("Quest"),
    ACHIEVEMENT_DIARY("Achievement Diary"),
    COLLECTION_LOG("Collection Log"),
    MISCELLANEOUS("Miscellaneous");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
