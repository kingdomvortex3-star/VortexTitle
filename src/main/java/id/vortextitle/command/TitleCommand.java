package id.vortextitle.command;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.gui.TitleGUI;
import id.vortextitle.manager.TitleFormatter;
import id.vortextitle.manager.TitleManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public final class TitleCommand
        implements CommandExecutor, TabCompleter {

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

        if (args.length == 0
                || args[0].equalsIgnoreCase("gui")) {

            if (!(sender instanceof Player player)) {
                return send(sender, "player-only");
            }

            if (!sender.hasPermission("vortextitle.use")) {
                return send(sender, "no-permission");
            }

            gui.open(player, 0);

            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

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

    private void give(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!sender.hasPermission("vortextitle.give")
                && !sender.hasPermission("vortextitle.admin")) {

            send(sender, "no-permission");
            return;
        }

        if (args.length < 3) {

            sender.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<yellow>Usage: /title give <player> <title>"
                    )
            );

            return;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[1]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {

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
        );

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

        send(
                sender,
                "given",
                "{title}",
                rawTitle,
                "{player}",
                target.getName() == null
                        ? args[1]
                        : target.getName(),
                "{id}",
                id
        );

        if (target.isOnline()) {

            target.getPlayer().sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<green>You received a new title: "
                    ).append(
                            TitleFormatter.parse(rawTitle)
                    ).append(
                            TitleFormatter.parse(
                                    " <dark_gray>(ID: <gray>"
                                            + id
                                            + "<dark_gray>)"
                            )
                    )
            );
        }
    }

    private void take(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!sender.hasPermission("vortextitle.take")
                && !sender.hasPermission("vortextitle.admin")) {

            send(sender, "no-permission");
            return;
        }

        if (args.length < 3) {

            sender.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<yellow>Usage: /title take <player> <id|all>"
                    )
            );

            return;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[1]);

        if (!target.hasPlayedBefore() && !target.isOnline()) {

            send(
                    sender,
                    "player-not-found",
                    "{player}",
                    args[1]
            );

            return;
        }

        if (args[2].equalsIgnoreCase("all")) {

            manager.takeAll(target.getUniqueId());

            send(
                    sender,
                    "all-taken",
                    "{player}",
                    args[1]
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
                    args[1]
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

    private void list(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!sender.hasPermission("vortextitle.admin")) {
            send(sender, "no-permission");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<yellow>Usage: /title list <player>"
                    )
            );
            return;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[1]);

        Map<String, String> titles =
                manager.getTitles(target.getUniqueId());

        send(
                sender,
                "list-header",
                "{player}",
                args[1]
        );

        if (titles.isEmpty()) {
            send(sender, "list-empty");
            return;
        }

        for (Map.Entry<String, String> entry
                : titles.entrySet()) {

            sender.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<gray>- <white>"
                                    + entry.getKey()
                                    + " <dark_gray>| "
                    ).append(
                            TitleFormatter.parse(entry.getValue())
                    )
            );
        }
    }

    private void owned(
            CommandSender sender
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
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

        for (Map.Entry<String, String> entry
                : titles.entrySet()) {

            player.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<gray>- <white>"
                                    + entry.getKey()
                                    + " <dark_gray>| "
                    ).append(
                            TitleFormatter.parse(entry.getValue())
                    )
            );
        }
    }

    private void select(
            CommandSender sender,
            String[] args
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
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

            player.sendMessage(
                    TitleFormatter.parse(
                            prefix()
                                    + "<yellow>Usage: /title select <id>"
                    )
            );

            return;
        }

        String raw = manager.getTitle(
                player.getUniqueId(),
                args[1]
        );

        if (raw == null
                || !manager.select(player.getUniqueId(), args[1])) {

            send(
                    sender,
                    "not-owned",
                    "{id}",
                    args[1]
            );

            return;
        }

        player.sendMessage(
                TitleFormatter.parse(
                        prefix() + "<green>Your active title is now: "
                ).append(
                        TitleFormatter.parse(raw)
                )
        );
    }

    private void reset(
            CommandSender sender
    ) throws SQLException {

        if (!(sender instanceof Player player)) {
            send(sender, "player-only");
            return;
        }

        manager.reset(player.getUniqueId());

        send(sender, "reset");
    }

    private void reload(CommandSender sender) {

        if (!sender.hasPermission("vortextitle.reload")
                && !sender.hasPermission("vortextitle.admin")) {

            send(sender, "no-permission");
            return;
        }

        plugin.reloadVortexTitle();

        send(sender, "reload");
    }

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

    private String prefix() {

        return plugin.getConfig().getString(
                "settings.prefix",
                ""
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {

            return startsWith(
                    List.of(
                            "gui",
                            "give",
                            "take",
                            "list",
                            "owned",
                            "select",
                            "reset",
                            "reload"
                    ),
                    args[0]
            );
        }

        if (args.length == 2
                && List.of("give", "take", "list")
                .contains(args[0].toLowerCase(Locale.ROOT))) {

            return startsWith(
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .map(Player::getName)
                            .toList(),
                    args[1]
            );
        }

        if (args.length == 3
                && args[0].equalsIgnoreCase("take")) {

            return startsWith(
                    List.of("all", "title-id"),
                    args[2]
            );
        }

        if (args.length == 2
                && args[0].equalsIgnoreCase("select")
                && sender instanceof Player player) {

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
                }
