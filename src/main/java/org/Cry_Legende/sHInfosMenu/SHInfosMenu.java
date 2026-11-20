package org.Cry_Legende.sHInfosMenu;

import org.Cry_Legende.sHInfosMenu.commands.AdminCommand;
import org.Cry_Legende.sHInfosMenu.commands.InfosCommand;
import org.Cry_Legende.sHInfosMenu.menu.MenuListener;
import org.Cry_Legende.sHInfosMenu.menu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class SHInfosMenu extends JavaPlugin {

    private MenuManager menuManager;
    private FileConfiguration settings;
    private FileConfiguration menuConfig;
    private FileConfiguration messages;

    /** Erreur de lecture d'un fichier de configuration : nom du fichier et cause. */
    public static final class ConfigException extends Exception {
        private final String file;

        ConfigException(String file, String message) {
            super(message);
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }

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
        saveResourceIfAbsent("menu.yml");
        saveMessagesIfAbsent();
        try {
            loadFiles();
        } catch (ConfigException e) {
            getLogger().severe("Fichier " + e.getFile() + " illisible, valeurs du jar utilisées : " + e.getMessage());
            settings = bundled("config.yml");
            menuConfig = bundled("menu.yml");
            messages = bundled("messages.yml");
        }

        this.menuManager = new MenuManager(this);

        // Enregistrement des commandes et des événements
        getCommand("infos").setExecutor(new InfosCommand(this));
        PluginCommand admin = getCommand("shinfosmenu");
        if (admin != null) {
            AdminCommand executor = new AdminCommand(this);
            admin.setExecutor(executor);
            admin.setTabCompleter(executor);
        }
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);

    }

    @Override
    public void onDisable() {
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    /** config.yml (réglages), tel que lu par le dernier chargement réussi. */
    @Override
    public FileConfiguration getConfig() {
        return settings != null ? settings : super.getConfig();
    }

    public FileConfiguration getMenuConfig() {
        return menuConfig;
    }

    /** Texte de messages.yml, couleurs appliquées ; les paires de remplacements sont (placeholder, valeur). */
    public String message(String key, String... replacements) {
        String text = messages == null ? null : messages.getString(key);
        if (text == null) {
            return ChatColor.RED + "Message manquant : " + key;
        }
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            text = text.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Relit config.yml, menu.yml et messages.yml. Rien n'est remplacé tant que les trois fichiers ne sont pas lus sans
     * erreur : sinon l'ancienne configuration reste en place.
     */
    public void loadFiles() throws ConfigException {
        FileConfiguration newSettings = readYaml("config.yml");
        FileConfiguration newMenu = readYaml("menu.yml");
        FileConfiguration newMessages = readYaml("messages.yml");
        settings = newSettings;
        menuConfig = newMenu;
        messages = newMessages;
    }

    /** Fichier du dossier du plugin lu strictement (erreur de syntaxe signalée), complété par les valeurs du jar. */
    private YamlConfiguration readYaml(String name) throws ConfigException {
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.load(new File(getDataFolder(), name));
        } catch (IOException | InvalidConfigurationException e) {
            String reason = e.getMessage() == null ? e.toString() : e.getMessage().lines().findFirst().orElse("");
            throw new ConfigException(name, reason);
        }
        yaml.setDefaults(bundled(name));
        return yaml;
    }

    private YamlConfiguration bundled(String name) {
        try (InputStream in = getResource(name)) {
            return in == null ? new YamlConfiguration()
                : YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new YamlConfiguration();
        }
    }

    private void saveResourceIfAbsent(String name) {
        if (!new File(getDataFolder(), name).exists()) {
            saveResource(name, false);
        }
    }

    /**
     * Premier démarrage avec ce fichier : messages.yml est créé à partir de la section messages de config.yml
     * (textes déjà en place), complétée par les textes du jar. La section de config.yml n'est plus lue ensuite.
     */
    private void saveMessagesIfAbsent() {
        File file = new File(getDataFolder(), "messages.yml");
        if (file.exists()) {
            return;
        }
        saveResource("messages.yml", false);
        ConfigurationSection old = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"))
            .getConfigurationSection("messages");
        if (old == null) {
            return;
        }
        YamlConfiguration created = YamlConfiguration.loadConfiguration(file);
        for (String key : old.getKeys(false)) {
            if (old.isString(key)) {
                created.set(key, old.getString(key));
            }
        }
        try {
            created.save(file);
            getLogger().info("Messages repris de config.yml dans messages.yml (la section messages de config.yml ne sert plus).");
        } catch (IOException e) {
            getLogger().warning("messages.yml non écrit : " + e.getMessage());
        }
    }
}
