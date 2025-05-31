package com.sololeveling.task;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a task in the Solo Leveling plugin.
 * Tasks can be from OSRS leagues, quests, or custom challenges.
 */
@Data
@Builder
public class Task {
    /**
     * Unique identifier for the task
     */
    private final String id;

    /**
     * The name of the task
     */
    private final String name;

    /**
     * A detailed description of the task
     */
    private final String description;

    /**
     * The difficulty level of the task (EASY, MEDIUM, HARD, ELITE, MASTER)
     */
    private final TaskDifficulty difficulty;

    /**
     * The category of the task (COMBAT, SKILLING, EXPLORATION, etc.)
     */
    private final TaskCategory category;

    /**
     * The source of the task (LEAGUE, QUEST, CUSTOM)
     */
    private final TaskSource source;

    /**
     * Whether the task is completed
     */
    @Setter
    private boolean completed;

    /**
     * Whether the task is visible to the player (some tasks can be hidden until unlocked)
     */
    @Setter
    private boolean visible;

    /**
     * Experience reward for completing the task
     */
    private int experienceReward;

    /**
     * Points reward for completing the task
     */
    private int pointsReward;

    /**
     * ID of the related quest (if this is a quest task)
     */
    private String relatedQuestId;

    /**
     * Any additional data related to the task
     */
    private String additionalData;

    /**
     * Get the related quest ID if this is a quest task
     */
    public String getRelatedQuestId() {
        return relatedQuestId;
    }
}
