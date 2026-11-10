
package id.vortextitle.command;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.gui.TitleGUI;
import id.vortextitle.manager.TitleFormatter;
import id.vortextitle.manager.TitleManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class TitleCommand implements CommandExecutor, TabCompleter {

    private final VortexTitlePlugin plugin;
    private final TitleManager manager;
    private final TitleGUI gui;

    public TitleCommand(
            VortexTitlePlugin plugin,
            TitleManager manager,
            TitleGUI gui
    ) {

        this.plugin = plugin;
        this.manager = manager;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (args.length == 0) {
            return openGUI(sender);
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("help")) {
            return help(sender);
        }

        if (sub.equals("gui")) {
            return openGUI(sender);
        }

        try {

            switch (sub) {

                case "give" -> give(sender, args);

                case "take" -> take(sender, args);

                case "list" -> list(sender, args);

                case "owned" -> owned(sender);

                case "select" -> select(sender, args);

                case "reset" -> reset(sender);

                case "reload" -> reload(sender);

                default -> send(sender, "usage");
            }

        } catch (SQLException exception) {

            plugin.getLogger().severe(
                    "Database operation failed: "
                            + exception.getMessage()
            );

            send(sender, "database-error");
        }

        return true;
    }

    /**
     * Opens the title collection GUI.
     */
    private boolean openGUI(CommandSender sender) {

        if (!(sender instanceof Player player)) {
            return send(sender, "player-only");
        }

        if (!player.hasPermission("vortextitle.use")) {
            return send(sender, "no-permission");
        }

        gui.open(player, 0);

        return true;
    }

    /**
     * Displays command help.
     */
    private boolean help(CommandSender sender) {

        sender.sendMessage(TitleFormatter.parse(
                prefix()
                        + "<gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<gradient:#00E5FF:#5865F2><bold>Vortex Title</bold></gradient> <dark_gray>| <gray>Command Help"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<aqua>/title <gray>- Open title collection"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<aqua>/title gui <gray>- Open title collection"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<aqua>/title owned <gray>- View your titles"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<aqua>/title select <id> <gray>- Select an owned title"
        ));

        sender.sendMessage(TitleFormatter.parse(
                "<aqua>/title reset <gray>- Reset your active title"
        ));

        if (hasGivePermission(sender)) {

            sender.sendMessage(TitleFormatter.parse(
                    "<dark_gray> "
            ));

            sender.sendMessage(TitleFormatter.parse(
                    "<gold><bold>Staff Commands"
            ));

            sender.sendMessage(TitleFormatter.parse(
                    "<aqua>/title give <player> <title> <gray>- Give a title"
            ));

            sender.sendMessage(TitleFormatter.parse(
                    "<aqua>/title take <player> <id|all> <gray>- Remove titles"
            ));

            sender.sendMessage(TitleFormatter.parse(
                    "<aqua>/title list <player> <gray>- View player's titles"
            ));
        }

        if (sender.hasPermission("vortextitle.reload")
                || sender.hasPermission("vortextitle.admin")) {

            sender.sendMessage(TitleFormatter.parse(
                    "<aqua>/title reload <gray>- Reload configuration"
            ));
        }

        sender.sendMessage(TitleFormatter.parse(
                "<dark_gray>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        ));

        return true;
    }

    /**
     * Gives a plain title to a player.
     */
    private void give(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!hasGivePermission(sender)) {
            send(sender, "no-permission");
            return;
        }

        if (args.length < 3) {

            sendRaw(
                    sender,
                    "<yellow>Usage: <white>/title give <player> <title>"
            );

            return;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (!isKnownPlayer(target)) {

            send(
                    sender,
                    "player-not-found",
                    "{player}",
                    args[1]
            );

            return;
        }

        String rawTitle = String.join(
                " ",
                Arrays.copyOfRange(args, 2, args.length)
        ).trim();

        if (rawTitle.isBlank()) {
            send(sender, "invalid-title");
            return;
        }

        boolean added = manager.give(
                target.getUniqueId(),
                rawTitle
        );

        String id = manager.createId(rawTitle);

        if (!added) {

            send(
                    sender,
                    "already-owned",
                    "{id}",
                    id
            );

            return;
        }

        String targetName = target.getName() == null
                ? args[1]
                : target.getName();

        sendTitleMessage(
                sender,
                "given",
                rawTitle,
                "{player}",
                targetName,
                "{id}",
                id
        );

        /*
         * Notify the target if online.
         */
        Player onlineTarget = target.getPlayer();

        if (onlineTarget != null) {

            onlineTarget.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<green>You received a new title: "
                    ).append(
                            manager.formatTitle(rawTitle)
                    ).append(
                            TitleFormatter.parse(
                                    "\n<dark_gray>ID: <gray>" + id
                            )
                    )
            );

            onlineTarget.playSound(
                    onlineTarget.getLocation(),
                    org.bukkit.Sound.ENTITY_PLAYER_LEVELUP,
                    1f,
                    1.2f
            );
        }
    }

    /**
     * Removes a title or all titles from a player.
     */
    private void take(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!hasTakePermission(sender)) {
            send(sender, "no-permission");
            return;
        }

        if (args.length < 3) {

            sendRaw(
                    sender,
                    "<yellow>Usage: <white>/title take <player> <id|all>"
            );

            return;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (!isKnownPlayer(target)) {

            send(
                    sender,
                    "player-not-found",
                    "{player}",
                    args[1]
            );

            return;
        }

        String targetName = target.getName() == null
                ? args[1]
                : target.getName();

        if (args[2].equalsIgnoreCase("all")) {

            int removed = manager.takeAll(
                    target.getUniqueId()
            );

            sendRaw(
                    sender,
                    "<green>Successfully removed <white>"
                            + removed
                            + " <green>titles from <white>"
                            + targetName
            );

            return;
        }

        boolean removed = manager.take(
                target.getUniqueId(),
                args[2]
        );

        if (removed) {

            send(
                    sender,
                    "taken",
                    "{id}",
                    args[2],
                    "{player}",
                    targetName
            );

        } else {

            send(
                    sender,
                    "not-owned",
                    "{id}",
                    args[2]
            );
        }
    }

    /**
     * Lists titles owned by another player.
     */
    private void list(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!sender.hasPermission("vortextitle.admin")) {
            send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {

            sendRaw(
                    sender,
                    "<yellow>Usage: <white>/title list <player>"
            );

            return;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (!isKnownPlayer(target)) {

            send(
                    sender,
                    "player-not-found",
                    "{player}",
                    args[1]
            );

            return;
        }

        Map<String, String> titles =
                manager.getTitles(target.getUniqueId());

        String targetName = target.getName() == null
                ? args[1]
                : target.getName();

        send(
                sender,
                "list-header",
                "{player}",
                targetName
        );

        if (titles.isEmpty()) {
            send(sender, "list-empty");
            return;
        }

        for (Map.Entry<String, String> entry : titles.entrySet()) {

            sender.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<dark_gray>▪ <gray>"
                                    + entry.getKey()
                                    + " <dark_gray>» "
                    ).append(
                            manager.formatTitle(entry.getValue())
                    )
            );
        }
    }

    /**
     * Lists the sender's owned titles.
     */
    private void owned(
            CommandSender sender
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
            return;
        }

        if (!player.hasPermission("vortextitle.use")) {
            send(sender, "no-permission");
            return;
        }

        Map<String, String> titles =
                manager.getTitles(player.getUniqueId());

        if (titles.isEmpty()) {
            send(sender, "no-titles");
            return;
        }

        send(
                sender,
                "list-header",
                "{player}",
                player.getName()
        );

        for (Map.Entry<String, String> entry : titles.entrySet()) {

            player.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<dark_gray>▪ <gray>"
                                    + entry.getKey()
                                    + " <dark_gray>» "
                    ).append(
                            manager.formatTitle(entry.getValue())
                    )
            );
        }
    }

    /**
     * Selects an owned title.
     */
    private void select(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
            return;
        }

        if (!player.hasPermission("vortextitle.use")) {
            send(sender, "no-permission");
            return;
        }

        if (!plugin.getConfig().getBoolean(
                "settings.allow-select-command",
                true
        )) {

            send(sender, "select-disabled");
            return;
        }

        if (args.length < 2) {

            sendRaw(
                    sender,
                    "<yellow>Usage: <white>/title select <id>"
            );

            return;
        }

        String rawTitle = manager.getTitle(
                player.getUniqueId(),
                args[1]
        );

        if (rawTitle == null
                || !manager.select(
                        player.getUniqueId(),
                        args[1]
                )) {

            send(
                    sender,
                    "not-owned",
                    "{id}",
                    args[1]
            );

            return;
        }

        sendTitleMessage(
                player,
                "selected",
                rawTitle
        );

        player.playSound(
                player.getLocation(),
                org.bukkit.Sound.UI_BUTTON_CLICK,
                1f,
                1.2f
        );
    }

    /**
     * Resets the active title.
     */
    private void reset(
            CommandSender sender
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
            return;
        }

        if (!player.hasPermission("vortextitle.use")) {
            send(sender, "no-permission");
            return;
        }

        manager.reset(player.getUniqueId());

        send(sender, "reset");

        player.playSound(
                player.getLocation(),
                org.bukkit.Sound.UI_BUTTON_CLICK,
                1f,
                0.8f
        );
    }

    /**
     * Reloads plugin configuration.
     */
    private void reload(CommandSender sender) {

        if (!sender.hasPermission("vortextitle.reload")
                && !sender.hasPermission("vortextitle.admin")) {

            send(sender, "no-permission");
            return;
        }

        plugin.reloadVortexTitle();

        send(sender, "reload");
    }

    /**
     * Sends a configured message with plain text replacements.
     */
    private boolean send(
            CommandSender sender,
            String key,
            String... replacements
    ) {

        String message = prefix()
                + plugin.getConfig().getString(
                        "messages." + key,
                        "<red>Missing message: " + key
                );

        for (int i = 0; i + 1 < replacements.length; i += 2) {

            message = message.replace(
                    replacements[i],
                    replacements[i + 1]
            );
        }

        sender.sendMessage(
                TitleFormatter.parse(message)
        );

        return true;
    }

    /**
     * Sends a configured message with a safely formatted title.
     */
    private void sendTitleMessage(
            CommandSender sender,
            String key,
            String rawTitle,
            String... replacements
    ) {

        String message = prefix()
                + plugin.getConfig().getString(
                        "messages." + key,
                        "<red>Missing message: " + key
                );

        for (int i = 0; i + 1 < replacements.length; i += 2) {

            message = message.replace(
                    replacements[i],
                    replacements[i + 1]
            );
        }

        Component component = TitleFormatter.parse(message);

        component = component.replaceText(
                TextReplacementConfig.builder()
                        .matchLiteral("{title}")
                        .replacement(manager.formatTitle(rawTitle))
                        .build()
        );

        sender.sendMessage(component);
    }

    /**
     * Sends a raw MiniMessage message with the plugin prefix.
     */
    private void sendRaw(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage(
                TitleFormatter.parse(prefix() + message)
        );
    }

    private String prefix() {

        return plugin.getConfig().getString(
                "settings.prefix",
                ""
        );
    }

    private OfflinePlayer findPlayer(String name) {

        Player online = Bukkit.getPlayerExact(name);

        if (online != null) {
            return online;
        }

        return Bukkit.getOfflinePlayer(name);
    }

    private boolean isKnownPlayer(OfflinePlayer player) {

        return player != null
                && (player.isOnline() || player.hasPlayedBefore());
    }

    private boolean hasGivePermission(CommandSender sender) {

        return sender.hasPermission("vortextitle.give")
                || sender.hasPermission("vortextitle.admin");
    }

    private boolean hasTakePermission(CommandSender sender) {

        return sender.hasPermission("vortextitle.take")
                || sender.hasPermission("vortextitle.admin");
    }

    private List<String> startsWith(
            List<String> values,
            String input
    ) {

        String lower = input.toLowerCase(Locale.ROOT);

        return values.stream()
                .filter(value ->
                        value.toLowerCase(Locale.ROOT)
                                .startsWith(lower)
                )
                .collect(Collectors.toList());
    }

    /**
     * Tab completion.
     */
    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {

            List<String> subcommands = new ArrayList<>(
                    List.of("gui", "owned", "select", "reset", "help")
            );

            if (hasGivePermission(sender)) {
                subcommands.add("give");
            }

            if (hasTakePermission(sender)) {
                subcommands.add("take");
            }

            if (sender.hasPermission("vortextitle.admin")) {
                subcommands.add("list");
            }

            if (sender.hasPermission("vortextitle.reload")
                    || sender.hasPermission("vortextitle.admin")) {
                subcommands.add("reload");
            }

            return startsWith(subcommands, args[0]);
        }

        if (args.length == 2
                && (args[0].equalsIgnoreCase("give")
                && hasGivePermission(sender)
                || args[0].equalsIgnoreCase("take")
                && hasTakePermission(sender)
                || args[0].equalsIgnoreCase("list")
                && sender.hasPermission("vortextitle.admin"))) {

            return startsWith(
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .toList(),
                    args[1]
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("take")
                && hasTakePermission(sender)) {

            List<String> suggestions = new ArrayList<>();
            suggestions.add("all");

            OfflinePlayer target = findPlayer(args[1]);

            if (isKnownPlayer(target)) {

                try {

                    suggestions.addAll(
                            manager.getTitles(
                                    target.getUniqueId()
                            ).keySet()
                    );

                } catch (SQLException exception) {

                    plugin.getLogger().warning(
                            "Tab completion error: "
                                    + exception.getMessage()
                    );
                }
            }

            return startsWith(suggestions, args[2]);
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("select")
                && sender instanceof Player player
                && player.hasPermission("vortextitle.use")) {

            try {

                return startsWith(
                        new ArrayList<>(
                                manager.getTitles(
                                        player.getUniqueId()
                                ).keySet()
                        ),
                        args[1]
                );

            } catch (SQLException exception) {

                return List.of();
            }
        }

        return List.of();
    }
}
