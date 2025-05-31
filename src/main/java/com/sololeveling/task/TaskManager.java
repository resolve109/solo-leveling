package com.sololeveling.task;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages tasks for the Solo Leveling plugin
 */
@Slf4j
public class TaskManager {
    private final List<Task> allTasks = new ArrayList<>();
    private final List<Task> completedTasks = new ArrayList<>();
    private final List<Task> visibleTasks = new ArrayList<>();

    /**
     * Initialize the task manager with default tasks
     */
    public void initialize() {
        loadDefaultTasks();
        updateTaskLists();
        log.info("Task Manager initialized with {} tasks", allTasks.size());
    }

    /**
     * Load default tasks from leagues and quests
     */
    private void loadDefaultTasks() {
        // Add sample league tasks
        loadLeagueTasks();

        // Add sample quest tasks
        loadQuestTasks();
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
            .pointsReward(50)
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
            .experienceReward(10000)
            .pointsReward(100)
            .completed(false)
            .visible(true)
            .build());

        // Trailblazer Reloaded League - Skilling tasks
        allTasks.add(Task.builder()
            .id("trl_skilling_1")
            .name("Reach Level 70 in a Skill")
            .description("Reach level 70 in any skill.")
            .difficulty(TaskDifficulty.MEDIUM)
            .category(TaskCategory.SKILLING)
            .source(TaskSource.LEAGUE_TRAILBLAZER)
            .experienceReward(7000)
            .pointsReward(70)
            .completed(false)
            .visible(true)
            .build());

        allTasks.add(Task.builder()
            .id("trl_skilling_2")
            .name("Reach Level 99 in a Skill")
            .description("Reach level 99 in any skill.")
            .difficulty(TaskDifficulty.MASTER)
            .category(TaskCategory.SKILLING)
            .source(TaskSource.LEAGUE_TRAILBLAZER)
            .experienceReward(25000)
            .pointsReward(250)
            .completed(false)
            .visible(true)
            .build());
    }

    /**
     * Load tasks from OSRS Quests
     */
    private void loadQuestTasks() {
        // Novice quests
        allTasks.add(Task.builder()
            .id("quest_1")
            .name("Complete Cook's Assistant")
            .description("Help the cook in Lumbridge Castle to make a cake.")
            .difficulty(TaskDifficulty.EASY)
            .category(TaskCategory.QUEST)
            .source(TaskSource.QUEST)
            .experienceReward(1000)
            .pointsReward(10)
            .completed(false)
            .visible(true)
            .build());

        // Experienced quests
        allTasks.add(Task.builder()
            .id("quest_2")
            .name("Complete Dragon Slayer")
            .description("Slay the mighty dragon Elvarg on Crandor Island.")
            .difficulty(TaskDifficulty.HARD)
            .category(TaskCategory.QUEST)
            .source(TaskSource.QUEST)
            .experienceReward(15000)
            .pointsReward(150)
            .completed(false)
            .visible(true)
            .build());

        // Master quests
        allTasks.add(Task.builder()
            .id("quest_3")
            .name("Complete Dragon Slayer II")
            .description("Investigate the Dragonkin and face a new dragon threat.")
            .difficulty(TaskDifficulty.MASTER)
            .category(TaskCategory.QUEST)
            .source(TaskSource.QUEST)
            .experienceReward(30000)
            .pointsReward(300)
            .completed(false)
            .visible(true)
            .build());
    }

    /**
     * Update the task lists (completed and visible)
     */
    private void updateTaskLists() {
        completedTasks.clear();
        completedTasks.addAll(allTasks.stream()
            .filter(Task::isCompleted)
            .collect(Collectors.toList()));

        visibleTasks.clear();
        visibleTasks.addAll(allTasks.stream()
            .filter(Task::isVisible)
            .collect(Collectors.toList()));
    }

    /**
     * Mark a task as completed
     * @param taskId the id of the task to complete
     * @return true if the task was found and marked as completed, false otherwise
     */
    public boolean completeTask(String taskId) {
        for (Task task : allTasks) {
            if (task.getId().equals(taskId) && !task.isCompleted()) {
                task.setCompleted(true);
                updateTaskLists();
                return true;
            }
        }
        return false;
    }

    /**
     * Reset a task to incomplete
     * @param taskId the id of the task to reset
     * @return true if the task was found and reset, false otherwise
     */
    public boolean resetTask(String taskId) {
        for (Task task : allTasks) {
            if (task.getId().equals(taskId) && task.isCompleted()) {
                task.setCompleted(false);
                updateTaskLists();
                return true;
            }
        }
        return false;
    }

    /**
     * Set a task's visibility
     * @param taskId the id of the task
     * @param visible whether the task should be visible
     * @return true if the task was found and visibility was set, false otherwise
     */
    public boolean setTaskVisibility(String taskId, boolean visible) {
        for (Task task : allTasks) {
            if (task.getId().equals(taskId)) {
                task.setVisible(visible);
                updateTaskLists();
                return true;
            }
        }
        return false;
    }

    /**
     * Add a new task to the task manager
     *
     * @param task the task to add
     * @return the added task
     */
    public Task addTask(Task task) {
        // Add the task to the all tasks list
        allTasks.add(task);

        // Update the task lists
        updateTaskLists();

        // Log the addition
        log.debug("Added new task: {} ({})", task.getName(), task.getId());

        return task;
    }

    /**
     * Add multiple tasks at once
     *
     * @param tasks the list of tasks to add
     */
    public void addTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }

        // Add all tasks
        allTasks.addAll(tasks);

        // Update the task lists
        updateTaskLists();

        // Log the addition
        log.debug("Added {} new tasks", tasks.size());
    }

    /**
     * Remove a task from the task manager
     *
     * @param taskId the ID of the task to remove
     * @return true if the task was removed, false otherwise
     */
    public boolean removeTask(String taskId) {
        Task taskToRemove = null;
        for (Task task : allTasks) {
            if (task.getId().equals(taskId)) {
                taskToRemove = task;
                break;
            }
        }

        if (taskToRemove != null) {
            allTasks.remove(taskToRemove);
            updateTaskLists();
            log.debug("Removed task: {} ({})", taskToRemove.getName(), taskToRemove.getId());
            return true;
        }

        return false;
    }

    /**
     * Get all tasks
     * @return an unmodifiable list of all tasks
     */
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(allTasks);
    }

    /**
     * Get completed tasks
     * @return an unmodifiable list of completed tasks
     */
    public List<Task> getCompletedTasks() {
        return Collections.unmodifiableList(completedTasks);
    }

    /**
     * Get visible tasks
     * @return an unmodifiable list of visible tasks
     */
    public List<Task> getVisibleTasks() {
        return Collections.unmodifiableList(visibleTasks);
    }

    /**
     * Get tasks by category
     * @param category the category to filter by
     * @return a list of tasks in the specified category
     */
    public List<Task> getTasksByCategory(TaskCategory category) {
        return allTasks.stream()
            .filter(task -> task.getCategory() == category)
            .collect(Collectors.toList());
    }

    /**
     * Get tasks by difficulty
     * @param difficulty the difficulty to filter by
     * @return a list of tasks with the specified difficulty
     */
    public List<Task> getTasksByDifficulty(TaskDifficulty difficulty) {
        return allTasks.stream()
            .filter(task -> task.getDifficulty() == difficulty)
            .collect(Collectors.toList());
    }

    /**
     * Get tasks by source
     * @param source the source to filter by
     * @return a list of tasks from the specified source
     */
    public List<Task> getTasksBySource(TaskSource source) {
        return allTasks.stream()
            .filter(task -> task.getSource() == source)
            .collect(Collectors.toList());
    }

    /**
     * Get a task by its ID
     * @param taskId the id of the task to find
     * @return the task, or null if not found
     */
    public Task getTaskById(String taskId) {
        for (Task task : allTasks) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }
}
