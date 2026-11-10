package id.vortextitle.manager;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.storage.TitleStorage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HexFormat;
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

    public String createId(String rawTitle) {

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

    public boolean give(
            UUID uuid,
            String rawTitle
    ) throws SQLException {

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

        return storage.addTitle(uuid, id, rawTitle);
    }

    public boolean take(
            UUID uuid,
            String id
    ) throws SQLException {

        return storage.removeTitle(
                uuid,
                id.toLowerCase()
        );
    }

    public int takeAll(UUID uuid) throws SQLException {

        return storage.removeAll(uuid);
    }

    public Map<String, String> getTitles(
            UUID uuid
    ) throws SQLException {

        return storage.getTitles(uuid);
    }

    public String getTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        return storage.getTitle(uuid, id);
    }

    public String getActiveId(
            UUID uuid
    ) throws SQLException {

        return storage.getActiveId(uuid);
    }

    public String getActiveTitle(
            UUID uuid
    ) throws SQLException {

        String id = storage.getActiveId(uuid);

        if (id == null) {
            return null;
        }

        return storage.getTitle(uuid, id);
    }

    public boolean select(
            UUID uuid,
            String id
    ) throws SQLException {

        if (storage.getTitle(uuid, id) == null) {
            return false;
        }

        storage.setActive(uuid, id);

        return true;
    }

    public void reset(UUID uuid) throws SQLException {

        storage.setActive(uuid, null);
    }

    public int count(UUID uuid) throws SQLException {

        return storage.countTitles(uuid);
    }
            }
