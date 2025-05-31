# Solo Leveling Plugin for RuneLite

An anime-themed Old School RuneScape plugin that tracks league tasks with a Solo Leveling aesthetic.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Building from Source](#building-from-source)
- [Development Mode](#development-mode)
- [Troubleshooting](#troubleshooting)
- [Features](#features)
- [Contributing](#contributing)

## Prerequisites

### Java 11+
The plugin requires Java 11 or higher to compile and run.

**Windows:**
1. Download Java 11+ from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [AdoptOpenJDK](https://adoptopenjdk.net/)
2. Install the JDK
3. Verify installation by opening Command Prompt and running:
   ```cmd
   java -version
   ```
   You should see Java 11 or higher in the output.

### Maven
Maven is required for building the plugin.

**Windows:**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to a directory (e.g., `C:\Program Files\Apache\Maven`)
3. Add Maven to your PATH environment variable:
   - Open System Properties → Advanced → Environment Variables
   - Add `C:\Program Files\Apache\Maven\bin` to your PATH
4. Verify installation:
   ```cmd
   mvn -version
   ```

## Installation

### Option 1: Quick Build and Install (Recommended)

1. Clone or download this repository
2. Navigate to the plugin directory
3. Run the build script:
   ```cmd
   build-and-test.bat
   ```
4. When prompted, choose 'y' to install the plugin to RuneLite
5. Restart RuneLite to load the plugin

### Option 2: Manual Build

1. Open Command Prompt in the plugin directory
2. Clean and compile:
   ```cmd
   mvn clean compile
   ```
3. Package the plugin:
   ```cmd
   mvn package
   ```
4. Copy the JAR from `target/solo-leveling-plugin-1.0.0.jar` to your RuneLite plugins directory:
   ```
   C:\Users\[YourUsername]\AppData\Local\RuneLite\plugin\
   ```

## Building from Source

The build script (`build-and-test.bat`) performs the following steps:

### 1. Environment Validation
- ✅ Checks Maven installation
- ✅ Verifies Java 11+ is available
- ✅ Validates project structure

### 2. Build Process
- 🧹 Cleans previous build artifacts
- 🔨 Compiles the plugin source code
- 📦 Packages into a JAR file

### 3. Quality Assurance
- 🔍 Validates JAR contains plugin class
- 📋 Verifies resources are included
- 🧪 Tests JSON structure integrity

### 4. Installation
- 📁 Creates RuneLite plugins directory if needed
- 📋 Copies plugin JAR to RuneLite
- ✅ Confirms successful installation

### Manual Build Commands

If you prefer to build manually:

```cmd
# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Run tests (if available)
mvn test

# Package into JAR
mvn package

# Generate documentation
mvn javadoc:javadoc
```

## Development Mode

The build script includes a development mode with additional tools:

### Available Commands:
1. **Dependency Analysis** - View project dependencies
2. **Generate Documentation** - Create JavaDoc documentation
3. **Code Quality Checks** - Scan for common issues
4. **Create Plugin Descriptor** - Generate plugin metadata
5. **Test with RuneLite Client** - Testing guidelines

### Running Development Mode:
```cmd
build-and-test.bat
# When prompted, choose 'y' for development mode
```

### Development Features:

#### Dependency Analysis
```cmd
mvn dependency:tree
```
Shows all project dependencies and their relationships.

#### Documentation Generation
```cmd
mvn javadoc:javadoc
```
Generates API documentation in `target/site/apidocs/`

#### Code Quality Checks
- Scans for `System.out.print` statements
- Finds TODO/FIXME comments
- Validates project structure

## Troubleshooting

### Environment Setup Failures

#### Environment lacks the RuneLite libraries

**This is a common issue when developing RuneLite plugins.** The plugin needs access to RuneLite's API and client libraries to compile.

**Step-by-Step Resolution:**

1. **Clone the RuneLite Repository:**
   ```cmd
   # Navigate to your development directory
   cd f:\github\
   
   # Clone RuneLite source
   git clone https://github.com/runelite/runelite.git
   cd runelite
   ```

2. **Build RuneLite Locally:**
   ```cmd
   # Build RuneLite to install dependencies to local Maven repository
   mvn clean install -DskipTests
   ```
   This installs RuneLite artifacts to your local Maven repository (`~/.m2/repository`).

3. **Verify RuneLite Dependencies:**
   ```cmd
   # Check if RuneLite artifacts are in local Maven repo
   dir "%USERPROFILE%\.m2\repository\net\runelite"
   ```

4. **Update Plugin POM (if needed):**
   Ensure your `pom.xml` has the correct RuneLite version and dependencies:
   ```xml
   <properties>
       <runelite.version>1.10.26</runelite.version>
   </properties>
   
   <dependencies>
       <dependency>
           <groupId>net.runelite</groupId>
           <artifactId>client</artifactId>
           <version>${runelite.version}</version>
           <scope>provided</scope>
       </dependency>
   </dependencies>
   ```

5. **Alternative: Use RuneLite Parent POM:**
   If available, inherit from RuneLite's parent POM:
   ```xml
   <parent>
       <groupId>net.runelite</groupId>
       <artifactId>runelite-parent</artifactId>
       <version>1.10.26</version>
   </parent>
   ```

#### RuneLite Development Environment Setup

For comprehensive plugin development, set up the full RuneLite development environment:

1. **Project Structure:**
   ```
   f:\github\
   ├── runelite\                    # RuneLite source
   │   ├── runelite-api\
   │   ├── runelite-client\
   │   └── pom.xml
   └── osrs-plugins\
       └── solo-leveling-plugin\    # Your plugin
   ```

2. **IDE Configuration (IntelliJ IDEA):**
   - Import RuneLite as a Maven project
   - Import your plugin as a separate Maven project
   - Configure module dependencies to reference RuneLite modules

3. **Development Workflow:**
   ```cmd
   # Build RuneLite first
   cd f:\github\runelite
   mvn clean install -DskipTests
   
   # Then build your plugin
   cd f:\github\osrs-plugins\solo-leveling-plugin
   mvn clean compile package
   ```

#### Using RuneLite Plugin Archetype

For new plugins, use the official RuneLite plugin archetype:

```cmd
mvn archetype:generate \
  -DarchetypeGroupId=net.runelite \
  -DarchetypeArtifactId=runelite-plugin-archetype \
  -DarchetypeVersion=1.10.26 \
  -DgroupId=com.example \
  -DartifactId=my-plugin \
  -DinteractiveMode=false
```

#### Dependency Resolution Issues

If you're still getting dependency errors:

1. **Clear Maven Cache:**
   ```cmd
   rmdir /s /q "%USERPROFILE%\.m2\repository\net\runelite"
   mvn dependency:purge-local-repository
   ```

2. **Force Update Dependencies:**
   ```cmd
   mvn clean compile -U
   ```

3. **Check Maven Settings:**
   Ensure your `~/.m2/settings.xml` doesn't block required repositories.

4. **Manual Dependency Installation:**
   If automatic resolution fails, manually install RuneLite JARs:
   ```cmd
   # Download RuneLite client JAR
   # Install to local repository
   mvn install:install-file \
     -Dfile=runelite-client-1.10.26.jar \
     -DgroupId=net.runelite \
     -DartifactId=client \
     -Dversion=1.10.26 \
     -Dpackaging=jar
   ```

#### RuneLite API Documentation

Reference the RuneLite API documentation for development:
- **Source:** https://github.com/runelite/runelite
- **API Docs:** Available in `runelite-api/src/main/java/net/runelite/api`
- **Wiki:** https://github.com/runelite/runelite/wiki
- **Contributing:** https://github.com/runelite/runelite/blob/master/.github/CONTRIBUTING.md

#### Script Failed: Script exited with code 127

**Error Code 127** means "command not found" - this indicates that either Java or Maven is not properly installed or not accessible from the command line.

**Step-by-Step Resolution:**

1. **Check Java Installation:**
   ```cmd
   java -version
   ```
   If this fails:
   - Download and install Java 11+ from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptopenjdk.net/)
   - During installation, ensure "Add to PATH" is checked
   - Restart your command prompt/terminal
   - Try again

2. **Check Maven Installation:**
   ```cmd
   mvn -version
   ```
   If this fails:
   - Download Maven from https://maven.apache.org/download.cgi
   - Extract to `C:\Program Files\Apache\Maven\` (Windows)
   - Add to PATH manually:
     - Press `Win + R`, type `sysdm.cpl`, press Enter
     - Go to Advanced tab → Environment Variables
     - Under System Variables, find and select PATH → Edit
     - Add `C:\Program Files\Apache\Maven\bin`
     - Click OK on all dialogs
   - Restart command prompt
   - Try again

3. **Verify Environment Variables:**
   ```cmd
   echo %JAVA_HOME%
   echo %PATH%
   ```
   Ensure both show your Java and Maven installations.

4. **Alternative: Use Chocolatey (Windows):**
   ```cmd
   # Install Chocolatey first (run as Administrator)
   Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   
   # Then install Java and Maven
   choco install openjdk11 maven
   ```

5. **Manual Environment Setup (Windows):**
   ```cmd
   # Set JAVA_HOME
   setx JAVA_HOME "C:\Program Files\Java\jdk-11.0.x"
   
   # Add to PATH
   setx PATH "%PATH%;%JAVA_HOME%\bin;C:\Program Files\Apache\Maven\bin"
   ```

#### Script Failed: Permission Denied

**Solution:**
- Run Command Prompt as Administrator
- Or use PowerShell with execution policy:
  ```powershell
  Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
  ```

#### Script Failed: File Not Found

**Solution:**
- Ensure you're in the correct directory
- Verify `build-and-test.bat` exists
- Check file permissions

### Pre-Build Environment Check

Before running the build script, manually verify your environment:

```cmd
# Check current directory
cd /d "f:\github\osrs-plugins\solo-leveling-plugin"

# Verify Java
java -version
javac -version

# Verify Maven
mvn -version

# Check project files
dir pom.xml
dir src\main\java
dir src\main\resources\league_tasks.json
```

### Alternative Build Methods

If the batch script continues to fail, try these alternatives:

#### Method 1: PowerShell Script
Create `build.ps1`:
```powershell
# Check Java
if (!(Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java not found. Please install Java 11+"
    exit 1
}

# Check Maven
if (!(Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "Maven not found. Please install Maven"
    exit 1
}

# Build
mvn clean compile package
```

#### Method 2: Direct Maven Commands
```cmd
# Navigate to project directory
cd /d "f:\github\osrs-plugins\solo-leveling-plugin"

# Clean and build
mvn clean
mvn compile
mvn package

# Verify build
dir target\*.jar
```

#### Method 3: IDE Integration
- Import project into IntelliJ IDEA or Eclipse
- Use built-in Maven integration
- Run Maven goals: `clean compile package`

### Environment Validation Script

Create a simple validation script `check-env.bat`:
```bat
@echo off
echo Checking Java...
java -version || echo [ERROR] Java not found
echo.
echo Checking Maven...
mvn -version || echo [ERROR] Maven not found
echo.
echo Checking project structure...
if exist pom.xml (echo [OK] pom.xml found) else (echo [ERROR] pom.xml missing)
if exist src\main\java (echo [OK] Java source directory found) else (echo [ERROR] Java source missing)
if exist src\main\resources (echo [OK] Resources directory found) else (echo [ERROR] Resources missing)
pause
```

### Common Issues:

#### "Maven is not installed or not in PATH!"
**Solution:** 
- Download Maven from https://maven.apache.org/download.cgi
- Add Maven's `bin` directory to your PATH environment variable
- Restart Command Prompt and try again

#### "Java 11 or higher is required!"
**Solution:**
- Install Java 11+ from Oracle or AdoptOpenJDK
- Ensure `JAVA_HOME` points to the correct Java installation
- Verify with `java -version`

#### "pom.xml not found!"
**Solution:**
- Ensure you're running the script from the plugin root directory
- Verify the project structure is intact

#### "league_tasks.json not found!"
**Solution:**
- Check that `src/main/resources/league_tasks.json` exists
- Ensure the file contains valid JSON data

#### "Plugin JAR not found"
**Solution:**
- Run `mvn clean package` manually
- Check the `target` directory for build artifacts
- Verify Maven build completed successfully

#### "Failed to copy plugin to RuneLite directory!"
**Solution:**
- Ensure RuneLite is not running
- Check file permissions
- Manually copy the JAR to:
  ```
  C:\Users\[YourUsername]\AppData\Local\RuneLite\plugin\
  ```

### Build Verification:

After a successful build, verify:
- ✅ JAR file exists in `target/` directory
- ✅ JAR contains `SoloLevelingPlugin.class`
- ✅ JAR includes `league_tasks.json`
- ✅ JSON structure is valid

## Project Structure

```
solo-leveling-plugin/
├── src/
│   ├── main/
│   │   ├── java/          # Plugin source code
│   │   └── resources/     # Plugin resources
│   │       └── league_tasks.json
├── target/                # Build output
├── pom.xml               # Maven configuration
├── build-and-test.bat    # Build script
└── README.md             # This file
```

## Features

- 🎌 Solo Leveling anime-themed UI
- 📋 League task tracking
- 🏆 Progress visualization
- 🎮 RuneLite integration
- 📊 Statistics and analytics

## Next Steps After Installation

1. **Start RuneLite**
2. **Go to Configuration → Plugin Hub**
3. **Look for "Solo Leveling" in the plugin list**
4. **Enable the plugin**
5. **Enjoy your anime-themed league tracking!**

## Contributing

1. Fork the repository
2. Create a feature branch
3. Run the build script to test your changes
4. Submit a pull request

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify your Java and Maven installations
3. Run the build script in development mode for detailed diagnostics
4. Check the build logs for specific error messages

---

**Happy leveling, Hunter!** 🗡️⚔️