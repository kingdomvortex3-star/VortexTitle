⚡ VortexTitle

<p align="center">
  <img src="https://img.shields.io/badge/VORTEX-NETWORK-5865F2?style=for-the-badge&logo=minecraft&logoColor=white" />
  <img src="https://img.shields.io/badge/Version-1.0.0-00C853?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/Minecraft-26.3-3C8527?style=for-the-badge&logo=minecraft" />
</p><p align="center">
  <b>Advanced Permanent Custom Title System</b>
  <br/>
  A powerful title collection system for modern Minecraft servers.
  <br/><br/>
  <i>Collect. Customize. Stand Out.</i>
</p>---

📖・Introduction

VortexTitle is an advanced and customizable Minecraft title plugin developed by Vortex Network.

Designed to provide a permanent title collection system, allowing staff to grant custom titles and players to collect, manage, and switch between their owned titles.

Every title is permanently stored in SQLite, ensuring that player collections remain safe even after disconnecting or restarting the server.

✨・Main Features

- 🎖️ Permanent Titles — Titles are permanently saved to each player's collection.
- 📝 Custom Titles — Staff can create titles with completely custom text.
- 📚 Title Collection — Players can collect multiple titles.
- 🔄 Title Switching — Freely switch between previously collected titles.
- 🎨 Advanced Formatting — MiniMessage, HEX, RGB, Legacy, Gradient, and Rainbow.
- 🖥️ Interactive GUI — Beautiful inventory-based title selection.
- 🔗 PlaceholderAPI — Integrate active titles with supported plugins.
- 💾 SQLite Storage — Persistent and reliable player data.
- 🛡️ Duplicate Protection — Prevents duplicate title ownership.
- ⚙️ Custom Configuration — Customize messages and GUI settings.
- 🔃 Reload System — Reload configurations without restarting.
- 👑 Staff Management — Complete title granting and removal commands.

🎨・Advanced Color Support

VortexTitle supports various Minecraft formatting systems, including RGBirdflop-compatible formats.

🌈 Gradient

<gradient:#00FFFF:#0000FF>WARRIOR</gradient>

💎 HEX

<#00FFFF>WARRIOR

🎨 RGB

&#00FFFFWARRIOR

🌈 RGBirdflop

&x&0&0&F&F&F&F VORTEX

✨ Rainbow

<rainbow>MYTHICAL</rainbow>

🟡 Legacy

&6LEGEND

💪 Bold

<bold>SUPREME</bold>

📐 Additional Formatting

- Italic
- Underline
- Strikethrough
- Obfuscated
- Custom HEX colors
- MiniMessage styling

📦・Installation

Requirements

- ☕ Java 25
- ⚙️ Paper / Purpur 26.3
- 🔌 PlaceholderAPI (Optional)

Installation Steps

1. 📥 Download the VortexTitle JAR.
2. 📂 Place it inside your server's "plugins" folder.
3. 🔗 Install PlaceholderAPI if required.
4. 🔄 Restart your server.
5. ⚙️ Configure VortexTitle to your preferences.

Plugin Directory

plugins/
└── VortexTitle/
    ├── config.yml
    └── titles.db

💻・Commands

👤 Player Commands

📂 Open Title Collection

/title

🖥️ Open Title GUI

/title gui

📚 View Owned Titles

/title owned

🎖️ Select an Owned Title

/title select <id>

🔄 Reset Active Title

/title reset

👑 Staff Commands

🎁 Give a Custom Title

/title give <player> <title>

Example:

/title give Player1 <gradient:#00FFFF:#0000FF>WARRIOR</gradient>

🗑️ Remove a Specific Title

/title take <player> <id>

🚫 Remove All Player Titles

/title take <player> all

📋 View Player Collection

/title list <player>

🔃 Reload Configuration

/title reload

🔐・Permissions

👤 Player Permissions

Permission| Description| Default
"vortextitle.use"| Access title collection| "true"

👑 Staff Permissions

Permission| Description| Default
"vortextitle.admin"| Full administration access| "OP"
"vortextitle.give"| Grant custom titles| "OP"
"vortextitle.take"| Remove player titles| "OP"
"vortextitle.reload"| Reload configuration| "OP"

🔗・PlaceholderAPI Integration

VortexTitle provides PlaceholderAPI integration for displaying player titles across compatible plugins.

🏷️ Available Placeholders

🎖️ Active Title

%vortextitle_title%

🆔 Active Title ID

%vortextitle_title_id%

📚 Total Collected Titles

%vortextitle_count%

🔍 Check Title Ownership

%vortextitle_has_title_<id>%

👥 Another Player's Active Title

%vortextitle_player_<player>%

🔧 Compatible Integrations

Placeholders can be used in compatible:

- 💬 Chat formatting plugins
- 📊 Scoreboard plugins
- 📑 TAB plugins
- 🖥️ GUI plugins
- 🔌 Other PlaceholderAPI integrations

«⚠️ RGB rendering depends on the formatting capabilities of the destination plugin.»

⚙️・Configuration

VortexTitle provides customizable settings for the title collection system.

📄 Main Configuration

settings:
  prefix: "<gradient:#00E5FF:#5865F2><bold>VortexTitle</bold></gradient>"
  max-titles-per-player: 0
  allow-select-command: true

gui:
  title: "<gradient:#00E5FF:#5865F2><bold>Title Collection</bold></gradient>"
  rows: 6

🔧 Configuration Options

Option| Description
"max-titles-per-player"| Maximum owned titles
"allow-select-command"| Enable direct title selection
"gui.title"| Custom inventory title
"gui.rows"| Inventory size

💡 Set "max-titles-per-player" to "0" for unlimited title collections.

💾・Database System

VortexTitle uses SQLite to store player collections and active title information.

📂 Database Location

plugins/VortexTitle/titles.db

📋 Stored Information

- 🆔 Player UUID
- 🎖️ Owned title IDs
- 🎨 Original title formatting
- 🕒 Title creation timestamps
- ⭐ Currently selected title

All collected titles remain saved after player disconnects and server restarts.

🛠️・Building From Source

📋 Requirements

- Java Development Kit 25
- Gradle 9.x
- Internet connection

📥 Clone Repository

git clone https://github.com/YOUR_USERNAME/VortexTitle.git

📂 Enter Project Directory

cd VortexTitle

⚙️ Build Plugin

gradle build

📦 Output JAR

build/libs/VortexTitle-1.0.0.jar

🗺️・Development Roadmap

- [x] 🎖️ Permanent title storage
- [x] 🎁 Custom title management
- [x] 📚 Player title collection
- [x] 🖥️ Interactive GUI
- [x] 🎨 MiniMessage formatting
- [x] 🌈 HEX and RGB formatting
- [x] 🔗 PlaceholderAPI integration
- [x] 💾 SQLite database
- [x] 👑 Staff management commands
- [x] 🔃 Configuration reload
- [ ] 🎨 Advanced GUI customization
- [ ] 📂 Title categories
- [ ] 🔌 Additional integrations

🐛・Bug Reports & Support

Found a bug or have a suggestion?

Please create a GitHub Issue and include:

- 🎮 Minecraft server version
- ☕ Java version
- 📦 VortexTitle version
- 📋 Console error logs
- 🔍 Steps to reproduce the issue

🤝・Credits & Developer

Developed by Vortex Network

Minecraft Plugin Development & Server Solutions.

<p align="center">
  <img src="https://img.shields.io/badge/Developed%20by-Vortex%20Network-5865F2?style=for-the-badge" />
  <br/><br/>
  <b>⚡ VortexTitle</b>
  <br/>
  <i>Your Titles. Your Identity. Your Collection.</i>
  <br/><br/>
  © Vortex Network
</p>
