package id.vortextitle.storage;

import id.vortextitle.VortexTitlePlugin;

import java.io.File;
import java.sql.*;
import java.util.*;

public final class TitleStorage {

    private final VortexTitlePlugin plugin;
    private Connection connection;

    public TitleStorage(
            VortexTitlePlugin plugin,
            File file
    ) throws SQLException {

        this.plugin = plugin;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "SQLite JDBC driver unavailable",
                    exception
            );
        }

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + file.getAbsolutePath()
        );

        try (Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA foreign_keys=ON");
            statement.execute("PRAGMA busy_timeout=5000");
        }

        createTables();
    }

    private void createTables() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_titles (
                    uuid TEXT NOT NULL,
                    title_id TEXT NOT NULL,
                    title_text TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    PRIMARY KEY (uuid, title_id)
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS active_titles (
                    uuid TEXT PRIMARY KEY,
                    title_id TEXT NOT NULL
                )
            """);
        }
    }

    public synchronized boolean addTitle(
            UUID uuid,
            String id,
            String text
    ) throws SQLException {

        String sql = """
            INSERT OR IGNORE INTO player_titles
            (uuid, title_id, title_text, created_at)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, id);
            statement.setString(3, text);
            statement.setLong(4, System.currentTimeMillis());

            return statement.executeUpdate() > 0;
        }
    }

    public synchronized boolean removeTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        boolean removed;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "DELETE FROM player_titles WHERE uuid=? AND title_id=?"
                     )) {

            statement.setString(1, uuid.toString());
            statement.setString(2, id);

            removed = statement.executeUpdate() > 0;
        }

        if (removed) {

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "DELETE FROM active_titles WHERE uuid=? AND title_id=?"
                         )) {

                statement.setString(1, uuid.toString());
                statement.setString(2, id);

                statement.executeUpdate();
            }
        }

        return removed;
    }

    public synchronized int removeAll(
            UUID uuid
    ) throws SQLException {

        int count;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "DELETE FROM player_titles WHERE uuid=?"
                     )) {

            statement.setString(1, uuid.toString());

            count = statement.executeUpdate();
        }

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "DELETE FROM active_titles WHERE uuid=?"
                     )) {

            statement.setString(1, uuid.toString());
            statement.executeUpdate();
        }

        return count;
    }

    public synchronized Map<String, String> getTitles(
            UUID uuid
    ) throws SQLException {

        Map<String, String> result = new LinkedHashMap<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             SELECT title_id, title_text
                             FROM player_titles
                             WHERE uuid=?
                             ORDER BY created_at ASC
                             """
                     )) {

            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {

                    result.put(
                            rs.getString("title_id"),
                            rs.getString("title_text")
                    );
                }
            }
        }

        return result;
    }

    public synchronized String getTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             SELECT title_text
                             FROM player_titles
                             WHERE uuid=? AND title_id=?
                             """
                     )) {

            statement.setString(1, uuid.toString());
            statement.setString(2, id);

            try (ResultSet rs = statement.executeQuery()) {

                return rs.next()
                        ? rs.getString("title_text")
                        : null;
            }
        }
    }

    public synchronized String getActiveId(
            UUID uuid
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "SELECT title_id FROM active_titles WHERE uuid=?"
                     )) {

            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {

                return rs.next()
                        ? rs.getString("title_id")
                        : null;
            }
        }
    }

    public synchronized void setActive(
            UUID uuid,
            String id
    ) throws SQLException {

        if (id == null) {

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "DELETE FROM active_titles WHERE uuid=?"
                         )) {

                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }

            return;
        }

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             INSERT INTO active_titles(uuid, title_id)
                             VALUES (?, ?)
                             ON CONFLICT(uuid)
                             DO UPDATE SET title_id=excluded.title_id
                             """
                     )) {

            statement.setString(1, uuid.toString());
            statement.setString(2, id);

            statement.executeUpdate();
        }
    }

    public synchronized int countTitles(
            UUID uuid
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             "SELECT COUNT(*) FROM player_titles WHERE uuid=?"
                     )) {

            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {

                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public synchronized void close() {

        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException exception) {

            plugin.getLogger().warning(
                    "SQLite close error: " + exception.getMessage()
            );
        }
    }
  }
