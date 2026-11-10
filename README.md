⚡ Vortex Title

<p align="center">
  <img src="assets/vortex-title-banner.png" width="100%" alt="Vortex Title - Advanced Custom Title System"/>
</p><p align="center">
  <b>ADVANCED PERMANENT CUSTOM TITLE SYSTEM</b>
  <br/>
  <i>Your Titles. Your Identity. Your Collection.</i>
</p><p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-9B5CFF?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Minecraft-26.3-3C8527?style=for-the-badge&logo=minecraft&logoColor=white"/>
  <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-blue?style=for-the-badge"/>
</p><p align="center">
  <b>Developed with ❤️ by Vortex Teams</b>
</p>---

📖 About Vortex Title

Vortex Title is an advanced Minecraft title management plugin designed to bring a customizable and permanent title collection experience to modern Minecraft servers.

Unlike traditional title systems that rely on a single predefined prefix, Vortex Title allows administrators to create and distribute completely customized titles directly to players.

Every granted title becomes part of the player's personal collection. Players can browse their collection through an interactive inventory GUI and switch between previously obtained titles whenever they want.

Whether you're running a Survival, RPG, Economy, SkyBlock, or SMP server, Vortex Title provides a flexible foundation for building your own title progression and reward system.

🎯 What Makes It Different?

- Permanent Ownership — Granted titles are stored permanently.
- Custom Title Creation — Staff can create titles without manually registering each title in configuration files.
- Personal Collections — Players can own multiple titles simultaneously.
- Flexible Selection — Players can switch between titles they already own.
- Advanced Formatting — Create visually unique titles using MiniMessage, HEX, RGB, Gradient, and Legacy formatting.
- Persistent Storage — Player collections and active selections are stored using SQLite.

---

✨ Core Features

🎖️ Permanent Title Collection

Players can collect multiple titles without losing previously obtained ones.

Each title is individually associated with its owner and stored in the database.

👑 Staff Title Management

Administrators can grant custom titles directly to players using commands.

There is no need to manually create every title in a configuration file.

🖥️ Interactive Title GUI

Players can access their personal title collection through an inventory-based interface.

The GUI provides:

- View owned titles.
- Identify the currently selected title.
- Select another owned title.
- Navigate between collection pages.
- Reset the currently selected title without deleting ownership.

🎨 Advanced Text Formatting

Create visually impressive titles using supported Minecraft text formatting systems.

Supported input formats include:

- Adventure MiniMessage
- HEX Colors
- RGB Colors
- Gradient
- Rainbow
- Legacy Color Codes
- Formatting Tags

💾 SQLite Data Storage

Player collections are stored persistently in an SQLite database.

Title ownership is retained across player disconnects and server restarts.

🔗 PlaceholderAPI Integration

Retrieve active titles and collection information through PlaceholderAPI.

Use the provided placeholders in compatible plugins.

⚙️ Configuration Management

Customize the plugin's prefix, messages, GUI appearance, collection limits, and selection settings.

🔄 Live Configuration Reload

Reload configuration files without requiring a complete server restart.

---

🎨 Advanced Formatting Showcase

Vortex Title supports several text formatting styles to help administrators create unique and visually appealing titles.

🌈 Gradient

<gradient:#00FFFF:#0000FF>WARRIOR</gradient>

💎 HEX Color

<#00FFFF>WARRIOR

🎨 RGB Color

&#00FFFFWARRIOR

🌈 RGBirdflop-Compatible Input

&x&0&0&F&F&F&F VORTEX

✨ Rainbow

<rainbow>MYTHICAL</rainbow>

💪 Bold

<bold>LEGEND</bold>

📐 Additional MiniMessage Formatting

<italic>ELITE</italic>
<underlined>SUPREME</underlined>
<strikethrough>EXCLUSIVE</strikethrough>

Formatting support is handled through the Adventure MiniMessage system and the plugin's color conversion layer.

«Note: The final appearance of RGB and advanced formatting depends on the compatibility of the plugin displaying the PlaceholderAPI result.»

---

💻 Command Documentation

👤 Player Commands

📂 Open Title Collection

/title

Opens the player's personal title collection GUI.

🖥️ Open Title GUI

/title gui

Opens the title selection interface.

📚 View Owned Titles

/title owned

Displays all titles currently owned by the player, including their IDs.

🎖️ Select an Owned Title

/title select <id>

Sets an owned title as the player's active title.

🔄 Reset Active Title

/title reset

Removes the currently selected title without deleting the player's collection.

👑 Staff Commands

🎁 Grant a Custom Title

/title give <player> <title>

Grants a permanent custom title to a player.

Example:

/title give Player1 <gradient:#00FFFF:#0000FF>WARRIOR</gradient>

🗑️ Remove a Specific Title

/title take <player> <id>

Removes a specific title from the player's collection.

🚫 Remove All Titles

/title take <player> all

Removes every collected title belonging to the specified player.

📋 View Player Collection

/title list <player>

Displays the title collection of another player.

🔃 Reload Configuration

/title reload

Reloads Vortex Title configuration files.

---

🔐 Permission System

👤 Player Permissions

Permission| Description| Default
"vortextitle.use"| Access the title collection| "true"

👑 Staff Permissions

Permission| Description| Default
"vortextitle.admin"| Full administration access| "OP"
"vortextitle.give"| Grant custom titles| "OP"
"vortextitle.take"| Remove player titles| "OP"
"vortextitle.reload"| Reload plugin configuration| "OP"

---

🔗 PlaceholderAPI Integration

Vortex Title provides PlaceholderAPI expansion support for retrieving title information.

🏷️ Available Placeholders

🎖️ Active Title

%vortextitle_title%

Returns the player's currently selected title.

🆔 Active Title ID

%vortextitle_title_id%

Returns the ID of the currently selected title.

📚 Collection Count

%vortextitle_count%

Returns the total number of titles owned by the player.

🔍 Check Title Ownership

%vortextitle_has_title_<id>%

Checks whether the player owns a specific title.

Returns:

true

or

false

👥 Another Player's Active Title

%vortextitle_player_<player>%

Retrieves another player's currently selected title.

🔧 Integration Possibilities

Use Vortex Title placeholders in compatible:

- 💬 Chat formatting plugins
- 📊 Scoreboard plugins
- 📑 TAB plugins
- 🖥️ GUI plugins
- 🔌 Other PlaceholderAPI integrations

PlaceholderAPI is optional and can be installed separately.

---

⚙️ Configuration

Vortex Title provides configurable settings to adapt the plugin to your server's requirements.

📄 Main Configuration

settings:
  prefix: "<gradient:#00E5FF:#5865F2><bold>VortexTitle</bold></gradient>"

  max-titles-per-player: 0

  allow-select-command: true

gui:
  title: "<gradient:#00E5FF:#5865F2><bold>Title Collection</bold></gradient>"

  rows: 6

🔧 Configuration Options

Setting| Description
"prefix"| Custom plugin message prefix
"max-titles-per-player"| Maximum number of owned titles
"allow-select-command"| Enables direct title selection commands
"gui.title"| Custom inventory title
"gui.rows"| Inventory size

💡 Unlimited Collection

Set the following value to "0" to allow unlimited title ownership:

max-titles-per-player: 0

---

💾 Database & Data Management

Vortex Title uses SQLite for persistent player data storage.

📂 Database Location

plugins/VortexTitle/titles.db

📋 Stored Information

- Player UUID
- Owned title IDs
- Original title formatting
- Title creation timestamps
- Currently selected title ID

🔒 Data Persistence

Player title collections are stored independently of online player sessions.

This allows players to retain their collected titles after disconnecting or restarting the server.

Recommendation: Back up "titles.db" regularly to protect player collection data.

---

📦 Installation Guide

📋 Requirements

Component| Requirement
Java| 25
Minecraft| 26.3
Server Software| Paper / Purpur
PlaceholderAPI| Optional
Storage| SQLite

🚀 Installation Steps

1. Download or build the Vortex Title JAR.
2. Place the JAR inside your server's "plugins" directory.
3. Install PlaceholderAPI if you need placeholder integration.
4. Restart your Minecraft server.
5. Open the generated Vortex Title configuration folder.
6. Customize the configuration according to your server's needs.

📁 Plugin Directory

plugins/
└── VortexTitle/
    ├── config.yml
    └── titles.db

---

🛠️ Building From Source

Want to compile Vortex Title yourself?

📋 Build Requirements

- Java Development Kit 25
- Gradle 9.x
- Internet connection for dependency resolution

1. Clone Repository

git clone https://github.com/YOUR_USERNAME/VortexTitle.git

2. Enter Project Directory

cd VortexTitle

3. Build Plugin

gradle build

4. Locate Compiled JAR

build/libs/VortexTitle-1.0.0.jar

The generated JAR is ready to be installed on a compatible server after successful compilation and testing.

---

🗺️ Development Roadmap

Feature| Status
Permanent Title Storage| ✅
Custom Title Management| ✅
Player Title Collection| ✅
Interactive Inventory GUI| ✅
MiniMessage Formatting| ✅
HEX & RGB Formatting| ✅
PlaceholderAPI Integration| ✅
SQLite Database| ✅
Staff Management Commands| ✅
Configuration Reload| ✅
Advanced GUI Customization| 🔜
Title Categories| 🔜
Additional Integrations| 🔜

---

🐛 Bug Reports & Suggestions

Found an issue or have an idea to improve Vortex Title?

We welcome feedback, feature requests, and bug reports.

When submitting a report, please include:

- Minecraft server version
- Java version
- Vortex Title version
- Relevant console logs
- Steps to reproduce the issue

Please use the GitHub Issues section to report problems and suggest improvements.

---

🤝 Credits & Development

<p align="center">
  <img src="https://img.shields.io/badge/Developed%20by-Vortex%20Teams-8B5CF6?style=for-the-badge"/>
</p><p align="center">
  <b>VORTEX TEAMS</b>
  <br/>
  Minecraft Plugin Development
  <br/>
  Custom Systems • Server Solutions • Plugin Engineering
</p>---

<p align="center">
  <b>⚡ VORTEX TITLE</b>
  <br/>
  <i>Your Titles. Your Identity. Your Collection.</i>
  <br/><br/>
  © Vortex Teams
</p>
