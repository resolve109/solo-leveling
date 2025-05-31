# Solo Leveling Plugin for RuneLite
![Solo Leveling Plugin Banner](https://example.com/banner.jpg)

> **Arise, Shadow Monarch!** 🗡️⚔️  
>

An anime-themed Old School RuneScape plugin that tracks your skills and progress with a Solo Leveling aesthetic, featuring hunter ranks, power levels, and anime-style notifications.

## Features

- 🗡️ **Hunter Rank System** - Progress from E-Rank to S-Rank Hunter based on your total level
- ⚔️ **Solo Leveling Themed Messages** - Anime-style notifications for level ups and XP gains  
- 💫 **Power Level Calculation** - Track your overall "power level" based on experience and levels
- 🎯 **Recent Activity Tracking** - See your latest skill gains in a beautiful overlay
- 🎨 **Customizable Theme** - Adjust colors and appearance to your preference
- 📊 **Progress Visualization** - Visual overlay showing your hunter status and goals
- 📋 **Task System** - Complete tasks from OSRS leagues, quests, and custom challenges

## Installation

### For Plugin Hub (Coming Soon)

Once this plugin is submitted to the RuneLite Plugin Hub:

1. Open RuneLite
2. Go to Configuration → Plugin Hub
3. Search for "Solo Leveling"
4. Click Install

### Building from Source

#### Prerequisites

- Java 11 or higher
- Git

#### Build Steps

1. **Clone this repository:**

   ```cmd
   git clone https://github.com/yourusername/solo-leveling-plugin.git
   cd solo-leveling-plugin
   ```

2. **Build the plugin:**

   ```cmd
   gradlew build
   ```

3. **Test the plugin locally:**

   ```cmd
   gradlew shadowJar
   ```

   Then run the generated JAR to test with RuneLite.

## Configuration

The plugin offers extensive customization options:

### Messages

- **Login Messages** - Welcome back messages with hunter theme
- **Level Up Notifications** - Anime-style level up celebrations
- **XP Gain Alerts** - Optional notifications for experience gains
- **Minimum XP Threshold** - Only show XP gains above a certain amount

### Overlay

- **Position** - Choose where to display the overlay (corners of screen)
- **Total Level Display** - Show your combined skill levels
- **Total Experience** - Display your total XP earned
- **Recent Activity** - Track recent skill gains with timestamps
- **Duration Settings** - Control how long recent activities are shown

### Tasks

- **Show Tasks** - Toggle display of tasks in the overlay
- **Max Tasks Shown** - Control how many tasks are visible at once
- **Filter by Source** - Only show tasks from specific sources (leagues, quests, custom)
- **Task Categories** - Enable/disable specific task types
- **Completion Messages** - Show notifications when tasks are completed

### Theme Customization

- **Primary Color** - Main theme color (default: Solo Leveling purple)
- **Secondary Color** - Accent color for highlights (default: gold)
- **Background** - Overlay background with transparency
- **Text Color** - Customize text appearance

## Hunter Rank System

Your hunter rank is determined by your total skill level:

- 🟤 **E-Rank Hunter** - Below 1000 total level
- ⚪ **D-Rank Hunter** - 1000-1499 total level  
- 🔸 **C-Rank Hunter** - 1500-1749 total level
- 🔷 **B-Rank Hunter** - 1750-1999 total level
- 💎 **A-Rank Hunter** - 2000-2276 total level
- 🌟 **S-Rank Hunter** - 2277 total level (maxed)

## Task System

The plugin includes a comprehensive task system with:

- **League Tasks** - Challenges from OSRS league events like Raging Echoes and Trailblazer Reloaded
- **Quest Tasks** - Complete quests throughout Gielinor
- **Custom Tasks** - Add your own personalized challenges

Tasks are categorized by difficulty (Easy, Medium, Hard, Elite, Master) and type (Combat, Skilling, Exploration, etc.).

## How to Add Custom Tasks

You can add your own tasks to the plugin by editing the `TaskManager.java` file. Here's how:

### 1. Add Tasks Directly in the Code

Add new tasks in the `loadDefaultTasks()` method or create a new method to load your custom tasks.

```java
private void loadCustomTasks() {
    allTasks.add(Task.builder()
        .id("custom_1")  // Unique ID for the task
        .name("My Custom Task")
        .description("Description of what to accomplish")
        .difficulty(TaskDifficulty.MEDIUM)
        .category(TaskCategory.COMBAT)
        .source(TaskSource.CUSTOM)
        .experienceReward(5000)
        .pointsReward(50)
        .completed(false)
        .visible(true)
        .build());
}
```

Then call this method from `loadDefaultTasks()`:

```java
private void loadDefaultTasks() {
    // Add league tasks
    loadLeagueTasks();
    
    // Add quest tasks
    loadQuestTasks();
    
    // Add custom tasks
    loadCustomTasks();
}
```

### 2. Load Tasks from a Configuration File (Advanced)

For more flexibility, you can implement loading tasks from a JSON file:

1. Create a JSON file with your tasks (e.g., `custom-tasks.json`)
2. Add a method to load this file in `TaskManager.java`
3. Parse the JSON and create Task objects

## Plugin Maintenance

### Adding New League Tasks

When new OSRS leagues are released:

1. Create a new enum value in `TaskSource.java` (if needed)
2. Add a new method in `TaskManager.java` to load these tasks (e.g., `loadNewLeagueTasks()`)
3. Call this method from `loadDefaultTasks()`

### Adding Quest Tasks

To add more quest tasks:

1. Open `TaskManager.java`
2. Locate the `loadQuestTasks()` method
3. Add new quests following the existing pattern

### Updating Task Status

The plugin currently doesn't automatically detect task completion. To implement this:

1. Create event handlers in `SoloLevelingPlugin.java` for relevant game events
2. Check if the event completes any tasks
3. Call `completeTask(taskId)` when a task is completed

### Bug Fixing

If you encounter any issues:

1. Check the RuneLite logs for error messages
2. Verify that all required dependencies are correctly configured
3. Test any changes with a local RuneLite installation before distributing

## Development

### Project Structure

```
solo-leveling-plugin/
├── src/
│   ├── main/
│   │   ├── java/com/sololeveling/
│   │   │   ├── SoloLevelingPlugin.java      # Main plugin class
│   │   │   ├── SoloLevelingConfig.java      # Configuration interface  
│   │   │   ├── SoloLevelingOverlay.java     # Visual overlay component
│   │   │   └── task/                        # Task system
│   │   │       ├── Task.java                # Task representation
│   │   │       ├── TaskCategory.java        # Task categories
│   │   │       ├── TaskDifficulty.java      # Task difficulty levels
│   │   │       ├── TaskManager.java         # Task management
│   │   │       └── TaskSource.java          # Task sources
│   │   └── resources/                       # Plugin resources
│   └── test/
│       └── java/com/sololeveling/
│           └── SoloLevelingPluginTest.java  # Test runner
├── build.gradle                             # Gradle build configuration
├── runelite-plugin.properties               # Plugin metadata
└── README.md                                # This file
```

### Building and Testing

1. **Clean build:**

   ```cmd
   gradlew clean build
   ```

2. **Run tests:**

   ```cmd
   gradlew test
   ```

3. **Generate shadow JAR for testing:**

   ```cmd
   gradlew shadowJar
   ```

4. **Run with RuneLite for development:**

   ```cmd
   java -jar build/libs/solo-leveling-plugin-1.0-SNAPSHOT-all.jar
   ```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Test thoroughly with the RuneLite client
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Plugin Hub Submission Process

To submit this plugin to the RuneLite Plugin Hub:

1. **Prepare Repository:**
   - Ensure code is clean and well-documented
   - Add appropriate LICENSE file (BSD 2-Clause recommended)
   - Test thoroughly with RuneLite

2. **Fork Plugin Hub:**

   ```cmd
   git clone https://github.com/yourusername/plugin-hub.git
   cd plugin-hub
   ```

3. **Create Plugin Entry:**

   ```cmd
   git checkout -b solo-leveling-plugin
   echo repository=https://github.com/yourusername/solo-leveling-plugin.git > plugins/solo-leveling-plugin
   echo commit=FULL_40_CHAR_COMMIT_HASH >> plugins/solo-leveling-plugin
   ```

4. **Submit Pull Request:**
   - Push changes to your fork
   - Create PR to main plugin-hub repository
   - Wait for review and approval

## Troubleshooting

### Build Issues

- Ensure Java 11+ is installed and in PATH
- Check that RuneLite dependencies are accessible
- Try `gradlew clean build` for a fresh build

### Runtime Issues  

- Verify plugin is enabled in RuneLite configuration
- Check RuneLite logs for error messages
- Ensure your RuneLite client is up to date

## Inspiration

This plugin is inspired by the Solo Leveling manhwa/anime series by Chugong. The hunter ranking system, power level concepts, and aesthetic choices all draw from this amazing series.

## License

This project is licensed under the BSD 2-Clause License - see the LICENSE file for details.

## Acknowledgments

- **RuneLite** - For providing an excellent plugin framework
- **Solo Leveling** - For the amazing story that inspired this plugin
- **OSRS Community** - For the continued support and feedback

---

**Rise, Shadow Monarch! Your journey through Gielinor awaits...** 🗡️⚔️

### Version History

- **v1.0.0** - Initial release with hunter ranks, overlay, and themed messages
- **Future Updates** - More Solo Leveling features, additional animations, sound effects
