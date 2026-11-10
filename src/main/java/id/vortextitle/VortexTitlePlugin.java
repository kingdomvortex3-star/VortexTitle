
package id.vortextitle;

import id.vortextitle.command.TitleCommand;
import id.vortextitle.gui.TitleGUI;
import id.vortextitle.manager.TitleManager;
import id.vortextitle.placeholder.TitleExpansion;
import id.vortextitle.storage.TitleStorage;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VortexTitlePlugin extends JavaPlugin {

    private TitleStorage storage;
    private TitleManager titleManager;
    private TitleGUI titleGUI;
    private TitleExpansion expansion;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        try {

            if (!getDataFolder().exists()
                    && !getDataFolder().mkdirs()) {

                throw new IllegalStateException(
                        "Could not create plugin data folder!"
                );
            }

            File database = new File(
                    getDataFolder(),
                    "titles.db"
            );

            storage = new TitleStorage(
                    this,
                    database
            );

            titleManager = new TitleManager(
                    this,
                    storage
            );

            titleGUI = new TitleGUI(
                    this,
                    titleManager
            );

            registerCommands();
            registerPlaceholderAPI();

            getLogger().info("--------------------------------");
            getLogger().info("Vortex Title has been enabled!");
            getLogger().info("Developer: Vortex Teams");
            getLogger().info("Database: SQLite");
            getLogger().info("Title Formatting: Enabled");
            getLogger().info("--------------------------------");

        } catch (Exception exception) {

            getLogger().severe(
                    "Failed to enable Vortex Title!"
            );

            exception.printStackTrace();

            getServer().getPluginManager()
                    .disablePlugin(this);
        }
    }

    /**
     * Registers the /title command and tab completion.
     */
    private void registerCommands() {

        PluginCommand command = getCommand("title");

        if (command == null) {

            throw new IllegalStateException(
                    "Command 'title' is missing from plugin.yml!"
            );
        }

        TitleCommand executor = new TitleCommand(
                this,
                titleManager,
                titleGUI
        );

        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    /**
     * Registers PlaceholderAPI integration if available.
     */
    private void registerPlaceholderAPI() {

        if (getServer().getPluginManager()
                .getPlugin("PlaceholderAPI") == null) {

            getLogger().info(
                    "PlaceholderAPI not found. Placeholder integration disabled."
            );

            return;
        }

        expansion = new TitleExpansion(
                this,
                titleManager
        );

        if (!expansion.register()) {

            expansion = null;

            getLogger().warning(
                    "Could not register PlaceholderAPI expansion!"
            );

            return;
        }

        getLogger().info(
                "PlaceholderAPI integration enabled!"
        );
    }

    @Override
    public void onDisable() {

        if (expansion != null) {

            expansion.unregister();
            expansion = null;
        }

        if (storage != null) {

            try {

                storage.close();

            } catch (Exception exception) {

                getLogger().severe(
                        "Could not close SQLite database: "
                                + exception.getMessage()
                );
            }

            storage = null;
        }

        getLogger().info(
                "Vortex Title has been disabled!"
        );
    }

    /**
     * Reloads Vortex Title configuration.
     *
     * Title formatting is read directly from the latest config,
     * so no database modification or server restart is required.
     */
    public void reloadVortexTitle() {

        reloadConfig();

        if (titleGUI != null) {
            titleGUI.reloadSettings();
        }

        getLogger().info(
                "Configuration successfully reloaded!"
        );
    }

    /**
     * Gets the title manager.
     */
    public TitleManager getTitleManager() {
        return titleManager;
    }
}
