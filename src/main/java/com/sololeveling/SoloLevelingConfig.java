package com.sololeveling;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("sololeveling")
public interface SoloLevelingConfig extends Config
{
	// === MESSAGE SETTINGS ===
	@ConfigSection(
		name = "Messages",
		description = "Configure Solo Leveling themed messages",
		position = 1
	)
	String messagesSection = "messages";

	@ConfigItem(
		keyName = "showLoginMessage",
		name = "Show Login Message",
		description = "Display a Solo Leveling themed welcome message when logging in",
		section = messagesSection,
		position = 1
	)
	default boolean showLoginMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showLevelUps",
		name = "Show Level Up Messages",
		description = "Display anime-themed messages when leveling up skills",
		section = messagesSection,
		position = 2
	)
	default boolean showLevelUps()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showXpGains",
		name = "Show XP Gain Messages",
		description = "Display messages for experience gains",
		section = messagesSection,
		position = 3
	)
	default boolean showXpGains()
	{
		return false;
	}

	@ConfigItem(
		keyName = "minimumXpToShow",
		name = "Minimum XP to Show",
		description = "Only show XP gain messages for gains above this amount",
		section = messagesSection,
		position = 4
	)
	@Range(min = 1, max = 10000)
	default int minimumXpToShow()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "showTaskCompletionMessages",
		name = "Show Task Completion Messages",
		description = "Display messages when tasks are completed",
		section = messagesSection,
		position = 5
	)
	default boolean showTaskCompletionMessages()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTaskRewards",
		name = "Show Task Rewards",
		description = "Display reward messages when completing tasks",
		section = messagesSection,
		position = 6
	)
	default boolean showTaskRewards()
	{
		return true;
	}

	// === OVERLAY SETTINGS ===
	@ConfigSection(
		name = "Overlay",
		description = "Configure the Solo Leveling overlay appearance",
		position = 2
	)
	String overlaySection = "overlay";

	@ConfigItem(
		keyName = "showOverlay",
		name = "Show Overlay",
		description = "Display the Solo Leveling overlay on screen",
		section = overlaySection,
		position = 1
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "overlayPosition",
		name = "Overlay Position",
		description = "Choose where to display the overlay",
		section = overlaySection,
		position = 2
	)
	default OverlayPosition overlayPosition()
	{
		return OverlayPosition.TOP_LEFT;
	}

	@ConfigItem(
		keyName = "showTotalLevel",
		name = "Show Total Level",
		description = "Display your total skill level in the overlay",
		section = overlaySection,
		position = 3
	)
	default boolean showTotalLevel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showTotalExperience",
		name = "Show Total Experience",
		description = "Display your total experience in the overlay",
		section = overlaySection,
		position = 4
	)
	default boolean showTotalExperience()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRecentXpGains",
		name = "Show Recent XP Gains",
		description = "Display recently gained experience in skills",
		section = overlaySection,
		position = 5
	)
	default boolean showRecentXpGains()
	{
		return true;
	}

	@ConfigItem(
		keyName = "recentXpDuration",
		name = "Recent XP Duration (seconds)",
		description = "How long to show recent XP gains in the overlay",
		section = overlaySection,
		position = 6
	)
	@Range(min = 5, max = 300)
	default int recentXpDuration()
	{
		return 30;
	}

	// === THEME SETTINGS ===
	@ConfigSection(
		name = "Theme",
		description = "Customize the Solo Leveling theme colors",
		position = 3
	)
	String themeSection = "theme";

	@ConfigItem(
		keyName = "primaryColor",
		name = "Primary Color",
		description = "Main color for the Solo Leveling theme",
		section = themeSection,
		position = 1
	)
	default Color primaryColor()
	{
		return new Color(138, 43, 226); // Blue Violet - Solo Leveling purple
	}

	@ConfigItem(
		keyName = "secondaryColor",
		name = "Secondary Color",
		description = "Secondary color for text and accents",
		section = themeSection,
		position = 2
	)
	default Color secondaryColor()
	{
		return new Color(255, 215, 0); // Gold
	}

	@ConfigItem(
		keyName = "backgroundColor",
		name = "Background Color",
		description = "Background color for overlay elements",
		section = themeSection,
		position = 3
	)
	default Color backgroundColor()
	{
		return new Color(0, 0, 0, 180); // Semi-transparent black
	}

	@ConfigItem(
		keyName = "textColor",
		name = "Text Color",
		description = "Color for overlay text",
		section = themeSection,
		position = 4
	)
	default Color textColor()
	{
		return Color.WHITE;
	}

	// === TASK SETTINGS ===
	@ConfigSection(
		name = "Tasks",
		description = "Configure task display and tracking",
		position = 4
	)
	String tasksSection = "tasks";

	@ConfigItem(
		keyName = "showTasks",
		name = "Show Tasks",
		description = "Display tasks in the overlay",
		section = tasksSection,
		position = 1
	)
	default boolean showTasks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "maxTasksShown",
		name = "Max Tasks Shown",
		description = "Maximum number of tasks to show in the overlay",
		section = tasksSection,
		position = 2
	)
	@Range(min = 1, max = 10)
	default int maxTasksShown()
	{
		return 5;
	}

	@ConfigItem(
		keyName = "filterTasksBySource",
		name = "Filter Tasks By Source",
		description = "Only show tasks from selected sources",
		section = tasksSection,
		position = 3
	)
	default boolean filterTasksBySource()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showLeagueTasks",
		name = "Show League Tasks",
		description = "Display tasks from OSRS leagues",
		section = tasksSection,
		position = 4
	)
	default boolean showLeagueTasks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showQuestTasks",
		name = "Show Quest Tasks",
		description = "Display tasks from OSRS quests",
		section = tasksSection,
		position = 5
	)
	default boolean showQuestTasks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showCustomTasks",
		name = "Show Custom Tasks",
		description = "Display custom tasks",
		section = tasksSection,
		position = 6
	)
	default boolean showCustomTasks()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showRandomTasks",
		name = "Show Random Tasks",
		description = "Display randomly generated tasks",
		section = tasksSection,
		position = 7
	)
	default boolean showRandomTasks()
	{
		return true;
	}

	// === ADVANCED SETTINGS ===
	@ConfigSection(
		name = "Advanced",
		description = "Advanced Solo Leveling plugin settings",
		position = 5
	)
	String advancedSection = "advanced";

	@ConfigItem(
		keyName = "hunterTitle",
		name = "Hunter Title",
		description = "Your custom hunter title (leave empty for 'Shadow Monarch')",
		section = advancedSection,
		position = 1
	)
	default String hunterTitle()
	{
		return "Shadow Monarch";
	}

	@ConfigItem(
		keyName = "useCustomRank",
		name = "Use Custom Rank",
		description = "Display a custom rank based on total level",
		section = advancedSection,
		position = 2
	)
	default boolean useCustomRank()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableSoundEffects",
		name = "Enable Sound Effects",
		description = "Play sound effects for level ups (if available)",
		section = advancedSection,
		position = 3
	)
	default boolean enableSoundEffects()
	{
		return false;
	}

	enum OverlayPosition
	{
		TOP_LEFT("Top Left"),
		TOP_RIGHT("Top Right"),
		BOTTOM_LEFT("Bottom Left"),
		BOTTOM_RIGHT("Bottom Right");

		private final String name;

		OverlayPosition(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
