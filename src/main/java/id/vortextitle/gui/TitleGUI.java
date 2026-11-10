
package id.vortextitle.gui;

import id.vortextitle.VortexTitlePlugin;
import id.vortextitle.manager.TitleFormatter;
import id.vortextitle.manager.TitleManager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.TextReplacementConfig;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TitleGUI implements Listener {

    private final VortexTitlePlugin plugin;
    private final TitleManager manager;

    public TitleGUI(
            VortexTitlePlugin plugin,
            TitleManager manager
    ) {

        this.plugin = plugin;
        this.manager = manager;

        plugin.getServer()
                .getPluginManager()
                .registerEvents(this, plugin);
    }

    public void reloadSettings() {
        // GUI reads the latest configuration directly.
    }

    /**
     * Opens the player's title collection GUI.
     */
    public void open(Player player, int page) {

        try {

            Map<String, String> titles =
                    manager.getTitles(player.getUniqueId());

            List<Map.Entry<String, String>> entries =
                    new ArrayList<>(titles.entrySet());

            int rows = Math.max(
                    1,
                    Math.min(
                            6,
                            plugin.getConfig().getInt("gui.rows", 6)
                    )
            );

            int size = rows * 9;

            List<Integer> slots = new ArrayList<>(
                    plugin.getConfig().getIntegerList(
                            "gui.title-slots"
                    )
            );

            slots.removeIf(slot ->
                    slot == null || slot < 0 || slot >= size
            );

            if (slots.isEmpty()) {

                slots = new ArrayList<>();

                for (int slot = 0; slot < size; slot++) {
                    slots.add(slot);
                }
            }

            int previousSlot = plugin.getConfig().getInt(
                    "gui.previous-slot",
                    45
            );

            int closeSlot = plugin.getConfig().getInt(
                    "gui.close-slot",
                    49
            );

            int nextSlot = plugin.getConfig().getInt(
                    "gui.next-slot",
                    53
            );

            int resetSlot = plugin.getConfig().getInt(
                    "gui.reset-slot",
                    47
            );

            /*
             * Do not use navigation slots for title items.
             */
            slots.removeIf(slot ->
                    slot == previousSlot
                            || slot == closeSlot
                            || slot == nextSlot
                            || slot == resetSlot
            );

            if (slots.isEmpty()) {
                player.sendMessage(message("database-error"));
                return;
            }

            int perPage = slots.size();

            int maxPage = Math.max(
                    0,
                    (entries.size() - 1) / perPage
            );

            int safePage = Math.max(
                    0,
                    Math.min(page, maxPage)
            );

            GuiHolder holder = new GuiHolder(
                    player.getUniqueId(),
                    safePage
            );

            String guiTitle = plugin.getConfig().getString(
                    "gui.title",
                    "<aqua>Title Collection"
            );

            Inventory inventory = Bukkit.createInventory(
                    holder,
                    size,
                    TitleFormatter.parse(guiTitle)
            );

            holder.setInventory(inventory);

            /*
             * Filler items.
             */
            if (plugin.getConfig().getBoolean(
                    "gui.filler.enabled",
                    true
            )) {

                Material material = Material.matchMaterial(
                        plugin.getConfig().getString(
                                "gui.filler.material",
                                "BLACK_STAINED_GLASS_PANE"
                        )
                );

                if (material == null || !material.isItem()) {
                    material = Material.BLACK_STAINED_GLASS_PANE;
                }

                ItemStack filler = createItem(
                        material,
                        plugin.getConfig().getString(
                                "gui.filler.name",
                                " "
                        ),
                        List.of()
                );

                for (int i = 0; i < size; i++) {
                    inventory.setItem(i, filler);
                }
            }

            String activeId = manager.getActiveId(
                    player.getUniqueId()
            );

            /*
             * Display owned titles.
             */
            for (int i = 0; i < perPage; i++) {

                int index = safePage * perPage + i;

                if (index >= entries.size()) {
                    break;
                }

                Map.Entry<String, String> entry =
                        entries.get(index);

                String id = entry.getKey();
                String rawTitle = entry.getValue();

                boolean active = activeId != null
                        && id.equalsIgnoreCase(activeId);

                List<Component> lore = new ArrayList<>();

                lore.add(
                        TitleFormatter.parse(
                                active
                                        ? "<green>Currently selected"
                                        : "<yellow>Click to select"
                        )
                );

                lore.add(
                        TitleFormatter.parse(
                                "<dark_gray>ID: <gray>" + id
                        )
                );

                /*
                 * IMPORTANT:
                 * Format the title using title-colors in config.yml.
                 * The database still stores only the plain title.
                 */
                Component formattedTitle =
                        manager.formatTitle(rawTitle);

                inventory.setItem(
                        slots.get(i),
                        createItem(
                                active
                                        ? Material.ENCHANTED_BOOK
                                        : Material.NAME_TAG,
                                formattedTitle,
                                lore
                        )
                );
            }

            /*
             * Previous page.
             */
            if (safePage > 0) {

                setItem(
                        inventory,
                        previousSlot,
                        createItem(
                                Material.ARROW,
                                "<yellow>Previous Page",
                                List.of()
                        )
                );
            }

            /*
             * Close button.
             */
            setItem(
                    inventory,
                    closeSlot,
                    createItem(
                            Material.BARRIER,
                            "<red>Close",
                            List.of()
                    )
            );

            /*
             * Next page.
             */
            if (safePage < maxPage) {

                setItem(
                        inventory,
                        nextSlot,
                        createItem(
                                Material.ARROW,
                                "<yellow>Next Page",
                                List.of()
                        )
                );
            }

            /*
             * Reset active title.
             */
            setItem(
                    inventory,
                    resetSlot,
                    createItem(
                            Material.MILK_BUCKET,
                            "<red>Reset Active Title",
                            List.of(
                                    TitleFormatter.parse(
                                            "<gray>Your collection will not be deleted."
                                    )
                            )
                    )
            );

            player.openInventory(inventory);

        } catch (SQLException exception) {

            plugin.getLogger().severe(
                    "GUI database error: " + exception.getMessage()
            );

            player.sendMessage(message("database-error"));
        }
    }

    /**
     * Handles GUI clicks.
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getView()
                .getTopInventory()
                .getHolder(false) instanceof GuiHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!holder.owner.equals(player.getUniqueId())) {
            return;
        }

        if (event.getClickedInventory() == null
                || event.getClickedInventory()
                != event.getView().getTopInventory()) {
            return;
        }

        int slot = event.getSlot();

        int closeSlot = plugin.getConfig().getInt(
                "gui.close-slot",
                49
        );

        int previousSlot = plugin.getConfig().getInt(
                "gui.previous-slot",
                45
        );

        int nextSlot = plugin.getConfig().getInt(
                "gui.next-slot",
                53
        );

        int resetSlot = plugin.getConfig().getInt(
                "gui.reset-slot",
                47
        );

        /*
         * Close GUI.
         */
        if (slot == closeSlot) {

            player.closeInventory();
            return;
        }

        /*
         * Previous page.
         */
        if (slot == previousSlot) {

            if (holder.page > 0) {
                open(player, holder.page - 1);
            }

            return;
        }

        /*
         * Next page.
         */
        if (slot == nextSlot) {

            open(player, holder.page + 1);
            return;
        }

        /*
         * Reset active title.
         */
        if (slot == resetSlot) {

            try {

                manager.reset(player.getUniqueId());

                player.sendMessage(message("reset"));

                player.playSound(
                        player.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        1f,
                        0.8f
                );

                open(player, holder.page);

            } catch (SQLException exception) {

                plugin.getLogger().severe(
                        "Could not reset active title: "
                                + exception.getMessage()
                );

                player.sendMessage(message("database-error"));
            }

            return;
        }

        /*
         * Read clicked title item.
         */
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();

        List<Component> lore = meta.lore();

        if (lore == null || lore.size() < 2) {
            return;
        }

        String idLine = PlainTextComponentSerializer
                .plainText()
                .serialize(lore.get(1));

        if (!idLine.startsWith("ID: ")) {
            return;
        }

        String id = idLine.substring(4).trim();

        if (id.isEmpty()) {
            return;
        }

        try {

            String rawTitle = manager.getTitle(
                    player.getUniqueId(),
                    id
            );

            if (rawTitle == null) {
                return;
            }

            if (!manager.select(
                    player.getUniqueId(),
                    id
            )) {
                return;
            }

            /*
             * Use the same configured title format in confirmation.
             */
            Component formattedTitle =
                    manager.formatTitle(rawTitle);

            player.sendMessage(
                    message("selected").replaceText(
                            TextReplacementConfig.builder()
                                    .matchLiteral("{title}")
                                    .replacement(formattedTitle)
                                    .build()
                    )
            );

            player.playSound(
                    player.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    1f,
                    1.2f
            );

            open(player, holder.page);

        } catch (SQLException exception) {

            plugin.getLogger().severe(
                    "Could not select title: "
                            + exception.getMessage()
            );

            player.sendMessage(message("database-error"));
        }
    }

    /**
     * Creates an item using a MiniMessage string.
     */
    private ItemStack createItem(
            Material material,
            String name,
            List<Component> lore
    ) {

        return createItem(
                material,
                TitleFormatter.parse(name),
                lore
        );
    }

    /**
     * Creates an item using an already formatted Component.
     */
    private ItemStack createItem(
            Material material,
            Component name,
            List<Component> lore
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        meta.displayName(name);

        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Safely places an item in the inventory.
     */
    private void setItem(
            Inventory inventory,
            int slot,
            ItemStack item
    ) {

        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }

    /**
     * Gets a prefixed message from config.yml.
     */
    private Component message(String key) {

        String prefix = plugin.getConfig().getString(
                "settings.prefix",
                ""
        );

        String text = plugin.getConfig().getString(
                "messages." + key,
                "<red>Missing message: " + key
        );

        return TitleFormatter.parse(prefix + text);
    }

    /**
     * Custom inventory holder.
     */
    private static final class GuiHolder
            implements InventoryHolder {

        private final UUID owner;
        private final int page;

        private Inventory inventory;

        private GuiHolder(
                UUID owner,
                int page
        ) {

            this.owner = owner;
            this.page = page;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
