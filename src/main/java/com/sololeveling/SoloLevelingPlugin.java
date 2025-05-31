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
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Solo Leveling",
	description = "An anime-themed skill tracking plugin with Solo Leveling aesthetics",
	tags = {"solo", "leveling", "anime", "skills", "tracker", "experience", "aesthetic"}
)
public class SoloLevelingPlugin extends Plugin
{
	@Inject
	private Client client;

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

	@Override
	protected void startUp() throws Exception
	{
		log.info("Solo Leveling plugin started! Welcome, Hunter!");
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
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Solo Leveling plugin stopped! Until next time, Hunter...");
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
		checkQuestCompletions();

		// Generate random challenges occasionally
		if (playerTracker.getGameTicks() % 600 == 0) // Every ~6 minutes (600 ticks)
		{
			generateRandomChallenge();
		}

		// Check for task completions
		checkTaskCompletions();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		// Update quest status in player tracker when varbits change
		// Quest states are stored in varbits, so this event will capture quest changes
		playerTracker.updateQuestStatus(client);

		// Check if this completes any tasks
		checkQuestTaskCompletions();

		// Check if player has quest cape
		if (playerTracker.hasQuestCape() && Math.random() < 0.5) // 50% chance
		{
			generateQuestChallenge();
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
		List<Task> visibleTasks = taskManager.getVisibleTasks();

		// Apply filtering based on configuration
		if (config.filterTasksBySource())
		{
			visibleTasks = visibleTasks.stream()
				.filter(task -> {
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
					return true;
				})
				.limit(config.maxTasksShown())
				.collect(Collectors.toList());
		}
		else
		{
			// Just limit the number of tasks shown
			if (visibleTasks.size() > config.maxTasksShown())
			{
				visibleTasks = visibleTasks.subList(0, config.maxTasksShown());
			}
		}

		return visibleTasks;
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
				// Show completion message
				String message = String.format("⚔️ Task completed: %s! You gained %d points and %d XP!",
					task.getName(), task.getPointsReward(), task.getExperienceReward());
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
					ColorUtil.wrapWithColorTag(message, config.secondaryColor()), null);
			}
		}

		return completed;
	}

	/**
	 * Get the player's stats from the Hiscores API
	 *
	 * @return a map of skills to their hiscore data, or null if failed
	 */
	public Map<Skill, OsrsApiManager.PlayerSkillData> getPlayerHiscores()
	{
		if (client.getLocalPlayer() == null)
		{
			return null;
		}

		String playerName = client.getLocalPlayer().getName();
		return apiManager.getPlayerHiscores(playerName);
	}

	/**
	 * Look up information about a game entity on the OSRS Wiki
	 *
	 * @param entityName the name of the entity to look up
	 * @return information about the entity
	 */
	public OsrsApiManager.WikiEntityInfo lookupWikiInfo(String entityName)
	{
		return apiManager.getWikiEntityInfo(entityName);
	}

	/**
	 * Get a direct wiki URL for an entity
	 *
	 * @param entityName the name of the entity
	 * @return the wiki URL
	 */
	public String getWikiUrl(String entityName)
	{
		return apiManager.getWikiUrl(entityName);
	}

	/**
	 * Generate wiki-based tasks for the player
	 */
	public void generateWikiBasedTasks()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		// Choose a skill to focus on
		Skill[] skills = {Skill.WOODCUTTING, Skill.FISHING, Skill.MINING, Skill.FARMING,
						  Skill.SLAYER, Skill.HUNTER};

		Skill focusSkill = skills[(int) (Math.random() * skills.length)];
		int playerLevel = client.getRealSkillLevel(focusSkill);

		// Generate a wiki-based task for appropriate level
		String entityName = getSkillEntityForLevel(focusSkill, playerLevel);
		if (entityName == null)
		{
			return;
		}

		// Look up wiki information
		OsrsApiManager.WikiEntityInfo info = lookupWikiInfo(entityName);

		// Create the task
		String taskAction = getSkillingAction(focusSkill);
		String taskName = String.format("Arise: %s %s", taskAction, entityName);

		String description;
		if (info.isFound())
		{
			// Use a shortened wiki description
			String shortDesc = info.getDescription();
			if (shortDesc.length() > 100)
			{
				shortDesc = shortDesc.substring(0, 100) + "...";
			}
			description = String.format("%s %s. Wiki: %s", taskAction, entityName, shortDesc);
		}
		else
		{
			description = String.format("%s %s to grow stronger!", taskAction, entityName);
		}

		Task newTask = Task.builder()
			.id("wiki_" + System.currentTimeMillis())
			.name(taskName)
			.description(description)
			.difficulty(getTaskDifficultyForLevel(playerLevel))
			.category(TaskCategory.SKILLING)
			.source(TaskSource.CUSTOM)
			.experienceReward(playerLevel * 100)
			.pointsReward(playerLevel / 2)
			.completed(false)
			.visible(true)
			.build();

		taskManager.addTask(newTask);

		// Notify player
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
			ColorUtil.wrapWithColorTag("🗡️ A new challenge arises, Hunter! " + taskName, Color.CYAN), null);
	}

	/**
	 * Compare the player's in-game stats with their hiscores data
	 * and generate appropriate tasks
	 */
	public void compareWithHiscoresAndGenerateTasks()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Map<Skill, OsrsApiManager.PlayerSkillData> hiscores = getPlayerHiscores();
		if (hiscores == null || hiscores.isEmpty())
		{
			return;
		}

		// Find skills where the player's in-game level is lower than hiscores
		// (could happen if they've been training on another account)
		for (Map.Entry<Skill, OsrsApiManager.PlayerSkillData> entry : hiscores.entrySet())
		{
			Skill skill = entry.getKey();
			int hiscoreLevel = entry.getValue().getLevel();
			int currentLevel = client.getRealSkillLevel(skill);

			if (currentLevel < hiscoreLevel)
			{
				// Create a task to reach their previous best
				String taskName = String.format("Reclaim your strength: Reach Level %d in %s",
					hiscoreLevel, skill.getName());

				String description = String.format(
					"The Shadow Monarch's power must be reclaimed! Reach level %d in %s to regain your former strength.",
					hiscoreLevel, skill.getName());

				Task newTask = Task.builder()
					.id("hiscore_" + skill.getName().toLowerCase() + "_" + System.currentTimeMillis())
					.name(taskName)
					.description(description)
					.difficulty(getTaskDifficultyForLevel(hiscoreLevel))
					.category(TaskCategory.SKILLING)
					.source(TaskSource.CUSTOM)
					.experienceReward(hiscoreLevel * 150)
					.pointsReward(hiscoreLevel)
					.completed(false)
					.visible(true)
					.build();

				taskManager.addTask(newTask);

				// Only create one such task at a time
				break;
			}
		}
	}

	/**
	 * Get an appropriate entity for a skill at the given level
	 */
	private String getSkillEntityForLevel(Skill skill, int level)
	{
		// This could be expanded with more entities for each skill and level range
		if (skill == Skill.WOODCUTTING)
		{
			if (level < 15) return "Regular trees";
			else if (level < 30) return "Oak trees";
			else if (level < 45) return "Willow trees";
			else if (level < 60) return "Maple trees";
			else if (level < 75) return "Yew trees";
			else return "Magic trees";
		}
		else if (skill == Skill.FISHING)
		{
			if (level < 20) return "Shrimp";
			else if (level < 40) return "Trout";
			else if (level < 60) return "Lobster";
			else if (level < 80) return "Shark";
			else return "Anglerfish";
		}
		else if (skill == Skill.MINING)
		{
			if (level < 15) return "Copper ore";
			else if (level < 30) return "Iron ore";
			else if (level < 45) return "Coal";
			else if (level < 70) return "Mithril ore";
			else if (level < 85) return "Adamantite ore";
			else return "Runite ore";
		}
		else if (skill == Skill.FARMING)
		{
			if (level < 20) return "Potato seeds";
			else if (level < 40) return "Strawberry seeds";
			else if (level < 60) return "Snapdragon seeds";
			else if (level < 80) return "Torstol seeds";
			else return "Magic saplings";
		}
		else if (skill == Skill.SLAYER)
		{
			if (level < 20) return "Crawling Hands";
			else if (level < 40) return "Pyrefiends";
			else if (level < 60) return "Aberrant Spectres";
			else if (level < 80) return "Gargoyles";
			else return "Abyssal demons";
		}
		else if (skill == Skill.HUNTER)
		{
			if (level < 20) return "Crimson Swifts";
			else if (level < 40) return "Tropical Wagtails";
			else if (level < 60) return "Red Chinchompas";
			else if (level < 80) return "Black Chinchompas";
			else return "Herbiboar";
		}

		return null;
	}

	/**
	 * Get the player tracker
	 */
	public PlayerTracker getPlayerTracker()
	{
		return playerTracker;
	}

	/**
	 * Get the API manager
	 */
	public OsrsApiManager getApiManager()
	{
		return apiManager;
	}
}
