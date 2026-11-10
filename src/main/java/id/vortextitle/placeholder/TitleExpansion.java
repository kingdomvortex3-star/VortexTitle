
package id.vortextitle.placeholder;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.manager.TitleManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

public final class TitleExpansion extends PlaceholderExpansion {

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
        return "Vortex Teams";
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

        if (player == null || params == null || params.isEmpty()) {
            return "";
        }

        try {

            String lower = params.toLowerCase(Locale.ROOT);
            UUID uuid = player.getUniqueId();

            /*
             * %vortextitle_title%
             *
             * Returns the player's active title with config formatting.
             */
            if (lower.equals("title")) {

                String rawTitle = manager.getActiveTitle(uuid);

                return rawTitle == null
                        ? ""
                        : manager.formatTitle(rawTitle)
                                .letLegacy();
            }

            /*
             * %vortextitle_title_id%
             *
             * Returns the active title ID.
             */
            if (lower.equals("title_id")) {

                String id = manager.getActiveId(uuid);

                return id == null ? "" : id;
            }

            /*
             * %vortextitle_count%
             *
             * Returns the number of owned titles.
             */
            if (lower.equals("count")) {

                return String.valueOf(
                        manager.count(uuid)
                );
            }

            /*
             * %vortextitle_has_title_<id>%
             *
             * Checks whether the player owns a title ID.
             */
            if (lower.startsWith("has_title_")) {

                String id = params.substring(
                        "has_title_".length()
                );

                return manager.getTitle(
                        uuid,
                        id
                ) == null ? "false" : "true";
            }

            /*
             * %vortextitle_player_<player>%
             *
             * Returns another player's active formatted title.
             */
            if (lower.startsWith("player_")) {

                String targetName = params.substring(
                        "player_".length()
                );

                if (targetName.isBlank()) {
                    return "";
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(targetName);

                String rawTitle = manager.getActiveTitle(
                        target.getUniqueId()
                );

                return rawTitle == null
                        ? ""
                        : manager.formatTitle(rawTitle)
                                .letLegacy();
            }

        } catch (SQLException exception) {

            plugin.getLogger().warning(
                    "Placeholder error: " + exception.getMessage()
            );
        }

        return null;
    }
}
