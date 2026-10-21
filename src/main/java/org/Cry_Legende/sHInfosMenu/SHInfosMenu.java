package org.Cry_Legende.sHInfosMenu;

import org.Cry_Legende.sHInfosMenu.commands.InfosCommand;
import org.Cry_Legende.sHInfosMenu.menu.MenuListener;
import org.Cry_Legende.sHInfosMenu.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public final class SHInfosMenu extends JavaPlugin {

    private MenuManager menuManager;
    private File menuFile;
    private FileConfiguration menuConfig;

    @Override
    public void onEnable() {
        // Vérification de la présence de PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("PlaceholderAPI n'a pas été trouvé. Le plugin SHInfosMenu va se désactiver.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Chargement des configurations
        saveDefaultConfig();
        createMenuConfig();

        this.menuManager = new MenuManager(this);

        // Enregistrement de la commande et des événements
        getCommand("infos").setExecutor(new InfosCommand(this));
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);

        getLogger().info("SHInfosMenu a été activé avec succès !");
    }

    @Override
    public void onDisable() {
        getLogger().info("SHInfosMenu a été désactivé.");
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    // Gestion du fichier menu.yml
    public FileConfiguration getMenuConfig() {
        if (this.menuConfig == null) {
            reloadMenuConfig();
        }
        return this.menuConfig;
    }

    public void reloadMenuConfig() {
        if (this.menuFile == null) {
            this.menuFile = new File(getDataFolder(), "menu.yml");
        }
        this.menuConfig = YamlConfiguration.loadConfiguration(this.menuFile);

        InputStream defaultStream = this.getResource("menu.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.menuConfig.setDefaults(defaultConfig);
        }
    }

    public void saveMenuConfig() {
        if (this.menuConfig == null || this.menuFile == null) {
            return;
        }
        try {
            getMenuConfig().save(this.menuFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Impossible de sauvegarder le fichier menu.yml", e);
        }
    }

    private void createMenuConfig() {
        menuFile = new File(getDataFolder(), "menu.yml");
        if (!menuFile.exists()) {
            saveResource("menu.yml", false);
        }
        menuConfig = YamlConfiguration.loadConfiguration(menuFile);
    }
}