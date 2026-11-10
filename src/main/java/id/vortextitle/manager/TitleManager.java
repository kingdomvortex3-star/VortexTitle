
package id.vortextitle.manager;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.storage.TitleStorage;

import net.kyori.adventure.text.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class TitleManager {

    private final VortexTitlePlugin plugin;
    private final TitleStorage storage;

    public TitleManager(
            VortexTitlePlugin plugin,
            TitleStorage storage
    ) {

        this.plugin = plugin;
        this.storage = storage;
    }

    /**
     * Generates a stable ID from the plain title name.
     */
    public String createId(String rawTitle) {

        if (rawTitle == null || rawTitle.isBlank()) {
            throw new IllegalArgumentException(
                    "Title cannot be empty"
            );
        }

        try {

            byte[] digest = MessageDigest
                    .getInstance("SHA-256")
                    .digest(
                            rawTitle.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of()
                    .formatHex(digest)
                    .substring(0, 12);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Could not generate title ID",
                    exception
            );
        }
    }

    /**
     * Gives a plain title to a player.
     *
     * The title is stored without MiniMessage or color formatting.
     */
    public boolean give(
            UUID uuid,
            String rawTitle
    ) throws SQLException {

        if (uuid == null
                || rawTitle == null
                || rawTitle.isBlank()) {

            return false;
        }

        String id = createId(rawTitle);

        int limit = plugin.getConfig().getInt(
                "settings.max-titles-per-player",
                0
        );

        if (limit > 0
                && !storage.getTitles(uuid).containsKey(id)
                && storage.countTitles(uuid) >= limit) {

            return false;
        }

        return storage.addTitle(
                uuid,
                id,
                rawTitle
        );
    }

    /**
     * Removes one title from a player.
     */
    public boolean take(
            UUID uuid,
            String id
    ) throws SQLException {

        if (uuid == null || id == null || id.isBlank()) {
            return false;
        }

        return storage.removeTitle(
                uuid,
                normalizeId(id)
        );
    }

    /**
     * Removes every title owned by a player.
     */
    public int takeAll(UUID uuid) throws SQLException {

        if (uuid == null) {
            return 0;
        }

        return storage.removeAll(uuid);
    }

    /**
     * Gets all titles owned by a player.
     *
     * Map key   = title ID
     * Map value = plain title name
     */
    public Map<String, String> getTitles(
            UUID uuid
    ) throws SQLException {

        return storage.getTitles(uuid);
    }

    /**
     * Gets one plain title by ID.
     */
    public String getTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        if (uuid == null || id == null) {
            return null;
        }

        return storage.getTitle(
                uuid,
                normalizeId(id)
        );
    }

    /**
     * Gets the active title ID.
     */
    public String getActiveId(
            UUID uuid
    ) throws SQLException {

        if (uuid == null) {
            return null;
        }

        return storage.getActiveId(uuid);
    }

    /**
     * Gets the active title as plain text.
     */
    public String getActiveTitle(
            UUID uuid
    ) throws SQLException {

        String id = getActiveId(uuid);

        if (id == null) {
            return null;
        }

        return getTitle(uuid, id);
    }

    /**
     * Formats a plain title using title-colors in config.yml.
     *
     * This method does not modify the stored title.
     */
    public Component formatTitle(String rawTitle) {

        return TitleFormatter.formatTitle(
                rawTitle,
                plugin.getConfig()
        );
    }

    /**
     * Gets a player's formatted title by ID.
     */
    public Component getFormattedTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        String rawTitle = getTitle(uuid, id);

        if (rawTitle == null) {
            return Component.empty();
        }

        return formatTitle(rawTitle);
    }

    /**
     * Gets a player's active title as a formatted Component.
     */
    public Component getFormattedActiveTitle(
            UUID uuid
    ) throws SQLException {

        String rawTitle = getActiveTitle(uuid);

        if (rawTitle == null) {
            return Component.empty();
        }

        return formatTitle(rawTitle);
    }

    /**
     * Selects a title as the player's active title.
     */
    public boolean select(
            UUID uuid,
            String id
    ) throws SQLException {

        if (uuid == null || id == null || id.isBlank()) {
            return false;
        }

        String normalizedId = normalizeId(id);

        if (storage.getTitle(uuid, normalizedId) == null) {
            return false;
        }

        storage.setActive(
                uuid,
                normalizedId
        );

        return true;
    }

    /**
     * Resets the player's active title.
     */
    public void reset(UUID uuid) throws SQLException {

        if (uuid == null) {
            return;
        }

        storage.setActive(uuid, null);
    }

    /**
     * Counts the player's owned titles.
     */
    public int count(UUID uuid) throws SQLException {

        if (uuid == null) {
            return 0;
        }

        return storage.countTitles(uuid);
    }

    /**
     * Normalizes title IDs.
     */
    private String normalizeId(String id) {

        return id.toLowerCase(Locale.ROOT);
    }
}
