package com.sololeveling.task;

/**
 * Represents the source of a task
 */
public enum TaskSource {
    LEAGUE_RAGING_ECHOES("Raging Echoes League"),
    LEAGUE_TRAILBLAZER("Trailblazer Reloaded League"),
    QUEST("OSRS Quest"),
    CUSTOM("Custom"),
    COMBAT_ACHIEVEMENT("Combat Achievement"),
    ACHIEVEMENT_DIARY("Achievement Diary"),
    COLLECTION_LOG("Collection Log"),
    SKILL_PROGRESSION("Skill Progression"),
    EQUIPMENT_UPGRADE("Equipment Upgrade"),
    BOSS_CHALLENGE("Boss Challenge"),
    MINIGAME_MASTERY("Minigame Mastery");

    private final String displayName;

    TaskSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
