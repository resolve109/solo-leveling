package com.sololeveling;

import com.google.inject.Provides;
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
import java.util.Map;

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
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Solo Leveling plugin stopped! Until next time, Hunter...");
		overlayManager.remove(overlay);
		previousExperience.clear();
		previousLevels.clear();
		lastExperienceGain.clear();
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

	@Provides
	SoloLevelingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SoloLevelingConfig.class);
	}
}
