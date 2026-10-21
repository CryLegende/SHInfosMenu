package org.Cry_Legende.sHInfosMenu.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfosCommand implements CommandExecutor {

    private final SHInfosMenu plugin;

    public InfosCommand(SHInfosMenu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.console-not-allowed", "&cCette commande ne peut être exécutée que par un joueur.")));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shinfosmenu.use")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission", "&cPermission refusée.")));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.usage", "&cUsage: /infos <joueur>")));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Vérifier si le joueur est dans la blacklist
        if (plugin.getConfig().getStringList("blacklist").stream().anyMatch(name -> name.equalsIgnoreCase(targetName))) {
            String message = plugin.getConfig().getString("messages.player-blacklisted", "&cVous ne pouvez pas consulter les informations de ce joueur.");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        // Vérifier si le joueur a déjà joué sur le serveur
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            String message = plugin.getConfig().getString("messages.player-not-found", "&cLe joueur '%target%' n'a pas été trouvé.").replace("%target%", targetName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        // Vérifier si le joueur doit être en ligne
        if (plugin.getConfig().getBoolean("check-if-online") && !target.isOnline()) {
            String message = plugin.getConfig().getString("messages.player-offline", "&cLe joueur '%target%' est actuellement hors ligne.").replace("%target%", targetName);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }

        // Ouvrir le menu
        plugin.getMenuManager().openMenu(player, target);
        return true;
    }
}