package com.sololeveling.task;

import lombok.Builder;
import lombok.Data;

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
    private boolean completed;

    /**
     * Whether the task is visible to the player (some tasks can be hidden until unlocked)
     */
    private boolean visible;

    /**
     * The experience points rewarded for completing the task
     */
    private final int experienceReward;

    /**
     * Points awarded for completing the task
     */
    private final int pointsReward;
}
