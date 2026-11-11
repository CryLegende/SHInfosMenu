package org.Cry_Legende.sHInfosMenu.commands;

import org.Cry_Legende.sHInfosMenu.SHInfosMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;
import java.util.Locale;

/**
 * /shinfosmenu reload : relit config.yml, menu.yml et messages.yml.
 */
public final class AdminCommand implements TabExecutor {

    private static final String RELOAD = "shinfosmenu.admin.reload";

    private final SHInfosMenu plugin;

    public AdminCommand(SHInfosMenu plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(RELOAD)) {
            sender.sendMessage(plugin.message("no-permission"));
            return true;
        }
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(plugin.message("admin-usage", "%subcommands%", "reload"));
            return true;
        }
        try {
            plugin.loadFiles();
        } catch (SHInfosMenu.ConfigException e) {
            plugin.getLogger().severe("Rechargement annulé, " + e.getFile() + " illisible : " + e.getMessage());
            sender.sendMessage(plugin.message("reload-failed", "%file%", e.getFile(), "%error%", e.getMessage()));
            return true;
        }
        sender.sendMessage(plugin.message("reloaded"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(RELOAD) && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
            return List.of("reload");
        }
        return List.of();
    }
}
