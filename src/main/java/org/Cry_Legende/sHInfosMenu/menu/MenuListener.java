package org.Cry_Legende.sHInfosMenu.menu;

import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    private final SHInfosMenu plugin;

    public MenuListener(SHInfosMenu plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        // Le menu est reconnu par son holder : le titre vient de la config et peut contenir n'importe quoi
        if (!(event.getView().getTopInventory().getHolder() instanceof InfosHolder holder)) {
            return;
        }

        // Tout clic est annulé tant que le menu est ouvert, y compris dans l'inventaire du joueur
        // (shift-clic, double-clic, bundle) : rien ne doit entrer ni sortir du menu.
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)
            || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        int clickedSlot = event.getSlot();
        OfflinePlayer target = holder.getTarget();
        FileConfiguration menuConfig = plugin.getMenuConfig();

        // Retrouver l'item cliqué dans la config pour exécuter les commandes
        for (String key : menuConfig.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key;
            boolean isCorrectSlot = false;

            if (menuConfig.contains(path + ".slot") && menuConfig.getInt(path + ".slot") == clickedSlot) {
                isCorrectSlot = true;
            } else if (menuConfig.getIntegerList(path + ".slots").contains(clickedSlot)) {
                isCorrectSlot = true;
            }

            if (isCorrectSlot) {
                if (menuConfig.contains(path + ".click_commands")) {
                    for (String command : menuConfig.getStringList(path + ".click_commands")) {
                        plugin.getMenuManager().executeCommand(player, target, command);
                    }
                }
                break; // On a trouvé l'item, on arrête la boucle
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof InfosHolder) {
            event.setCancelled(true);
        }
    }
}
