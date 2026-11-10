⚡ VortexTitle

Advanced Permanent Custom Title System

A powerful, flexible, and customizable title collection plugin for Minecraft servers. VortexTitle allows administrators to grant unlimited custom titles while giving players the freedom to collect, manage, and switch between their owned titles.

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-5865F2?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Minecraft-26.3-3C8527?style=for-the-badge&logo=minecraft&logoColor=white" />
  <img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur-blue?style=for-the-badge" />
</p><p align="center">
  <b>Developed by Vortex Network</b>
</p>---

Overview

VortexTitle introduces a permanent title collection system designed for modern Minecraft servers.

Unlike traditional prefix systems, players can own multiple custom titles simultaneously. Staff members can grant titles directly without manually registering them in configuration files.

Every granted title is stored permanently in SQLite, allowing players to switch between their collected titles at any time.

Features

Feature| Description
Permanent Collection| Permanently stores all granted titles.
Custom Titles| Staff can create titles with custom text.
Unlimited Collection| Players can collect multiple titles.
Interactive GUI| Browse and select owned titles.
MiniMessage| Supports Adventure MiniMessage formatting.
RGB & HEX| Supports RGB and HEX color formats.
Gradient & Rainbow| Supports colorful text effects.
PlaceholderAPI| Integrates with compatible plugins.
SQLite Database| Persistent player data storage.
Staff Commands| Grant, revoke, and manage titles.
Configuration| Customizable GUI and messages.
Reload System| Reload configuration without restarting.

Formatting Support

VortexTitle supports various Minecraft text formatting systems.

Legacy Colors

&bWARRIOR
&6LEGEND

HEX Colors

&#00FFFFWARRIOR
&#FF00FFMYTHICAL

MiniMessage

<#00FFFF>WARRIOR

Gradient

<gradient:#00FFFF:#0000FF>WARRIOR</gradient>

Rainbow

<rainbow>MYTHICAL</rainbow>

RGBirdflop

&x&0&0&F&F&F&F VORTEX

Additional MiniMessage formatting includes bold, italic, underline, strikethrough, obfuscated text, and other supported Adventure tags.

Installation

Requirements

- Minecraft 26.3
- Java 25
- Paper or Purpur
- PlaceholderAPI (Optional)

Setup

1. Download the VortexTitle JAR.
2. Place the JAR inside your server's "plugins" folder.
3. Install PlaceholderAPI for placeholder integration.
4. Restart your server.
5. Configure VortexTitle inside its configuration directory.

plugins/
└── VortexTitle/
    ├── config.yml
    └── titles.db

Commands

Player Commands

Command| Description
"/title"| Open the title collection GUI.
"/title gui"| Open the title collection GUI.
"/title owned"| Display collected titles.
"/title select <id>"| Select an owned title.
"/title reset"| Clear the active title.

Staff Commands

Give Custom Title

/title give <player> <title>

Example:

/title give Player1 <gradient:#00FFFF:#0000FF>WARRIOR</gradient>

Remove Specific Title

/title take <player> <id>

Remove All Titles

/title take <player> all

View Player Collection

/title list <player>

Reload Configuration

/title reload

Permissions

Player Permissions

Permission| Description| Default
"vortextitle.use"| Access title collection| "true"

Staff Permissions

Permission| Description| Default
"vortextitle.admin"| Full administration access| "op"
"vortextitle.give"| Grant custom titles| "op"
"vortextitle.take"| Revoke player titles| "op"
"vortextitle.reload"| Reload plugin configuration| "op"

PlaceholderAPI Integration

VortexTitle provides PlaceholderAPI support for displaying player titles in compatible plugins.

Available Placeholders

Active Title

%vortextitle_title%

Active Title ID

%vortextitle_title_id%

Collected Title Count

%vortextitle_count%

Check Title Ownership

%vortextitle_has_title_<id>%

Another Player's Active Title

%vortextitle_player_<player>%

These placeholders can be used with compatible chat, scoreboard, TAB, and other PlaceholderAPI-supported plugins.

«RGB rendering depends on the formatting support of the destination plugin.»

Configuration

VortexTitle provides configurable GUI settings, messages, and title collection limits.

Example:

settings:
  prefix: "<gradient:#00E5FF:#5865F2><bold>VortexTitle</bold></gradient>"
  max-titles-per-player: 0
  allow-select-command: true

gui:
  title: "<gradient:#00E5FF:#5865F2><bold>Title Collection</bold></gradient>"
  rows: 6

Configuration Options

Option| Description
"max-titles-per-player"| Maximum number of collected titles.
"allow-select-command"| Enables direct title selection.
"gui.title"| Custom inventory title.
"gui.rows"| Inventory size.

Set "max-titles-per-player" to "0" for unlimited title collections.

Database

VortexTitle uses SQLite for persistent player title storage.

Database Location

plugins/VortexTitle/titles.db

Stored Information

- Player UUID
- Owned title IDs
- Original title formatting
- Title creation timestamps
- Currently selected title

Player collections remain stored after disconnects and server restarts.

Building From Source

Requirements

- JDK 25
- Gradle 9.x
- Internet connection

Clone Repository

git clone https://github.com/YOUR_USERNAME/VortexTitle.git

Enter Project Directory

cd VortexTitle

Build

gradle build

Output

build/libs/VortexTitle-1.0.0.jar

Roadmap

- [x] Permanent title storage
- [x] Custom title management
- [x] Player title collection
- [x] Interactive inventory GUI
- [x] MiniMessage formatting
- [x] HEX and RGB formatting
- [x] PlaceholderAPI integration
- [x] SQLite database
- [x] Staff management commands
- [x] Configuration reload
- [ ] Advanced GUI customization
- [ ] Title categories
- [ ] Additional integrations

Support & Bug Reports

Found a bug or have a suggestion?

Please create a GitHub Issue and include:

- Minecraft version
- Java version
- VortexTitle version
- Relevant console logs
- Steps to reproduce the issue

Credits

Vortex Network

Minecraft Plugin Development & Server Solutions.

<p align="center">
  <b>VortexTitle</b>
  <br/>
  <i>Your Titles. Your Identity. Your Collection.</i>
  <br/><br/>
  © Vortex Network
</p>
