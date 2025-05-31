package com.sololeveling;

import com.google.inject.Provides;
import com.sololeveling.api.OsrsApiManager;
import com.sololeveling.task.Task;
import com.sololeveling.task.TaskCategory;
import com.sololeveling.task.TaskDifficulty;
import com.sololeveling.task.TaskManager;
import com.sololeveling.task.TaskSource;
import com.sololeveling.tracking.PlayerTracker;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Solo Leveling",
	description = "An anime-themed skill tracking plugin with Solo Leveling aesthetics",
	tags = {"solo", "leveling", "anime", "skills", "tracker", "experience", "aesthetic"}
)
public class SoloLevelingPlugin extends Plugin
{
	// Fallback logger in case Lombok's @Slf4j doesn't work
	private static final Logger logger = Logger.getLogger(SoloLevelingPlugin.class.getName());

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SoloLevelingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SoloLevelingOverlay overlay;

	// Task manager for task tracking
	private final TaskManager taskManager = new TaskManager();

	// Player progress tracker
	private final PlayerTracker playerTracker = new PlayerTracker();

	// API manager for Hiscores and Wiki integration
	private final OsrsApiManager apiManager = new OsrsApiManager();

	// Track experience gains and levels
	private final Map<Skill, Integer> previousExperience = new HashMap<>();
	private final Map<Skill, Integer> previousLevels = new HashMap<>();
	private final Map<Skill, Long> lastExperienceGain = new HashMap<>();
	
	// Solo Leveling themed messages
	private static final String[] LEVEL_UP_MESSAGES = {
		"🗡️ Hunter %s has reached level %d! Power increases!",
		"⚔️ The Shadow Monarch grows stronger! Level %d in %s!",
		"🌟 You have leveled up! %s is now level %d!",
		"💀 Death cannot stop your growth! %s level %d achieved!",
		"🔥 The power within awakens! %s level %d unlocked!"
	};

	private static final String[] XP_GAIN_MESSAGES = {
		"💫 +%,d XP gained in %s! The grind continues...",
		"⚡ Experience flows like mana! +%,d %s XP",
		"🎯 Another step towards S-Rank! +%,d %s XP"
	};

	@Provides
	SoloLevelingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoloLevelingConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		logger.info("Solo Leveling plugin started! Welcome, Hunter!");
		overlayManager.add(overlay);
		
		// Initialize experience tracking
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			initializeExperienceTracking();

			// Initialize player tracker
			playerTracker.initialize(client);
		}

		// Initialize task manager
		taskManager.initialize();
		
		// Add debug logging for task initialization
		log.info("DEBUG: Task initialization started");
		
		// Create some initial tasks to ensure there's something to display
		createInitialTasks();
		
		// Add more debug info
		log.info("DEBUG: TaskManager has {} total tasks", taskManager.getAllTasks().size());
		log.info("DEBUG: TaskManager has {} visible tasks", taskManager.getVisibleTasks().size());
		for (Task task : taskManager.getVisibleTasks()) {
			log.info("DEBUG: Task - {}", task.getName());
		}
	}
	
	/**
	 * Create initial tasks to ensure there's something visible in the overlay
	 */
	private void createInitialTasks() {
		// Create and add some initial tasks
		Task initialTask = Task.builder()
			.id("initial_task_1")
			.name("Begin your journey")
			.description("Start leveling your skills and becoming stronger")
			.difficulty(TaskDifficulty.EASY)
			.category(TaskCategory.SKILLING)
			.source(TaskSource.CUSTOM)
			.experienceReward(1000)
			.pointsReward(50)
			.completed(false)
			.visible(true)
			.build();
		
		Task combatTask = Task.builder()
			.id("initial_combat_1")
			.name("Defeat a goblin")
			.description("Start your combat journey by defeating a goblin")
			.difficulty(TaskDifficulty.EASY)
			.category(TaskCategory.COMBAT)
			.source(TaskSource.CUSTOM)
			.experienceReward(500)
			.pointsReward(25)
			.completed(false)
			.visible(true)
			.build();
			
		taskManager.addTask(initialTask);
		taskManager.addTask(combatTask);
		
		// Generate a random challenge too
		generateRandomChallenge();
	}

	@Override
	protected void shutDown() throws Exception
	{
		logger.info("Solo Leveling plugin stopped! Until next time, Hunter...");
		overlayManager.remove(overlay);
		previousExperience.clear();
		previousLevels.clear();
		lastExperienceGain.clear();

		// Save player tracker session time
		playerTracker.updateSessionTime();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			initializeExperienceTracking();
			
			if (config.showLoginMessage())
			{
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
					ColorUtil.wrapWithColorTag("🗡️ Welcome back, Shadow Monarch! Your journey continues...", Color.CYAN), null);
			}
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final Skill skill = statChanged.getSkill();
		final int currentXp = statChanged.getXp();
		final int currentLevel = statChanged.getLevel();
		
		if (previousExperience.containsKey(skill))
		{
			final int previousXp = previousExperience.get(skill);
			final int previousLevel = previousLevels.getOrDefault(skill, currentLevel);
			
			// Check for experience gain
			if (currentXp > previousXp)
			{
				final int xpGain = currentXp - previousXp;
				lastExperienceGain.put(skill, System.currentTimeMillis());
				
				if (config.showXpGains() && xpGain > config.minimumXpToShow())
				{
					String message = String.format(getRandomXpMessage(), xpGain, skill.getName());
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
						ColorUtil.wrapWithColorTag(message, Color.GREEN), null);
				}
			}
			
			// Check for level up
			if (currentLevel > previousLevel)
			{
				if (config.showLevelUps())
				{
					String message = String.format(getRandomLevelUpMessage(), skill.getName(), currentLevel);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
						ColorUtil.wrapWithColorTag(message, Color.YELLOW), null);
				}
			}
		}
		
		// Update tracking
		previousExperience.put(skill, currentXp);
		previousLevels.put(skill, currentLevel);
		lastExperienceGain.put(skill, System.currentTimeMillis());
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		// Update game tick count in player tracker
		playerTracker.recordGameTick();

		// Check for quest completions
		clientThread.invoke(this::checkQuestCompletions);

		// Generate random challenges occasionally
		if (playerTracker.getGameTicks() % 600 == 0) // Every ~6 minutes (600 ticks)
		{
			clientThread.invoke(this::generateRandomChallenge);
		}

		// Check for task completions
		clientThread.invoke(this::checkTaskCompletions);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		// Update quest status in player tracker when varbits change
		// Quest states are stored in varbits, so this event will capture quest changes
		clientThread.invoke(() -> playerTracker.updateQuestStatus(client));

		// Check if this completes any tasks
		clientThread.invoke(this::checkQuestTaskCompletions);

		// Check if player has quest cape
		if (playerTracker.hasQuestCape() && Math.random() < 0.5) // 50% chance
		{
			clientThread.invoke(this::generateQuestChallenge);
		}
	}

	private void initializeExperienceTracking()
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		for (Skill skill : Skill.values())
		{
			// Skip non-trainable skills
			if (skill.getName().equals("Overall"))
			{
				continue;
			}
			previousExperience.put(skill, client.getSkillExperience(skill));
			previousLevels.put(skill, client.getRealSkillLevel(skill));
		}
	}

	private String getRandomLevelUpMessage()
	{
		return LEVEL_UP_MESSAGES[(int) (Math.random() * LEVEL_UP_MESSAGES.length)];
	}

	private String getRandomXpMessage()
	{
		return XP_GAIN_MESSAGES[(int) (Math.random() * XP_GAIN_MESSAGES.length)];
	}

	public Map<Skill, Long> getLastExperienceGain()
	{
		return lastExperienceGain;
	}
	public int getTotalLevel()
	{
		if (client.getLocalPlayer() == null)
		{
			return 0;
		}
		
		int totalLevel = 0;
		for (Skill skill : Skill.values())
		{
			// Skip non-trainable skills
			if (skill.getName().equals("Overall"))
			{
				continue;
			}
			totalLevel += client.getRealSkillLevel(skill);
		}
		return totalLevel;
	}

	public long getTotalExperience()
	{
		if (client.getLocalPlayer() == null)
		{
			return 0;
		}
		
		long totalXp = 0;
		for (Skill skill : Skill.values())
		{
			// Skip non-trainable skills
			if (skill.getName().equals("Overall"))
			{
				continue;
			}
			totalXp += client.getSkillExperience(skill);
		}
		return totalXp;
	}

	/**
	 * Get tasks that should be displayed in the overlay.
	 * Filters tasks based on configuration settings.
	 *
	 * @return List of tasks to display
	 */
	public List<Task> getTasks()
	{
		// Debug the task retrieval
		log.debug("DEBUG: Getting tasks for display. TaskManager initialized: {}", taskManager.isInitialized());
		log.debug("DEBUG: Total tasks available: {}", taskManager.getAllTasks().size());
		log.debug("DEBUG: Visible tasks available: {}", taskManager.getVisibleTasks().size());
		
		List<Task> visibleTasks = taskManager.getVisibleTasks();
		
		// Debug the visible tasks
		for (Task task : visibleTasks) {
			log.debug("DEBUG: Visible task: {} ({})", task.getName(), task.getSource());
		}

		// Apply filtering based on configuration
		List<Task> filteredTasks = visibleTasks.stream()
			.filter(task -> {
				// First check if we should filter by source
				if (!config.filterTasksBySource()) {
					return true;
				}
				
				// Then apply source filters
				if (!config.showLeagueTasks() &&
					(task.getSource() == TaskSource.LEAGUE_RAGING_ECHOES ||
					task.getSource() == TaskSource.LEAGUE_TRAILBLAZER))
				{
					return false;
				}
				if (!config.showQuestTasks() && task.getSource() == TaskSource.QUEST)
				{
					return false;
				}
				if (!config.showCustomTasks() && task.getSource() == TaskSource.CUSTOM)
				{
					return false;
				}
				if (!config.showRandomTasks() && 
					(task.getId().startsWith("random_") || task.getId().startsWith("personalized_")))
				{
					return false;
				}
				return true;
			})
			.limit(config.maxTasksShown())
			.collect(Collectors.toList());
		
		// Debug the filtered tasks
		log.debug("DEBUG: Filtered tasks count: {}", filteredTasks.size());
		for (Task task : filteredTasks) {
			log.debug("DEBUG: Filtered task: {}", task.getName());
		}

		return filteredTasks;
	}

	/**
	 * Complete a task and show a message if configured.
	 *
	 * @param taskId ID of the task to complete
	 * @return true if task was completed, false otherwise
	 */
	public boolean completeTask(String taskId)
	{
		boolean completed = taskManager.completeTask(taskId);

		if (completed && config.showTaskCompletionMessages() && client.getGameState() == GameState.LOGGED_IN)
		{
			Task task = taskManager.getTaskById(taskId);
			if (task != null)
			{
				String message = String.format("🎯 Task completed: %s", task.getName());
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
					ColorUtil.wrapWithColorTag(message, Color.GREEN), null);

				if (config.showTaskRewards() && (task.getExperienceReward() > 0 || task.getPointsReward() > 0))
				{
					String rewardMessage = String.format("💰 Rewards: %,d XP, %d points",
						task.getExperienceReward(), task.getPointsReward());
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
						ColorUtil.wrapWithColorTag(rewardMessage, Color.YELLOW), null);
				}
			}
		}

		return completed;
	}

	/**
	 * Check for quest completions (placeholder implementation)
	 */
	private void checkQuestCompletions() {
		// We'll use the PlayerTracker's method instead of reimplementing
		playerTracker.checkQuestCompletions(client);
		
		// Check if any tasks need to be updated based on quest status
		checkQuestTaskCompletions();
	}

	/**
	 * Generate a random challenge (placeholder implementation)
	 */
	private void generateRandomChallenge() {
		if (!config.showRandomTasks()) {
			return;
		}
		
		// Create a random task
		TaskDifficulty difficulty = getRandomDifficulty();
		TaskCategory category = getRandomCategory();
		
		log.debug("DEBUG: Generating random task with difficulty {} and category {}", difficulty, category);
		
		Task randomTask = taskManager.createRandomTask(difficulty, category);
		if (randomTask != null) {
			taskManager.addTask(randomTask);
			
			if (config.showTaskCompletionMessages()) {
				String message = "🌟 New task generated: " + randomTask.getName();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
					ColorUtil.wrapWithColorTag(message, Color.CYAN), null);
			}
		}
	}

	/**
	 * Check for task completions (placeholder implementation)
	 */
	private void checkTaskCompletions() {
		// This would contain logic to check if tasks have been completed automatically
		// For now we're just logging for debugging
		log.debug("DEBUG: Checking for task completions");
	}

	/**
	 * Check for quest task completions (placeholder implementation)
	 */
	private void checkQuestTaskCompletions() {
		if (playerTracker == null || taskManager == null) {
			return;
		}
		
		log.debug("DEBUG: Checking for quest task completions");
		
		// Get incomplete quest tasks
		List<Task> questTasks = taskManager.getIncompleteTasksBySource(TaskSource.QUEST);
		log.debug("DEBUG: Found {} incomplete quest tasks", questTasks.size());
		
		// Check each task to see if its related quest is completed
		for (Task task : questTasks) {
			if (task.getRelatedQuestId() != null &&
				playerTracker.isQuestCompleted(task.getRelatedQuestId())) {
				log.debug("DEBUG: Quest task completed: {}", task.getName());
				completeTask(task.getId());
			}
		}
	}

	/**
	 * Generate a quest challenge (placeholder implementation)
	 */
	private void generateQuestChallenge() {
		log.debug("DEBUG: Generating quest challenge");
		Task questTask = taskManager.createQuestTask();
		if (questTask != null) {
			taskManager.addTask(questTask);
			
			if (config.showTaskCompletionMessages()) {
				String message = "📜 New quest task: " + questTask.getName();
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
					ColorUtil.wrapWithColorTag(message, Color.CYAN), null);
			}
		}
	}

	/**
	 * Get a random difficulty level
	 */
	private TaskDifficulty getRandomDifficulty() {
		double rand = Math.random();
		if (rand < 0.4) {
			return TaskDifficulty.EASY;
		} else if (rand < 0.7) {
			return TaskDifficulty.MEDIUM;
		} else if (rand < 0.9) {
			return TaskDifficulty.HARD;
		} else if (rand < 0.95) {
			return TaskDifficulty.ELITE;
		} else {
			return TaskDifficulty.MASTER;
		}
	}

	/**
	 * Get a random category
	 */
	private TaskCategory getRandomCategory() {
		TaskCategory[] categories = TaskCategory.values();
		return categories[(int)(Math.random() * categories.length)];
	}

	/**
	 * Get appropriate difficulty and action for skill levels
	 */
	private TaskDifficulty getTaskDifficultyForLevel(int level) {
		if (level < 30) return TaskDifficulty.EASY;
		if (level < 50) return TaskDifficulty.MEDIUM;
		if (level < 70) return TaskDifficulty.HARD;
		if (level < 90) return TaskDifficulty.ELITE;
		return TaskDifficulty.MASTER;
	}

	/**
	 * Get appropriate skilling action for a skill
	 */
	private String getSkillingAction(Skill skill) {
		switch (skill) {
			case MINING: return "Mine";
			case FISHING: return "Catch";
			case WOODCUTTING: return "Cut";
			case COOKING: return "Cook";
			case SMITHING: return "Smith";
			default: return "Train";
		}
	}

	/**
	 * Get appropriate entity for skill level
	 */
	private String getSkillEntityForLevel(Skill skill, int level) {
		switch (skill) {
			case WOODCUTTING:
				if (level < 30) return "oak logs";
				if (level < 45) return "willow logs";
				if (level < 60) return "maple logs";
				return "yew logs";
			case FISHING:
				if (level < 40) return "trout";
				if (level < 60) return "lobsters";
				return "sharks";
			default:
				return "resources";
		}
	}

	/**
	 * Get a random monster for task generation
	 */
	private String getRandomMonster() {
		String[] monsters = {
			"Goblin", "Hill Giant", "Moss Giant", "Skeleton", "Zombie", "Ghost",
			"Bandit", "Farmer", "Guard", "Lesser Demon", "Greater Demon", "Black Demon",
			"Green Dragon", "Blue Dragon", "Red Dragon", "Black Dragon", "Iron Dragon", "Steel Dragon",
			"Abyssal Demon", "Dark Beast", "Cave Kraken", "Gargoyle", "Nechryael", "Spiritual Mage"
		};
		return monsters[(int)(Math.random() * monsters.length)];
	}

	/**
	 * Get a random item for task generation
	 */
	private String getRandomItem() {
		String[] items = {
			"Bones", "Big Bones", "Feathers", "Runes", "Herbs", "Gems",
			"Coal", "Iron Ore", "Gold Ore", "Mithril Ore", "Adamantite Ore", "Runite Ore",
			"Logs", "Oak Logs", "Willow Logs", "Maple Logs", "Yew Logs", "Magic Logs",
			"Raw Shrimp", "Raw Trout", "Raw Salmon", "Raw Lobster", "Raw Swordfish", "Raw Shark"
		};
		return items[(int)(Math.random() * items.length)];
	}

	/**
	 * Get a random skill for task generation
	 */
	private String getRandomSkill() {
		Skill[] skills = {
			Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.PRAYER, Skill.MAGIC,
			Skill.RUNECRAFT, Skill.CONSTRUCTION, Skill.HITPOINTS, Skill.AGILITY, Skill.HERBLORE,
			Skill.THIEVING, Skill.CRAFTING, Skill.FLETCHING, Skill.SLAYER, Skill.HUNTER,
			Skill.MINING, Skill.SMITHING, Skill.FISHING, Skill.COOKING, Skill.FIREMAKING,
			Skill.WOODCUTTING, Skill.FARMING
		};
		return skills[(int)(Math.random() * skills.length)].getName();
	}
}
