package com.sololeveling.tracking;

import lombok.Data;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks the player's progress in various aspects of the game.
 */
@Data
public class PlayerTracker {
    // Quest tracking
    private final Map<String, Boolean> completedQuests = new HashMap<>();
    private int questPoints = 0;

    // Skill tracking
    private final Map<Skill, Integer> skillLevels = new HashMap<>();
    private final Map<Skill, Long> skillExperience = new HashMap<>();

    // Game time tracking
    private long sessionStartTime;
    private long totalPlayTimeMillis = 0;
    private int gameTicks = 0;

    // Activity tracking
    private final Map<String, Integer> activityCounts = new HashMap<>();

    /**
     * Initialize the player tracker with current game state
     */
    public void initialize(Client client) {
        if (client.getGameState().getState() < 30) {
            // Not logged in
            return;
        }

        // Start tracking session time
        sessionStartTime = System.currentTimeMillis();

        // Track skill levels and experience
        for (Skill skill : Skill.values()) {
            if (skill != Skill.OVERALL) {
                skillLevels.put(skill, client.getRealSkillLevel(skill));
                skillExperience.put(skill, (long) client.getSkillExperience(skill));
            }
        }

        // Track quest completion status
        updateQuestStatus(client);
    }

    /**
     * Update quest completion status
     */
    public void updateQuestStatus(Client client) {
        questPoints = client.getVarpValue(Varbits.QUEST_POINTS);

        // Track all quests (simplified version)
        for (Quest quest : Quest.values()) {
            completedQuests.put(quest.getName(), quest.getState(client) == 2);
        }
    }

    /**
     * Update skill levels and experience
     */
    public void updateSkills(Client client) {
        for (Skill skill : Skill.values()) {
            if (skill != Skill.OVERALL) {
                skillLevels.put(skill, client.getRealSkillLevel(skill));
                skillExperience.put(skill, (long) client.getSkillExperience(skill));
            }
        }
    }

    /**
     * Record a game tick
     */
    public void recordGameTick() {
        gameTicks++;
    }

    /**
     * Update session time
     */
    public void updateSessionTime() {
        long currentSessionTime = System.currentTimeMillis() - sessionStartTime;
        totalPlayTimeMillis += currentSessionTime;
        // Reset for next session
        sessionStartTime = System.currentTimeMillis();
    }

    /**
     * Record an activity occurrence
     */
    public void recordActivity(String activity) {
        int count = activityCounts.getOrDefault(activity, 0);
        activityCounts.put(activity, count + 1);
    }

    /**
     * Record an activity with a specific count
     */
    public void recordActivity(String activity, int count) {
        int currentCount = activityCounts.getOrDefault(activity, 0);
        activityCounts.put(activity, currentCount + count);
    }

    /**
     * Get formatted play time
     */
    public String getFormattedPlayTime() {
        long totalSeconds = totalPlayTimeMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Check if a quest is completed
     */
    public boolean isQuestCompleted(String questName) {
        return completedQuests.getOrDefault(questName, false);
    }

    /**
     * Get number of completed quests
     */
    public int getCompletedQuestCount() {
        int count = 0;
        for (Boolean completed : completedQuests.values()) {
            if (completed) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if all quests are completed (quest cape)
     */
    public boolean hasQuestCape() {
        // Check if all quests are completed
        for (Boolean completed : completedQuests.values()) {
            if (!completed) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the count for a specific activity
     */
    public int getActivityCount(String activity) {
        return activityCounts.getOrDefault(activity, 0);
    }
}
