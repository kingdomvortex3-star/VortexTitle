package id.vortextitle.placeholder;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.manager.TitleFormatter;
import id.vortextitle.manager.TitleManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.Locale;

public final class TitleExpansion
        extends PlaceholderExpansion {

    private final VortexTitlePlugin plugin;
    private final TitleManager manager;

    public TitleExpansion(
            VortexTitlePlugin plugin,
            TitleManager manager
    ) {

        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public String getIdentifier() {
        return "vortextitle";
    }

    @Override
    public String getAuthor() {
        return "Vortex Network";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(
            OfflinePlayer player,
            String params
    ) {

        if (player == null) {
            return "";
        }

        try {

            String lower = params.toLowerCase(Locale.ROOT);

            if (lower.equals("title")) {

                String title = manager.getActiveTitle(
                        player.getUniqueId()
                );

                return title == null
                        ? ""
                        : TitleFormatter.toLegacy(title);
            }

            if (lower.equals("title_id")) {

                String id = manager.getActiveId(
                        player.getUniqueId()
                );

                return id == null ? "" : id;
            }

            if (lower.equals("count")) {

                return String.valueOf(
                        manager.count(player.getUniqueId())
                );
            }

            if (lower.startsWith("has_title_")) {

                String id = params.substring(
                        "has_title_".length()
                );

                return manager.getTitle(
                        player.getUniqueId(),
                        id
                ) == null ? "false" : "true";
            }

            if (lower.startsWith("player_")) {

                String targetName = params.substring(
                        "player_".length()
                );

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(targetName);

                String title = manager.getActiveTitle(
                        target.getUniqueId()
                );

                return title == null
                        ? ""
                        : TitleFormatter.toLegacy(title);
            }

        } catch (SQLException exception) {

            plugin.getLogger().warning(
                    "Placeholder error: " + exception.getMessage()
            );
        }

        return null;
    }
}
