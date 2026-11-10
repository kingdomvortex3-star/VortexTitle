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

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File database = new File(
                    getDataFolder(),
                    "titles.db"
            );

            storage = new TitleStorage(this, database);

            titleManager = new TitleManager(
                    this,
                    storage
            );

            titleGUI = new TitleGUI(
                    this,
                    titleManager
            );

            TitleCommand executor = new TitleCommand(
                    this,
                    titleManager,
                    titleGUI
            );

            PluginCommand command = getCommand("title");

            if (command == null) {
                throw new IllegalStateException(
                        "Title command not registered!"
                );
            }

            command.setExecutor(executor);
            command.setTabCompleter(executor);

            if (getServer().getPluginManager()
                    .getPlugin("PlaceholderAPI") != null) {

                expansion = new TitleExpansion(
                        this,
                        titleManager
                );

                expansion.register();

                getLogger().info(
                        "PlaceholderAPI connected!"
                );
            }

            getLogger().info(
                    "VortexTitle enabled successfully!"
            );

        } catch (Exception exception) {

            getLogger().severe(
                    "Failed to enable VortexTitle!"
            );

            exception.printStackTrace();

            getServer().getPluginManager()
                    .disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        if (expansion != null) {
            expansion.unregister();
        }

        if (storage != null) {
            storage.close();
        }

        getLogger().info(
                "VortexTitle disabled!"
        );
    }

    public void reloadVortexTitle() {

        reloadConfig();

        if (titleGUI != null) {
            titleGUI.reloadSettings();
        }
    }

    public TitleManager getTitleManager() {
        return titleManagerpackage id.vortextitle;

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

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File database = new File(
                    getDataFolder(),
                    "titles.db"
            );

            storage = new TitleStorage(this, database);

            titleManager = new TitleManager(
                    this,
                    storage
            );

            titleGUI = new TitleGUI(
                    this,
                    titleManager
            );

            TitleCommand executor = new TitleCommand(
                    this,
                    titleManager,
                    titleGUI
            );

            PluginCommand command = getCommand("title");

            if (command == null) {
                throw new IllegalStateException(
                        "Title command not registered!"
                );
            }

            command.setExecutor(executor);
            command.setTabCompleter(executor);

            if (getServer().getPluginManager()
                    .getPlugin("PlaceholderAPI") != null) {

                expansion = new TitleExpansion(
                        this,
                        titleManager
                );

                expansion.register();

                getLogger().info(
                        "PlaceholderAPI connected!"
                );
            }

            getLogger().info(
                    "VortexTitle enabled successfully!"
            );

        } catch (Exception exception) {

            getLogger().severe(
                    "Failed to enable VortexTitle!"
            );

            exception.printStackTrace();

            getServer().getPluginManager()
                    .disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

        if (expansion != null) {
            expansion.unregister();
        }

        if (storage != null) {
            storage.close();
        }

        getLogger().info(
                "VortexTitle disabled!"
        );
    }

    public void reloadVortexTitle() {

        reloadConfig();

        if (titleGUI != null) {
            titleGUI.reloadSettings();
        }
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }
              };
    }
              }
