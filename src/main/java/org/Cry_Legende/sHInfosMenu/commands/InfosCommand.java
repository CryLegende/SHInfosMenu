package org.Cry_Legende.sHInfosMenu.commands;

import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.Bukkit;
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
            sender.sendMessage(plugin.message("console-not-allowed"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shinfosmenu.use")) {
            player.sendMessage(plugin.message("no-permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.message("usage"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Vérifier si le joueur est dans la blacklist
        if (plugin.getConfig().getStringList("blacklist").stream().anyMatch(name -> name.equalsIgnoreCase(targetName))) {
            player.sendMessage(plugin.message("player-blacklisted"));
            return true;
        }

        // Vérifier si le joueur a déjà joué sur le serveur
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(plugin.message("player-not-found", "%target%", targetName));
            return true;
        }

        // Vérifier si le joueur doit être en ligne
        if (plugin.getConfig().getBoolean("check-if-online") && !target.isOnline()) {
            player.sendMessage(plugin.message("player-offline", "%target%", targetName));
            return true;
        }

        // Ouvrir le menu
        plugin.getMenuManager().openMenu(player, target);
        return true;
    }
}
