package com.sololeveling;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;

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
		// Simple power level calculation based on total XP and level
		long totalXp = plugin.getTotalExperience();
		int totalLevel = plugin.getTotalLevel();
		
		// Combine XP and levels for a "power level"
		return (int) ((totalXp / 1000) + (totalLevel * 10));
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
