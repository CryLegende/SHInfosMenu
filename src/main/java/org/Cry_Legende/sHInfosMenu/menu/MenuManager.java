package org.Cry_Legende.sHInfosMenu.menu;

import me.clip.placeholderapi.PlaceholderAPI;
import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MenuManager {

    private final SHInfosMenu plugin;

    public MenuManager(SHInfosMenu plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player viewer, OfflinePlayer target) {
        FileConfiguration menuConfig = plugin.getMenuConfig();

        String title = menuConfig.getString("menu_title", "Infos de %player_name%");
        title = PlaceholderAPI.setPlaceholders(target, title);
        title = ChatColor.translateAlternateColorCodes('&', title);

        int size = menuConfig.getInt("size", 27);

        InfosHolder holder = new InfosHolder(target);
        Inventory menu = Bukkit.createInventory(holder, size, title);
        holder.setInventory(menu);

        // Parcourir les items définis dans menu.yml
        for (String key : menuConfig.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key;

            // Matériau
            String materialString = menuConfig.getString(path + ".material", "STONE");
            materialString = PlaceholderAPI.setPlaceholders(target, materialString);

            ItemStack item;
            if (materialString.toLowerCase().startsWith("head-")) {
                item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                skullMeta.setOwningPlayer(target);
                item.setItemMeta(skullMeta);
            } else {
                try {
                    item = new ItemStack(Material.valueOf(materialString.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Matériau invalide '" + materialString + "' dans menu.yml pour l'item '" + key + "'. Utilisation de STONE par défaut.");
                    item = new ItemStack(Material.STONE);
                }
            }

            ItemMeta meta = item.getItemMeta();

            // Nom de l'item
            String displayName = menuConfig.getString(path + ".display_name", "");
            if (!displayName.isEmpty()) {
                displayName = PlaceholderAPI.setPlaceholders(target, displayName);
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            }

            // Lore de l'item
            List<String> lore = menuConfig.getStringList(path + ".lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = lore.stream()
                    .map(line -> PlaceholderAPI.setPlaceholders(target, line))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }

            item.setItemMeta(meta);

            // Placer l'item dans les slots
            if (menuConfig.contains(path + ".slot")) {
                int slot = menuConfig.getInt(path + ".slot");
                if (slot >= 0 && slot < size) {
                    menu.setItem(slot, item);
                }
            } else if (menuConfig.contains(path + ".slots")) {
                for (int slot : menuConfig.getIntegerList(path + ".slots")) {
                    if (slot >= 0 && slot < size) {
                        menu.setItem(slot, item);
                    }
                }
            }
        }

        viewer.openInventory(menu);

        // Exécuter les commandes d'ouverture
        for (String cmd : menuConfig.getStringList("open_commands")) {
            executeCommand(viewer, target, cmd);
        }
    }

    /** Son lu en config : ancien nom (ENTITY_PLAYER_LEVELUP) ou clé (minecraft:entity.player.levelup). */
    static Sound sound(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.trim();
        if (s.indexOf(':') >= 0 || s.indexOf('.') >= 0) {
            NamespacedKey key = NamespacedKey.fromString(s.toLowerCase(Locale.ROOT));
            return key == null ? null : Registry.SOUND_EVENT.get(key);
        }
        try {
            return (Sound) Sound.class.getField(s.toUpperCase(Locale.ROOT)).get(null);
        } catch (ReflectiveOperationException | ClassCastException e) {
            return null;
        }
    }

    // Méthode pour exécuter les commandes (utilisée aussi par le Listener)
    public void executeCommand(Player viewer, OfflinePlayer target, String command) {
        command = PlaceholderAPI.setPlaceholders(target, command);

        if (command.startsWith("[player] ")) {
            viewer.performCommand(command.substring(9));
        } else if (command.startsWith("[console] ")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(10));
        } else if (command.equalsIgnoreCase("[close]")) {
            viewer.closeInventory();
        } else if (command.toLowerCase().startsWith("[sound] ")) {
            Sound sound = sound(command.substring(8));
            if (sound != null) {
                viewer.playSound(viewer.getLocation(), sound, 1.0f, 1.0f);
            } else {
                plugin.getLogger().warning("Son invalide: " + command.substring(8));
            }
        } else {
            // Par défaut, exécuter en tant que console
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
