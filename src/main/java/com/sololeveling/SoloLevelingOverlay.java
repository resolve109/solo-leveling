package com.sololeveling;

import com.sololeveling.task.Task;
import com.sololeveling.task.TaskCategory;
import com.sololeveling.task.TaskDifficulty;
import com.sololeveling.task.TaskSource;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

@Slf4j
public class SoloLevelingOverlay extends Overlay
{
	private final Client client;
	private final SoloLevelingPlugin plugin;
	private final SoloLevelingConfig config;
	private final PanelComponent panelComponent = new PanelComponent();
	private final NumberFormat numberFormat = NumberFormat.getInstance();

	@Inject
	private SoloLevelingOverlay(Client client, SoloLevelingPlugin plugin, SoloLevelingConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showOverlay() || client.getLocalPlayer() == null)
		{
			return null;
		}

		panelComponent.getChildren().clear();
		panelComponent.setBackgroundColor(config.backgroundColor());
		panelComponent.setBorder(new Rectangle(2, 2, 2, 2));

		// Update overlay position based on config
		updateOverlayPosition();

		// Title with hunter rank
		String title = "🗡️ " + getHunterRank();
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(title)
			.color(config.primaryColor())
			.build());

		// Total Level
		if (config.showTotalLevel())
		{
			int totalLevel = plugin.getTotalLevel();
			panelComponent.getChildren().add(LineComponent.builder()
				.left("⚡ Total Level:")
				.leftColor(config.textColor())
				.right(String.valueOf(totalLevel))
				.rightColor(config.secondaryColor())
				.build());
		}

		// Total Experience
		if (config.showTotalExperience())
		{
			long totalXp = plugin.getTotalExperience();
			panelComponent.getChildren().add(LineComponent.builder()
				.left("💫 Total XP:")
				.leftColor(config.textColor())
				.right(numberFormat.format(totalXp))
				.rightColor(config.secondaryColor())
				.build());
		}

		// Recent XP Gains
		if (config.showRecentXpGains())
		{
			addRecentXpGains();
		}

		// Tasks
		if (config.showTasks())
		{
			addTasks();
		}

		// Hunter Status
		addHunterStatus();

		return panelComponent.render(graphics);
	}

	private void updateOverlayPosition()
	{
		switch (config.overlayPosition())
		{
			case TOP_LEFT:
				setPosition(OverlayPosition.TOP_LEFT);
				break;
			case TOP_RIGHT:
				setPosition(OverlayPosition.TOP_RIGHT);
				break;
			case BOTTOM_LEFT:
				setPosition(OverlayPosition.BOTTOM_LEFT);
				break;
			case BOTTOM_RIGHT:
				setPosition(OverlayPosition.BOTTOM_RIGHT);
				break;
		}
	}

	private void addRecentXpGains()
	{
		Map<Skill, Long> lastGains = plugin.getLastExperienceGain();
		long currentTime = System.currentTimeMillis();
		long cutoffTime = currentTime - (config.recentXpDuration() * 1000L);

		boolean hasRecentGains = false;
		for (Map.Entry<Skill, Long> entry : lastGains.entrySet())
		{
			if (entry.getValue() > cutoffTime)
			{
				if (!hasRecentGains)
				{
					// Add separator
					panelComponent.getChildren().add(LineComponent.builder()
						.left("━━━━━━━━━━━━━━━━")
						.leftColor(config.primaryColor())
						.build());
					
					panelComponent.getChildren().add(LineComponent.builder()
						.left("🎯 Recent Gains:")
						.leftColor(config.textColor())
						.build());
					hasRecentGains = true;
				}

				Skill skill = entry.getKey();
				long timeSince = (currentTime - entry.getValue()) / 1000;
				String timeText = timeSince < 60 ? timeSince + "s" : (timeSince / 60) + "m";
				
				panelComponent.getChildren().add(LineComponent.builder()
					.left("  " + getSkillEmoji(skill) + " " + skill.getName())
					.leftColor(config.textColor())
					.right(timeText + " ago")
					.rightColor(Color.GRAY)
					.build());
			}
		}
	}

	private void addTasks()
	{
		log.debug("DEBUG: Adding tasks to overlay. Config showTasks: {}", config.showTasks());
		
		List<Task> tasks = plugin.getTasks();
		log.debug("DEBUG: Retrieved {} tasks from plugin", tasks != null ? tasks.size() : 0);
		
		if (tasks == null || tasks.isEmpty())
		{
			// Add debug information to overlay when no tasks are available
			panelComponent.getChildren().add(LineComponent.builder()
				.left("━━━━━━━━━━━━━━━━")
				.leftColor(config.primaryColor())
				.build());

			panelComponent.getChildren().add(LineComponent.builder()
				.left("📋 Tasks:")
				.leftColor(config.textColor())
				.build());
				
			panelComponent.getChildren().add(LineComponent.builder()
				.left("  No tasks available")
				.leftColor(Color.GRAY)
				.build());
				
			log.debug("DEBUG: No tasks to display in overlay");
			return;
		}

		// Add separator
		panelComponent.getChildren().add(LineComponent.builder()
			.left("━━━━━━━━━━━━━━━━")
			.leftColor(config.primaryColor())
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("📋 Tasks:")
			.leftColor(config.textColor())
			.build());

		for (Task task : tasks)
		{
			String taskText = task.getName();
			if (config.filterTasksBySource()) {
				taskText += " (" + task.getCategory() + ")";
			}
			
			Color difficultyColor = getDifficultyColor(task.getDifficulty());
			
			panelComponent.getChildren().add(LineComponent.builder()
				.left("  " + taskText)
				.leftColor(config.textColor())
				.right(task.getDifficulty().toString())
				.rightColor(difficultyColor)
				.build());
				
			log.debug("DEBUG: Added task to overlay: {}", task.getName());
		}
	}
	
	/**
	 * Get appropriate color for difficulty level
	 */
	private Color getDifficultyColor(TaskDifficulty difficulty) {
		switch (difficulty) {
			case EASY: 
				return Color.GREEN;
			case MEDIUM: 
				return Color.CYAN;
			case HARD: 
				return Color.ORANGE;
			case ELITE: 
				return new Color(255, 0, 255); // Magenta
			case MASTER: 
				return Color.RED;
			default: 
				return config.secondaryColor();
		}
	}

	private void addHunterStatus()
	{
		// Add separator
		panelComponent.getChildren().add(LineComponent.builder()
			.left("━━━━━━━━━━━━━━━━")
			.leftColor(config.primaryColor())
			.build());

		// Power Level (simplified calculation)
		int powerLevel = calculatePowerLevel();
		panelComponent.getChildren().add(LineComponent.builder()
			.left("💀 Power Level:")
			.leftColor(config.textColor())
			.right(numberFormat.format(powerLevel))
			.rightColor(config.primaryColor())
			.build());

		// Next milestone
		String nextMilestone = getNextMilestone();
		if (!nextMilestone.isEmpty())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("🎯 Next Goal:")
				.leftColor(config.textColor())
				.right(nextMilestone)
				.rightColor(config.secondaryColor())
				.build());
		}
	}

	private String getHunterRank()
	{
		if (!config.useCustomRank())
		{
			return config.hunterTitle();
		}

		int totalLevel = plugin.getTotalLevel();
		
		if (totalLevel >= 2277) // Max total level
		{
			return "🌟 S-Rank Hunter";
		}
		else if (totalLevel >= 2000)
		{
			return "💎 A-Rank Hunter";
		}
		else if (totalLevel >= 1750)
		{
			return "🔷 B-Rank Hunter";
		}
		else if (totalLevel >= 1500)
		{
			return "🔸 C-Rank Hunter";
		}
		else if (totalLevel >= 1000)
		{
			return "⚪ D-Rank Hunter";
		}
		else
		{
			return "🟤 E-Rank Hunter";
		}
	}

	private int calculatePowerLevel()
	{
		// Enhanced power level calculation based on:
		// 1. Total skill levels
		// 2. Combat level
		// 3. Quest points
		// 4. Achievement diary completion
		// 5. Combat achievements

		int totalLevel = plugin.getTotalLevel();
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return 0;
		}

		int combatLevel = player.getCombatLevel();
		int questPoints = client.getVarpValue(101); // Quest points varbit

		// Calculate the power level using a weighted formula
		// Base power from total level (50%)
		int basePower = totalLevel * 5;

		// Combat contribution (30%)
		int combatPower = combatLevel * 50;

		// Quest contribution (20%)
		int questPower = questPoints * 25;

		// Additional power from achievements, diaries, etc.
		// These would need to be tracked separately
		int achievementPower = 0;

		// Sum all components
		int powerLevel = basePower + combatPower + questPower + achievementPower;

		// Apply scaling to make the number more impressive
		// This puts most beginners at around 5,000-10,000 power level
		// Mid-game players at 20,000-50,000
		// End-game players at 100,000+
		return powerLevel;
	}

	private String getNextMilestone()
	{
		int totalLevel = plugin.getTotalLevel();
		
		if (totalLevel < 1000)
		{
			return "Level 1000 Total";
		}
		else if (totalLevel < 1500)
		{
			return "Level 1500 Total";
		}
		else if (totalLevel < 1750)
		{
			return "Level 1750 Total";
		}
		else if (totalLevel < 2000)
		{
			return "Level 2000 Total";
		}
		else if (totalLevel < 2277)
		{
			return "Max Total Level";
		}
		else
		{
			return "Legendary Status";
		}
	}

	private String getSkillEmoji(Skill skill)
	{
		switch (skill)
		{
			case ATTACK: return "⚔️";
			case DEFENCE: return "🛡️";
			case STRENGTH: return "💪";
			case HITPOINTS: return "❤️";
			case RANGED: return "🏹";
			case PRAYER: return "🙏";
			case MAGIC: return "🔮";
			case COOKING: return "🍳";
			case WOODCUTTING: return "🪓";
			case FLETCHING: return "🏹";
			case FISHING: return "🎣";
			case FIREMAKING: return "🔥";
			case CRAFTING: return "🛠️";
			case SMITHING: return "⚒️";
			case MINING: return "⛏️";
			case HERBLORE: return "🧪";
			case AGILITY: return "🏃";
			case THIEVING: return "🗝️";
			case SLAYER: return "💀";
			case FARMING: return "🌱";
			case RUNECRAFT: return "🔮";
			case HUNTER: return "🏹";
			case CONSTRUCTION: return "🏠";
			default: return "⭐";
		}
	}
}
