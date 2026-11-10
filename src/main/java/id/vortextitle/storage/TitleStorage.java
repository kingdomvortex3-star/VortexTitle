
package id.vortextitle.storage;

import id.vortextitle.VortexTitlePlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class TitleStorage {

    private final VortexTitlePlugin plugin;
    private Connection connection;

    public TitleStorage(
            VortexTitlePlugin plugin,
            File file
    ) throws SQLException {

        this.plugin = plugin;

        if (file == null) {
            throw new SQLException("Database file cannot be null!");
        }

        File parent = file.getParentFile();

        if (parent != null
                && !parent.exists()
                && !parent.mkdirs()) {

            throw new SQLException(
                    "Could not create database directory!"
            );
        }

        try {
            Class.forName("org.sqlite.JDBC");

        } catch (ClassNotFoundException exception) {

            throw new SQLException(
                    "SQLite JDBC driver unavailable!",
                    exception
            );
        }

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + file.getAbsolutePath()
        );

        try {

            configureDatabase();
            createTables();

        } catch (SQLException exception) {

            close();
            throw exception;
        }
    }

    /**
     * Configures SQLite database performance and safety.
     */
    private void configureDatabase() throws SQLException {

        try (Statement statement = connection.createStatement()) {

            statement.execute("PRAGMA journal_mode=WAL");
            statement.execute("PRAGMA foreign_keys=ON");
            statement.execute("PRAGMA busy_timeout=5000");
            statement.execute("PRAGMA synchronous=NORMAL");
        }
    }

    /**
     * Creates the database tables if they do not exist.
     *
     * Existing database structure is preserved.
     */
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

    /**
     * Adds a plain title to a player.
     *
     * Formatting is never stored in this table.
     */
    public synchronized boolean addTitle(
            UUID uuid,
            String id,
            String text
    ) throws SQLException {

        validatePlayer(uuid);
        validateValue(id, "Title ID");
        validateValue(text, "Title text");

        ensureOpen();

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

    /**
     * Removes one title and its active selection if necessary.
     *
     * Both operations are performed in one transaction.
     */
    public synchronized boolean removeTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        validatePlayer(uuid);
        validateValue(id, "Title ID");

        ensureOpen();

        boolean previousAutoCommit = connection.getAutoCommit();

        try {

            connection.setAutoCommit(false);

            int removed;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                 DELETE FROM player_titles
                                 WHERE uuid=? AND title_id=?
                                 """
                         )) {

                statement.setString(1, uuid.toString());
                statement.setString(2, id);

                removed = statement.executeUpdate();
            }

            if (removed > 0) {

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     """
                                     DELETE FROM active_titles
                                     WHERE uuid=? AND title_id=?
                                     """
                             )) {

                    statement.setString(1, uuid.toString());
                    statement.setString(2, id);

                    statement.executeUpdate();
                }
            }

            connection.commit();

            return removed > 0;

        } catch (SQLException exception) {

            rollbackQuietly();
            throw exception;

        } finally {

            connection.setAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Removes all titles and active selection from a player.
     *
     * Returns the number of removed titles.
     */
    public synchronized int removeAll(
            UUID uuid
    ) throws SQLException {

        validatePlayer(uuid);
        ensureOpen();

        boolean previousAutoCommit = connection.getAutoCommit();

        try {

            connection.setAutoCommit(false);

            int count;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                 DELETE FROM player_titles
                                 WHERE uuid=?
                                 """
                         )) {

                statement.setString(1, uuid.toString());

                count = statement.executeUpdate();
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                 DELETE FROM active_titles
                                 WHERE uuid=?
                                 """
                         )) {

                statement.setString(1, uuid.toString());

                statement.executeUpdate();
            }

            connection.commit();

            return count;

        } catch (SQLException exception) {

            rollbackQuietly();
            throw exception;

        } finally {

            connection.setAutoCommit(previousAutoCommit);
        }
    }

    /**
     * Gets all titles owned by a player.
     *
     * Map key   = title ID
     * Map value = plain title text
     */
    public synchronized Map<String, String> getTitles(
            UUID uuid
    ) throws SQLException {

        validatePlayer(uuid);
        ensureOpen();

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

    /**
     * Gets one plain title by ID.
     */
    public synchronized String getTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        validatePlayer(uuid);
        validateValue(id, "Title ID");

        ensureOpen();

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

    /**
     * Gets the active title ID of a player.
     */
    public synchronized String getActiveId(
            UUID uuid
    ) throws SQLException {

        validatePlayer(uuid);
        ensureOpen();

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             SELECT title_id
                             FROM active_titles
                             WHERE uuid=?
                             """
                     )) {

            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {

                return rs.next()
                        ? rs.getString("title_id")
                        : null;
            }
        }
    }

    /**
     * Sets the active title.
     *
     * The title must already belong to the player.
     * Passing null resets the active title.
     */
    public synchronized void setActive(
            UUID uuid,
            String id
    ) throws SQLException {

        validatePlayer(uuid);
        ensureOpen();

        if (id == null) {

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 """
                                 DELETE FROM active_titles
                                 WHERE uuid=?
                                 """
                         )) {

                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }

            return;
        }

        validateValue(id, "Title ID");

        /*
         * Prevent active titles that are not owned by the player.
         */
        if (!ownsTitle(uuid, id)) {

            throw new SQLException(
                    "Cannot select a title that the player does not own!"
            );
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

    /**
     * Checks whether a player owns a title.
     */
    private boolean ownsTitle(
            UUID uuid,
            String id
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             SELECT 1
                             FROM player_titles
                             WHERE uuid=? AND title_id=?
                             LIMIT 1
                             """
                     )) {

            statement.setString(1, uuid.toString());
            statement.setString(2, id);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Counts all titles owned by a player.
     */
    public synchronized int countTitles(
            UUID uuid
    ) throws SQLException {

        validatePlayer(uuid);
        ensureOpen();

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             """
                             SELECT COUNT(*)
                             FROM player_titles
                             WHERE uuid=?
                             """
                     )) {

            statement.setString(1, uuid.toString());

            try (ResultSet rs = statement.executeQuery()) {

                return rs.next()
                        ? rs.getInt(1)
                        : 0;
            }
        }
    }

    /**
     * Rolls back a failed transaction without hiding
     * the original database exception.
     */
    private void rollbackQuietly() {

        try {

            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }

        } catch (SQLException exception) {

            plugin.getLogger().severe(
                    "SQLite rollback error: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Checks whether the database connection is available.
     */
    private void ensureOpen() throws SQLException {

        if (connection == null || connection.isClosed()) {

            throw new SQLException(
                    "SQLite database connection is closed!"
            );
        }
    }

    /**
     * Validates player UUID.
     */
    private void validatePlayer(UUID uuid) throws SQLException {

        if (uuid == null) {

            throw new SQLException(
                    "Player UUID cannot be null!"
            );
        }
    }

    /**
     * Validates required text values.
     */
    private void validateValue(
            String value,
            String field
    ) throws SQLException {

        if (value == null || value.isBlank()) {

            throw new SQLException(
                    field + " cannot be empty!"
            );
        }
    }

    /**
     * Closes the SQLite connection.
     */
    public synchronized void close() {

        if (connection == null) {
            return;
        }

        try {

            if (!connection.isClosed()) {
                connection.close();
            }

        } catch (SQLException exception) {

            plugin.getLogger().warning(
                    "SQLite close error: "
                            + exception.getMessage()
            );

        } finally {

            connection = null;
        }
    }
}
