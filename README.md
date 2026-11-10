⚡ VortexTitle

Advanced Permanent Custom Title System for Minecraft

VortexTitle is a powerful and customizable Minecraft plugin that allows server administrators to grant permanent custom titles to players.

Players can collect multiple titles, manage their collections, and freely switch between titles whenever they want.

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-26.3-3C8527?style=for-the-badge&logo=minecraft&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
</p>---

✨ Features

- Permanent Title Collection – Players can permanently collect multiple titles.
- Custom Titles – Staff can grant titles with any custom text.
- Player Title Selection – Players can freely switch between owned titles.
- MiniMessage Support – Adventure MiniMessage formatting.
- RGB & HEX Colors – Support for RGB color formats.
- Gradient & Rainbow – Colorful gradient and rainbow titles.
- Legacy Formatting – Traditional Minecraft color codes.
- Interactive GUI – Browse and select titles through an inventory menu.
- SQLite Storage – Persistent player title data.
- PlaceholderAPI Integration – Display active titles in compatible plugins.
- Staff Management – Grant, revoke, and manage player titles.
- Configurable GUI – Customize inventory size, slots, names, and messages.
- Reload Support – Reload configuration without restarting the server.
- Duplicate Protection – Prevent identical titles from being granted twice to the same player.

---

🎨 Supported Formatting

VortexTitle supports multiple Minecraft text formatting systems.

Legacy Color

&bWARRIOR

HEX Color

&#00FFFFWARRIOR

MiniMessage

<#00FFFF>WARRIOR

Gradient

<gradient:#00FFFF:#0000FF>WARRIOR</gradient>

Rainbow

<rainbow>MYTHICAL</rainbow>

Bold

<bold>LEGEND</bold>

Italic

<italic>ELITE</italic>

Underline

<underlined>VORTEX</underlined>

RGBirdflop RGB

&x&0&0&F&F&F&F VORTEX

---

📦 Installation

1. Download or build the VortexTitle JAR.

2. Install the JAR into your server's "plugins" directory.

3. Install PlaceholderAPI if you want placeholder integration.

4. Start your Paper or Purpur server.

5. Configure the plugin inside:

plugins/VortexTitle/config.yml

---

🛠️ Build From Source

Requirements

- Java Development Kit 25

- Gradle 9.x

- Paper API 26.3

- Internet connection for dependency downloads

Clone Repository

git clone https://github.com/YOUR_USERNAME/VortexTitle.git

cd VortexTitle

Build Plugin

gradle build

Output

build/libs/VortexTitle-1.0.0.jar

---

💻 Commands

Player Commands

Open Title GUI

/title

Open Title Collection

/title gui

View Owned Titles

/title owned

Select an Owned Title

/title select <id>

Reset Active Title

/title reset

Staff Commands

Give a Permanent Custom Title

/title give <player> <title>

Example:

/title give Player1 <gradient:#00FFFF:#0000FF>WARRIOR</gradient>

Remove a Specific Title

/title take <player> <id>

Remove All Player Titles

/title take <player> all

View Player Title Collection

/title list <player>

Reload Plugin Configuration

/title reload

---

🔐 Permissions

Player Permissions

Permission| Description| Default
"vortextitle.use"| Access title collection| True

Staff Permissions

Permission| Description| Default
"vortextitle.admin"| Full administration access| OP
"vortextitle.give"| Grant permanent titles| OP
"vortextitle.take"| Remove player titles| OP
"vortextitle.reload"| Reload plugin configuration| OP

---

🔗 PlaceholderAPI

VortexTitle provides PlaceholderAPI integration.

Active Title

%vortextitle_title%

Returns the player's currently selected title.

Active Title ID

%vortextitle_title_id%

Returns the ID of the currently selected title.

Title Collection Count

%vortextitle_count%

Returns the total number of collected titles.

Check Title Ownership

%vortextitle_has_title_<id>%

Returns "true" or "false".

Another Player's Active Title

%vortextitle_player_<player>%

Returns another player's currently selected title.

Example Usage

%vortextitle_title%

Use these placeholders in compatible chat, scoreboard, TAB, and other plugins.

Note: RGB rendering depends on the formatting capabilities of the destination plugin.

---

⚙️ Configuration

Main configuration:

settings:
  prefix: "<gradient:#00E5FF:#5865F2><bold>VortexTitle</bold></gradient>"

  max-titles-per-player: 0

  allow-select-command: true

gui:
  title: "<gradient:#00E5FF:#5865F2><bold>Title Collection</bold></gradient>"

  rows: 6

Configuration Options

- "max-titles-per-player: 0" – Unlimited title collection.
- "allow-select-command: true" – Allow players to select titles using commands.
- "gui.rows: 6" – Set the GUI inventory size.

---

💾 Data Storage

VortexTitle uses SQLite to store player title collections.

Database Location

plugins/VortexTitle/titles.db

Stored Data

- Player UUID
- Owned title IDs
- Original title formatting
- Title creation timestamps
- Currently selected title

All collected titles remain saved after players disconnect or the server restarts.

---

🗺️ Roadmap

- [x] Permanent title storage
- [x] Custom title granting
- [x] Title collection GUI
- [x] MiniMessage formatting
- [x] HEX and RGB formatting
- [x] PlaceholderAPI integration
- [x] SQLite storage
- [x] Staff management commands
- [x] Configuration reload
- [ ] Advanced GUI customization
- [ ] Title categories
- [ ] Additional integrations

---

🤝 Contributing

Contributions, suggestions, and bug reports are welcome.

If you encounter an issue, please provide:

- Minecraft server version
- Java version
- Plugin version
- Error logs
- Steps to reproduce the issue

---

📜 License

Choose and add an appropriate open-source license before distributing this project.

---

❤️ Credits

Developed by Vortex Network

Creating powerful and customizable Minecraft server solutions.

<p align="center">
  <b>VortexTitle</b>
  <br>
  <i>Your Titles. Your Identity. Your Collection.</i>
</p>
