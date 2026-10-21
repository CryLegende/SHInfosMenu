package org.Cry_Legende.sHInfosMenu.menu;

import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuListener implements Listener {

    private final SHInfosMenu plugin;
    private final String rawTitlePattern;

    public MenuListener(SHInfosMenu plugin) {
        this.plugin = plugin;
        // On prend le titre brut SANS les codes couleurs et on remplace le placeholder par une regex pour TOUT capturer.
        // C'est beaucoup plus fiable que de matcher les codes couleurs.
        String rawTitle = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
            plugin.getMenuConfig().getString("menu_title", "Infos de %player_name%")));
        this.rawTitlePattern = rawTitle.replace("%player_name%", "(.+)");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        // On compare les titres SANS les codes couleurs pour être sûr que ça fonctionne toujours
        String viewTitle = ChatColor.stripColor(event.getView().getTitle());
        Pattern pattern = Pattern.compile(this.rawTitlePattern);
        Matcher matcher = pattern.matcher(viewTitle);

        // Si le titre de l'inventaire ne correspond pas au format, on ignore.
        if (!matcher.matches()) {
            return;
        }

        // C'EST LA LIGNE LA PLUS IMPORTANTE : elle est maintenant au bon endroit.
        // Elle empêche le joueur de prendre ou de déplacer l'item.
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        int clickedSlot = event.getSlot();

        // On récupère le nom du joueur capturé par la regex dans le titre
        String targetName = matcher.group(1).trim();
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Si le joueur cible n'est pas valide (par exemple, si le nom était vide), on arrête
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            plugin.getLogger().warning("Impossible de trouver le joueur cible '" + targetName + "' à partir du titre du menu.");
            return;
        }

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
}
