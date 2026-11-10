⚡ VortexTitle

Advanced Permanent Custom Title System for Minecraft

VortexTitle is a powerful and customizable Minecraft plugin that allows server administrators to grant permanent custom titles to players. Players can collect multiple titles, manage their collections, and freely switch between titles whenever they want.

Designed for Paper & Purpur 26.3, powered by Java 25, Adventure MiniMessage, SQLite, and PlaceholderAPI.

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-26.3-3C8527?style=for-the-badge&logo=minecraft&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
</p>---

✨ Features

- Permanent Title Collection – Players can permanently collect multiple titles.
- Custom Titles – Staff can grant titles with any custom text.
- Player Title Selection – Players can freely switch between their owned titles.
- MiniMessage Support – Full Adventure MiniMessage formatting.
- RGB & HEX Colors – Support for RGBirdflop-style color formats.
- Gradient & Rainbow – Create colorful gradient and rainbow titles.
- Legacy Formatting – Supports traditional Minecraft color codes.
- Interactive GUI – Browse and select titles through an inventory menu.
- SQLite Storage – Persistent player title data.
- PlaceholderAPI Integration – Display active titles in supported plugins.
- Staff Management – Grant, revoke, and manage player titles.
- Configurable GUI – Customize inventory size, slots, names, and messages.
- Reload Support – Reload configuration without restarting the server.
- Duplicate Protection – Prevent identical titles from being added twice to the same player.

🎨 Supported Formatting

VortexTitle supports multiple Minecraft text formatting systems.

Format| Example
Legacy| "&bWARRIOR"
HEX| "&#00FFFFWARRIOR"
MiniMessage| "<#00FFFF>WARRIOR"
Gradient| "<gradient:#00FFFF:#0000FF>WARRIOR</gradient>"
Rainbow| "<rainbow>MYTHICAL</rainbow>"
Bold| "<bold>LEGEND</bold>"
Italic| "<italic>ELITE</italic>"
Underline| "<underlined>VORTEX</underlined>"
RGBirdflop| "&x&0&0&F&F&F&F VORTEX"

📦 Installation

1. Download or build the VortexTitle JAR.
2. Install the plugin into your server's "plugins" directory.
3. Install PlaceholderAPI if you want placeholder integration.
4. Start your Paper or Purpur server.
5. Configure the plugin in "plugins/VortexTitle/config.yml".

🛠️ Build From Source

Requirements

- Java Development Kit 25
- Gradle 9.x
- Paper API 26.3
- Internet connection for dependency downloads

Build

Clone the repository:

git clone https://github.com/YOUR_USERNAME/VortexTitle.git
cd VortexTitle

Build the plugin:

gradle build

The compiled plugin will be available at:

build/libs/VortexTitle-1.0.0.jar

💻 Commands

Player Commands

Command| Description
"/title"| Open the title collection GUI
"/title gui"| Open the title collection GUI
"/title owned"| View your collected titles
"/title select <id>"| Select an owned title
"/title reset"| Clear your active title

Staff Commands

Command| Description
"/title give <player> <title>"| Grant a permanent custom title
"/title take <player> <id>"| Revoke a specific title
"/title take <player> all"| Remove all titles from a player
"/title list <player>"| View a player's title collection
"/title reload"| Reload plugin configuration

🔐 Permissions

Permission| Description| Default
"vortextitle.use"| Access title collection| True
"vortextitle.admin"| Full administration access| OP
"vortextitle.give"| Grant titles| OP
"vortextitle.take"| Revoke titles| OP
"vortextitle.reload"| Reload configuration| OP

🔗 PlaceholderAPI

VortexTitle provides PlaceholderAPI integration.

Placeholder| Description
"%vortextitle_title%"| Player's active title
"%vortextitle_title_id%"| Active title ID
"%vortextitle_count%"| Total collected titles
"%vortextitle_has_title_<id>%"| Check title ownership
"%vortextitle_player_<player>%"| Another player's active title

Example

%vortextitle_title%

Use these placeholders in compatible chat, scoreboard, TAB, and other plugins.

Note: The destination plugin must support the relevant formatting for RGB colors to render correctly.

⚙️ Configuration

Main configuration:

settings:
  prefix: "<gradient:#00E5FF:#5865F2><bold>VortexTitle</bold></gradient>"
  max-titles-per-player: 0
  allow-select-command: true

gui:
  title: "<gradient:#00E5FF:#5865F2><bold>Title Collection</bold></gradient>"
  rows: 6

Set "max-titles-per-player" to "0" for unlimited title collections.

💾 Data Storage

VortexTitle uses SQLite to store player title collections.

Database location:

plugins/VortexTitle/titles.db

Player data includes:

- Player UUID
- Owned title IDs
- Original title formatting
- Title creation timestamps
- Currently selected title

Collected titles remain saved after players disconnect or the server restarts.

🗺️ Roadmap

- [x] Permanent title storage
- [x] Custom title granting
- [x] Title collection GUI
- [x] MiniMessage formatting
- [x] HEX and RGB formatting
- [x] PlaceholderAPI integration
- [x] SQLite storage
- [x] Staff management commands
- [ ] Advanced GUI customization
- [ ] Title categories
- [ ] Title expiration system
- [ ] Additional integrations

🤝 Contributing

Contributions, suggestions, and bug reports are welcome.

If you find an issue, please open a GitHub Issue with:

- Minecraft server version
- Java version
- Plugin version
- Error logs
- Steps to reproduce the issue

📜 License

Choose and add an appropriate open-source license before distributing this project.

❤️ Credits

Developed by Vortex Network

Creating powerful and customizable Minecraft server solutions.

<p align="center">
  <b>VortexTitle</b><br/>
  <i>Your Titles. Your Identity. Your Collection.</i>
</p>
