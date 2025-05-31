package com.sololeveling.task;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Manages tasks for the Solo Leveling plugin
 */
@Slf4j
public class TaskManager {
    private final List<Task> allTasks = new ArrayList<>();
    private final List<Task> completedTasks = new ArrayList<>();
    private final List<Task> visibleTasks = new ArrayList<>();
    private final Random random = new Random();
    private boolean isInitialized = false;

    /**
     * Initialize the task manager with default tasks
     */
    public void initialize() {
        // Run initialization in background to prevent lag on game startup
        CompletableFuture.runAsync(() -> {
            log.info("Starting task initialization in background...");
            loadDefaultTasks();
            updateTaskLists();
            isInitialized = true;
            log.info("Task Manager initialized with {} tasks", allTasks.size());
        });
    }

    /**
     * Check if TaskManager has completed initialization
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Load default tasks from leagues, quests, achievement diaries, combat achievements, and collection log
     */
    private void loadDefaultTasks() {
        // Add sample league tasks
        loadLeagueTasks();

        // Add sample quest tasks
        loadQuestTasks();

        // Add Achievement Diary tasks
        loadAchievementDiaryTasks();

        // Add Combat Achievement tasks
        loadCombatAchievementTasks();

        // Add Collection Log tasks
        loadCollectionLogTasks();
    }

    /**
     * Load tasks from OSRS Leagues
     */
    private void loadLeagueTasks() {
        // Raging Echoes League - Combat tasks
        allTasks.add(Task.builder()
                .id("rel_combat_1")
                .name("Defeat the King Black Dragon")
                .description("Defeat the King Black Dragon in the Wilderness.")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.LEAGUE_RAGING_ECHOES)
                .experienceReward(5000)
                .pointsReward(100)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("rel_combat_2")
                .name("Defeat Zulrah")
                .description("Defeat the toxic serpent, Zulrah.")
                .difficulty(TaskDifficulty.HARD)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.LEAGUE_RAGING_ECHOES)
                .experienceReward(7500)
                .pointsReward(150)
                .completed(false)
                .visible(true)
                .build());

        // Trailblazer League - Skilling tasks
        allTasks.add(Task.builder()
                .id("tbl_skilling_1")
                .name("Reach 99 in any skill")
                .description("Reach level 99 in any skill.")
                .difficulty(TaskDifficulty.ELITE)
                .category(TaskCategory.SKILLING)
                .source(TaskSource.LEAGUE_TRAILBLAZER)
                .experienceReward(20000)
                .pointsReward(500)
                .completed(false)
                .visible(true)
                .build());
    }

    /**
     * Load quest-related tasks
     */
    private void loadQuestTasks() {
        allTasks.add(Task.builder()
                .id("quest_1")
                .name("Complete Dragon Slayer")
                .description("Complete the Dragon Slayer quest.")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.QUEST)
                .source(TaskSource.QUEST)
                .experienceReward(5000)
                .pointsReward(100)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("quest_2")
                .name("Complete Monkey Madness")
                .description("Complete the Monkey Madness quest.")
                .difficulty(TaskDifficulty.HARD)
                .category(TaskCategory.QUEST)
                .source(TaskSource.QUEST)
                .experienceReward(7500)
                .pointsReward(150)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("quest_3")
                .name("Complete Recipe for Disaster")
                .description("Complete the Recipe for Disaster quest.")
                .difficulty(TaskDifficulty.MASTER)
                .category(TaskCategory.QUEST)
                .source(TaskSource.QUEST)
                .experienceReward(15000)
                .pointsReward(300)
                .completed(false)
                .visible(true)
                .build());
    }

    /**
     * Load tasks from Achievement Diaries
     */
    private void loadAchievementDiaryTasks() {
        // Karamja Diary
        allTasks.add(Task.builder()
                .id("diary_karamja_easy_1")
                .name("Mine some gold from the rocks on the north west peninsula of Karamja")
                .description("Mine some gold from the rocks on the north west peninsula of Karamja")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.ACHIEVEMENT_DIARY)
                .source(TaskSource.CUSTOM)
                .experienceReward(1000)
                .pointsReward(25)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("diary_karamja_easy_2")
                .name("Use the Fishing spots north of the banana plantation")
                .description("Use the Fishing spots north of the banana plantation")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.ACHIEVEMENT_DIARY)
                .source(TaskSource.CUSTOM)
                .experienceReward(1000)
                .pointsReward(25)
                .completed(false)
                .visible(true)
                .build());

        // Lumbridge & Draynor Diary
        allTasks.add(Task.builder()
                .id("diary_lumbridge_medium_1")
                .name("Complete a lap of the Draynor Village Agility Course")
                .description("Complete a lap of the Draynor Village Agility Course")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.ACHIEVEMENT_DIARY)
                .source(TaskSource.CUSTOM)
                .experienceReward(2500)
                .pointsReward(50)
                .completed(false)
                .visible(true)
                .build());
    }

    /**
     * Load tasks from Combat Achievements
     */
    private void loadCombatAchievementTasks() {
        // Easy Combat Achievements
        allTasks.add(Task.builder()
                .id("combat_easy_1")
                .name("Defeat the Giant Mole")
                .description("Defeat the Giant Mole located under Falador Park")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.CUSTOM)
                .experienceReward(2000)
                .pointsReward(50)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("combat_easy_2")
                .name("Defeat the King Black Dragon")
                .description("Defeat the King Black Dragon in the Wilderness")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.CUSTOM)
                .experienceReward(2000)
                .pointsReward(50)
                .completed(false)
                .visible(true)
                .build());

        // Medium Combat Achievements
        allTasks.add(Task.builder()
                .id("combat_medium_1")
                .name("Defeat Zulrah")
                .description("Defeat the toxic serpent, Zulrah")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.CUSTOM)
                .experienceReward(5000)
                .pointsReward(100)
                .completed(false)
                .visible(true)
                .build());

        // Hard Combat Achievements
        allTasks.add(Task.builder()
                .id("combat_hard_1")
                .name("Defeat the Corporeal Beast")
                .description("Defeat the Corporeal Beast")
                .difficulty(TaskDifficulty.HARD)
                .category(TaskCategory.COMBAT)
                .source(TaskSource.CUSTOM)
                .experienceReward(10000)
                .pointsReward(200)
                .completed(false)
                .visible(true)
                .build());
    }

    /**
     * Load tasks from Collection Log
     */
    private void loadCollectionLogTasks() {
        // Bosses collection
        allTasks.add(Task.builder()
                .id("collection_bosses_1")
                .name("Obtain a unique drop from any boss")
                .description("Obtain a unique item that appears in the boss section of the Collection Log")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.COLLECTION_LOG)
                .source(TaskSource.CUSTOM)
                .experienceReward(5000)
                .pointsReward(100)
                .completed(false)
                .visible(true)
                .build());

        // Treasure Trails
        allTasks.add(Task.builder()
                .id("collection_clues_1")
                .name("Complete an Easy Clue Scroll")
                .description("Complete an Easy Clue Scroll and collect a unique item")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.COLLECTION_LOG)
                .source(TaskSource.CUSTOM)
                .experienceReward(1000)
                .pointsReward(50)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("collection_clues_2")
                .name("Complete a Medium Clue Scroll")
                .description("Complete a Medium Clue Scroll and collect a unique item")
                .difficulty(TaskDifficulty.MEDIUM)
                .category(TaskCategory.COLLECTION_LOG)
                .source(TaskSource.CUSTOM)
                .experienceReward(2500)
                .pointsReward(75)
                .completed(false)
                .visible(true)
                .build());

        allTasks.add(Task.builder()
                .id("collection_clues_3")
                .name("Complete a Hard Clue Scroll")
                .description("Complete a Hard Clue Scroll and collect a unique item")
                .difficulty(TaskDifficulty.HARD)
                .category(TaskCategory.COLLECTION_LOG)
                .source(TaskSource.CUSTOM)
                .experienceReward(5000)
                .pointsReward(125)
                .completed(false)
                .visible(true)
                .build());
    }

    /**
     * Update the lists of completed and visible tasks
     */
    private void updateTaskLists() {
        completedTasks.clear();
        visibleTasks.clear();

        for (Task task : allTasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
            if (task.isVisible()) {
                visibleTasks.add(task);
            }
        }
    }

    /**
     * Get all tasks
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(allTasks);
    }

    /**
     * Get all visible tasks
     */
    public List<Task> getVisibleTasks() {
        return Collections.unmodifiableList(visibleTasks);
    }

    /**
     * Get all completed tasks
     */
    public List<Task> getCompletedTasks() {
        return Collections.unmodifiableList(completedTasks);
    }

    /**
     * Get all incomplete tasks
     */
    public List<Task> getIncompleteTasks() {
        return allTasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

    /**
     * Get incomplete tasks by source
     */
    public List<Task> getIncompleteTasksBySource(TaskSource source) {
        return allTasks.stream()
                .filter(task -> !task.isCompleted())
                .filter(task -> task.getSource() == source)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by category
     */
    public List<Task> getTasksByCategory(TaskCategory category) {
        return allTasks.stream()
                .filter(task -> task.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by difficulty
     */
    public List<Task> getTasksByDifficulty(TaskDifficulty difficulty) {
        return allTasks.stream()
                .filter(task -> task.getDifficulty() == difficulty)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by source
     */
    public List<Task> getTasksBySource(TaskSource source) {
        return allTasks.stream()
                .filter(task -> task.getSource() == source)
                .collect(Collectors.toList());
    }

    /**
     * Get a task by ID
     */
    public Task getTaskById(String taskId) {
        Optional<Task> task = allTasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst();
        return task.orElse(null);
    }

    /**
     * Complete a task by ID
     */
    public boolean completeTask(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null && !task.isCompleted()) {
            task.setCompleted(true);
            updateTaskLists();
            log.debug("Task completed: {}", task.getName());
            return true;
        }
        return false;
    }

    /**
     * Reset a task to incomplete
     */
    public boolean resetTask(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null && task.isCompleted()) {
            task.setCompleted(false);
            updateTaskLists();
            log.debug("Task reset: {}", task.getName());
            return true;
        }
        return false;
    }

    /**
     * Set a task's visibility
     */
    public boolean setTaskVisibility(String taskId, boolean visible) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setVisible(visible);
            updateTaskLists();
            return true;
        }
        return false;
    }

    /**
     * Add a new task to the task manager
     */
    public Task addTask(Task task) {
        allTasks.add(task);
        updateTaskLists();
        log.debug("Added new task: {} ({})", task.getName(), task.getId());
        return task;
    }

    /**
     * Add multiple tasks at once
     */
    public void addTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        allTasks.addAll(tasks);
        updateTaskLists();
        log.debug("Added {} new tasks", tasks.size());
    }

    /**
     * Remove a task from the task manager
     */
    public boolean removeTask(String taskId) {
        Task taskToRemove = getTaskById(taskId);
        if (taskToRemove != null) {
            allTasks.remove(taskToRemove);
            updateTaskLists();
            log.debug("Removed task: {} ({})", taskToRemove.getName(), taskToRemove.getId());
            return true;
        }
        return false;
    }

    /**
     * Create a random task with specified difficulty and category
     */
    public Task createRandomTask(TaskDifficulty difficulty, TaskCategory category) {
        String id = "random_" + System.currentTimeMillis();
        String[] names = {
                "Defeat " + getRandomMonster(),
                "Collect " + (random.nextInt(50) + 10) + " " + getRandomItem(),
                "Train " + getRandomSkill() + " for 30 minutes",
                "Complete a " + difficulty.toString().toLowerCase() + " clue scroll",
                "Reach level " + (random.nextInt(10) + 5) + " in " + getRandomSkill()
        };

        return Task.builder()
                .id(id)
                .name(names[random.nextInt(names.length)])
                .description("A randomly generated task to test your skills, Hunter!")
                .difficulty(difficulty)
                .category(category)
                .source(TaskSource.CUSTOM)
                .experienceReward(calculateReward(difficulty))
                .pointsReward(calculatePoints(difficulty))
                .completed(false)
                .visible(true)
                .build();
    }

    /**
     * Create a quest-related task
     */
    public Task createQuestTask() {
        String id = "quest_random_" + System.currentTimeMillis();
        String[] quests = {
                "Cook's Assistant",
                "Demon Slayer",
                "Dragon Slayer",
                "Ernest the Chicken",
                "Goblin Diplomacy",
                "Imp Catcher",
                "The Knight's Sword",
                "Pirate's Treasure",
                "Prince Ali Rescue",
                "The Restless Ghost",
                "Romeo & Juliet",
                "Rune Mysteries",
                "Sheep Shearer",
                "Shield of Arrav",
                "Vampire Slayer",
                "Witch's Potion"
        };

        String quest = quests[random.nextInt(quests.length)];

        return Task.builder()
                .id(id)
                .name("Complete " + quest)
                .description("Complete the " + quest + " quest to grow stronger, Hunter!")
                .difficulty(TaskDifficulty.EASY)
                .category(TaskCategory.QUEST)
                .source(TaskSource.QUEST)
                .experienceReward(1000)
                .pointsReward(50)
                .completed(false)
                .visible(true)
                .build();
    }

    /**
     * Generate personalized tasks based on player's current stats and progress
     */
    public Task createPersonalizedTask(String playerName, Map<Skill, Integer> currentStats,
                                       Map<TaskDifficulty, Integer> completedTaskDifficulties) {
        // Determine appropriate difficulty level based on progression
        TaskDifficulty appropriateDifficulty = getAppropriateDifficulty(completedTaskDifficulties);

        // Get the player's highest skills to focus tasks on
        List<Skill> highestSkills = getHighestSkills(currentStats, 3);

        // Get the player's lowest skills that could use improvement
        List<Skill> lowestSkills = getLowestSkills(currentStats, 3);

        // Determine if we should focus on strengths or weaknesses (70% chance to focus on lowest skills)
        boolean focusOnWeaknesses = random.nextDouble() < 0.7;
        List<Skill> targetSkills = focusOnWeaknesses ? lowestSkills : highestSkills;

        if (targetSkills.isEmpty()) {
            // Fallback to a random skill if no target skills found
            Skill[] allSkills = Skill.values();
            List<Skill> skillList = new ArrayList<>();
            for (Skill skill : allSkills) {
                if (skill != Skill.OVERALL) {
                    skillList.add(skill);
                }
            }
            targetSkills = skillList;
        }

        // Select a random skill from our target list
        Skill targetSkill = targetSkills.get(random.nextInt(targetSkills.size()));

        // Create a task appropriate for the player's level in that skill
        int currentLevel = currentStats.getOrDefault(targetSkill, 1);
        return createSkillBasedTask(targetSkill, currentLevel, appropriateDifficulty);
    }

    /**
     * Determine the appropriate difficulty level based on task completion history
     */
    private TaskDifficulty getAppropriateDifficulty(Map<TaskDifficulty, Integer> completedTaskDifficulties) {
        // Default to EASY if no history
        if (completedTaskDifficulties == null || completedTaskDifficulties.isEmpty()) {
            return TaskDifficulty.EASY;
        }

        // Check if player has completed enough tasks at each level to move up
        int easyCompleted = completedTaskDifficulties.getOrDefault(TaskDifficulty.EASY, 0);
        int mediumCompleted = completedTaskDifficulties.getOrDefault(TaskDifficulty.MEDIUM, 0);
        int hardCompleted = completedTaskDifficulties.getOrDefault(TaskDifficulty.HARD, 0);
        int eliteCompleted = completedTaskDifficulties.getOrDefault(TaskDifficulty.ELITE, 0);

        // Progression thresholds
        if (easyCompleted < 5) {
            return TaskDifficulty.EASY;
        } else if (mediumCompleted < 5) {
            return TaskDifficulty.MEDIUM;
        } else if (hardCompleted < 5) {
            return TaskDifficulty.HARD;
        } else if (eliteCompleted < 3) {
            return TaskDifficulty.ELITE;
        } else {
            return TaskDifficulty.MASTER;
        }
    }

    /**
     * Get the player's highest level skills
     */
    private List<Skill> getHighestSkills(Map<Skill, Integer> currentStats, int count) {
        return currentStats.entrySet().stream()
                .filter(entry -> entry.getKey() != Skill.OVERALL) // Exclude overall
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // Sort descending
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Get the player's lowest level skills (only trainable skills above level 1)
     */
    private List<Skill> getLowestSkills(Map<Skill, Integer> currentStats, int count) {
        return currentStats.entrySet().stream()
                .filter(entry -> entry.getKey() != Skill.OVERALL) // Exclude overall
                .filter(entry -> entry.getValue() > 1) // Only skills the player has started training
                .sorted(Map.Entry.comparingByValue()) // Sort ascending
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Create a task based on skill level and appropriate difficulty
     */
    private Task createSkillBasedTask(Skill skill, int level, TaskDifficulty difficulty) {
        String id = "personalized_" + skill.name().toLowerCase() + "_" + System.currentTimeMillis();

        // Calculate an appropriate target level based on current level and difficulty
        int targetLevel = Math.min(99, level + getTargetLevelIncrease(level, difficulty));

        // Generate task name and description based on skill
        String taskName;
        String taskDescription;
        TaskCategory category = TaskCategory.SKILLING;

        // Different task types based on skill
        switch (skill) {
            // Combat skills
            case ATTACK:
            case STRENGTH:
            case DEFENCE:
            case RANGED:
            case MAGIC:
            case PRAYER:
            case HITPOINTS:
                taskName = "Reach level " + targetLevel + " " + skill.getName();
                taskDescription = "Train your " + skill.getName() + " skill to level " + targetLevel + " to become stronger!";
                category = TaskCategory.COMBAT;
                break;

            // Gathering skills
            case MINING:
            case FISHING:
            case WOODCUTTING:
            case HUNTER:
            case FARMING:
                String resource = getSkillResource(skill, level);
                taskName = getSkillVerb(skill) + " " + resource;
                taskDescription = getSkillVerb(skill) + " " + resource + " until you reach level " + targetLevel + "!";
                break;

            // Production skills
            case COOKING:
            case FIREMAKING:
            case SMITHING:
            case CRAFTING:
            case FLETCHING:
            case HERBLORE:
            case RUNECRAFT:
            case CONSTRUCTION:
                String product = getSkillProduct(skill, level);
                taskName = "Make " + product;
                taskDescription = "Create " + product + " until you reach level " + targetLevel + "!";
                break;

            // Support skills
            case AGILITY:
                String course = getAgilityCourseName(level);
                taskName = "Complete laps of the " + course;
                taskDescription = "Complete laps of the " + course + " until you reach level " + targetLevel + "!";
                break;

            case THIEVING:
                String target = getThievingTarget(level);
                taskName = "Pickpocket " + target;
                taskDescription = "Pickpocket " + target + " until you reach level " + targetLevel + "!";
                break;

            case SLAYER:
                taskName = "Complete Slayer tasks";
                taskDescription = "Complete Slayer tasks from an appropriate master until level " + targetLevel + "!";
                break;

            default:
                taskName = "Train " + skill.getName() + " to level " + targetLevel;
                taskDescription = "Increase your " + skill.getName() + " skill to level " + targetLevel + " to grow stronger!";
        }

        // Build the task
        return Task.builder()
                .id(id)
                .name(taskName)
                .description(taskDescription)
                .difficulty(difficulty)
                .category(category)
                .source(TaskSource.CUSTOM)
                .experienceReward(calculateReward(difficulty))
                .pointsReward(calculatePoints(difficulty))
                .completed(false)
                .visible(true)
                .build();
    }

    /**
     * Calculate a target level increase based on current level and difficulty
     */
    private int getTargetLevelIncrease(int currentLevel, TaskDifficulty difficulty) {
        // Base increase
        int baseIncrease;

        switch (difficulty) {
            case EASY:
                baseIncrease = 1 + random.nextInt(2); // 1-2 levels
                break;
            case MEDIUM:
                baseIncrease = 3 + random.nextInt(3); // 3-5 levels
                break;
            case HARD:
                baseIncrease = 5 + random.nextInt(5); // 5-9 levels
                break;
            case ELITE:
                baseIncrease = 10 + random.nextInt(5); // 10-14 levels
                break;
            case MASTER:
                baseIncrease = 15 + random.nextInt(10); // 15-24 levels
                break;
            default:
                baseIncrease = 1;
        }

        // Scale down for higher levels
        if (currentLevel >= 80) {
            return Math.max(1, baseIncrease / 2);
        } else if (currentLevel >= 70) {
            return Math.max(1, baseIncrease * 2 / 3);
        } else if (currentLevel >= 50) {
            return Math.max(1, baseIncrease * 3 / 4);
        }

        return baseIncrease;
    }

    /**
     * Get a verb associated with a gathering skill
     */
    private String getSkillVerb(Skill skill) {
        switch (skill) {
            case MINING: return "Mine";
            case FISHING: return "Catch";
            case WOODCUTTING: return "Cut";
            case HUNTER: return "Hunt";
            case FARMING: return "Grow";
            default: return "Gather";
        }
    }

    /**
     * Get an appropriate resource for the skill and level
     */
    private String getSkillResource(Skill skill, int level) {
        switch (skill) {
            case MINING:
                if (level < 15) return "copper and tin ore";
                if (level < 30) return "iron ore";
                if (level < 40) return "coal";
                if (level < 55) return "mithril ore";
                if (level < 70) return "adamantite ore";
                if (level < 85) return "runite ore";
                return "amethyst";

            case FISHING:
                if (level < 20) return "shrimp and anchovies";
                if (level < 40) return "trout and salmon";
                if (level < 60) return "lobsters";
                if (level < 76) return "swordfish and tuna";
                if (level < 85) return "sharks";
                return "anglerfish or minnows";

            case WOODCUTTING:
                if (level < 15) return "regular logs";
                if (level < 30) return "oak logs";
                if (level < 45) return "willow logs";
                if (level < 60) return "maple logs";
                if (level < 75) return "yew logs";
                return "magic logs";

            case HUNTER:
                if (level < 20) return "crimson swifts";
                if (level < 40) return "tropical wagtails";
                if (level < 60) return "chinchompas";
                if (level < 80) return "red chinchompas";
                return "black chinchompas";

            case FARMING:
                if (level < 20) return "low-level crops";
                if (level < 40) return "mid-level crops";
                if (level < 60) return "higher-level crops";
                if (level < 80) return "fruit trees";
                return "high-level herbs and trees";

            default:
                return "resources";
        }
    }

    /**
     * Get an appropriate product for the skill and level
     */
    private String getSkillProduct(Skill skill, int level) {
        switch (skill) {
            case COOKING:
                if (level < 20) return "shrimp and meat";
                if (level < 40) return "trout and salmon";
                if (level < 60) return "lobsters";
                if (level < 80) return "sharks";
                return "anglerfish or manta rays";

            case FIREMAKING:
                if (level < 15) return "regular logs";
                if (level < 30) return "oak logs";
                if (level < 45) return "willow logs";
                if (level < 60) return "maple logs";
                if (level < 75) return "yew logs";
                return "magic logs or redwood logs";

            case SMITHING:
                if (level < 15) return "bronze items";
                if (level < 30) return "iron items";
                if (level < 50) return "steel items";
                if (level < 70) return "mithril items";
                if (level < 85) return "adamant items";
                return "rune items";

            case CRAFTING:
                if (level < 20) return "leather items";
                if (level < 40) return "jewelry";
                if (level < 60) return "dragonhide items";
                if (level < 80) return "battlestaves";
                return "high-level jewelry";

            case FLETCHING:
                if (level < 30) return "arrow shafts and low-level bows";
                if (level < 50) return "oak bows and arrows";
                if (level < 70) return "yew bows and bolts";
                if (level < 85) return "magic bows";
                return "dragon bolts or redwood shields";

            case HERBLORE:
                if (level < 30) return "low-level potions";
                if (level < 45) return "prayer potions";
                if (level < 65) return "super potions";
                if (level < 80) return "saradomin brews";
                return "super combat potions or stamina potions";

            case RUNECRAFT:
                if (level < 20) return "air runes";
                if (level < 35) return "cosmic runes";
                if (level < 50) return "law runes";
                if (level < 65) return "death runes";
                if (level < 77) return "blood runes";
                return "soul runes or combination runes";

            case CONSTRUCTION:
                if (level < 30) return "basic furniture";
                if (level < 50) return "oak furniture";
                if (level < 70) return "teak furniture";
                if (level < 85) return "mahogany furniture";
                return "high-level rooms or furniture";

            default:
                return "items";
        }
    }

    /**
     * Get an agility course appropriate for the level
     */
    private String getAgilityCourseName(int level) {
        if (level < 20) return "Gnome Stronghold Course";
        if (level < 30) return "Draynor Village Rooftop Course";
        if (level < 40) return "Varrock Rooftop Course";
        if (level < 50) return "Canifis Rooftop Course";
        if (level < 60) return "Falador Rooftop Course";
        if (level < 70) return "Seers' Village Rooftop Course";
        if (level < 80) return "Pollnivneach Rooftop Course";
        if (level < 90) return "Rellekka Rooftop Course";
        return "Ardougne Rooftop Course";
    }

    /**
     * Get a thieving target appropriate for the level
     */
    private String getThievingTarget(int level) {
        if (level < 20) return "Men/Women";
        if (level < 25) return "Farmers";
        if (level < 35) return "Warriors";
        if (level < 45) return "Rogues";
        if (level < 55) return "Master Farmers";
        if (level < 65) return "Guards";
        if (level < 75) return "Paladins";
        if (level < 85) return "Heroes";
        return "Elves";
    }

    /**
     * Get random monster for tasks
     */
    private String getRandomMonster() {
        String[] monsters = {
                "Goblins", "Cows", "Giant Rats", "Skeletons", "Zombies",
                "Hill Giants", "Moss Giants", "Fire Giants", "Ice Giants",
                "Dragons", "Demons", "TzHaar", "Barrows Brothers", "Kalphites"
        };
        return monsters[random.nextInt(monsters.length)];
    }

    /**
     * Get random item for tasks
     */
    private String getRandomItem() {
        String[] items = {
                "logs", "ores", "fish", "bones", "hides", "herbs", "seeds",
                "gems", "arrows", "runes", "potions", "food", "tools", "armor"
        };
        return items[random.nextInt(items.length)];
    }

    /**
     * Get random skill name
     */
    private String getRandomSkill() {
        String[] skills = {
                "Attack", "Strength", "Defence", "Ranged", "Prayer", "Magic",
                "Woodcutting", "Firemaking", "Cooking", "Fishing", "Mining",
                "Smithing", "Crafting", "Fletching", "Runecraft", "Construction",
                "Agility", "Herblore", "Thieving", "Slayer", "Farming", "Hunter"
        };
        return skills[random.nextInt(skills.length)];
    }

    /**
     * Calculate experience reward based on difficulty
     */
    private int calculateReward(TaskDifficulty difficulty) {
        switch (difficulty) {
            case EASY: return 1000 + random.nextInt(1000);
            case MEDIUM: return 2500 + random.nextInt(2500);
            case HARD: return 5000 + random.nextInt(5000);
            case ELITE: return 10000 + random.nextInt(10000);
            case MASTER: return 20000 + random.nextInt(15000);
            default: return 500;
        }
    }

    /**
     * Calculate points reward based on difficulty
     */
    private int calculatePoints(TaskDifficulty difficulty) {
        switch (difficulty) {
            case EASY: return 25 + random.nextInt(25);
            case MEDIUM: return 50 + random.nextInt(50);
            case HARD: return 100 + random.nextInt(100);
            case ELITE: return 200 + random.nextInt(200);
            case MASTER: return 400 + random.nextInt(300);
            default: return 10;
        }
    }
}

