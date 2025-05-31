# Solo Leveling Plugin for RuneLite

An anime-themed Old School RuneScape plugin that tracks your skills and progress with a Solo Leveling aesthetic, featuring hunter ranks, power levels, and anime-style notifications.

## Features

- 🗡️ **Hunter Rank System** - Progress from E-Rank to S-Rank Hunter based on your total level
- ⚔️ **Solo Leveling Themed Messages** - Anime-style notifications for level ups and XP gains  
- 💫 **Power Level Calculation** - Track your overall "power level" based on experience and levels
- 🎯 **Recent Activity Tracking** - See your latest skill gains in a beautiful overlay
- 🎨 **Customizable Theme** - Adjust colors and appearance to your preference
- 📊 **Progress Visualization** - Visual overlay showing your hunter status and goals

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

## Power Level Calculation

Your power level combines:

- Total experience earned (divided by 1000)
- Total skill levels (multiplied by 10)

This creates a unique "power level" that represents your overall OSRS progression in an anime-style format.

## Development

### Project Structure

```
solo-leveling-plugin/
├── src/
│   ├── main/
│   │   ├── java/com/sololeveling/
│   │   │   ├── SoloLevelingPlugin.java      # Main plugin class
│   │   │   ├── SoloLevelingConfig.java      # Configuration interface  
│   │   │   └── SoloLevelingOverlay.java     # Visual overlay component
│   │   └── resources/                       # Plugin resources
│   └── test/
│       └── java/com/sololeveling/
│           └── SoloLevelingPluginTest.java  # Test runner
├── build.gradle                             # Gradle build configuration
├── runelite-plugin.properties              # Plugin metadata
└── README.md                               # This file
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
