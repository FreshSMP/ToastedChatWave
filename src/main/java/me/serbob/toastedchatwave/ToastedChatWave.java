package me.serbob.toastedchatwave;

import me.serbob.toastedchatwave.TabCompleters.ChatwaveTabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ToastedChatWave extends JavaPlugin {
    private File configFile;
    private YamlConfiguration config;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        getServer().getPluginManager().registerEvents(new ChatWave(this),this);
        getCommand("wave").setExecutor(new ChatWave(this));
        getCommand("wave").setTabCompleter(new ChatwaveTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public void reloadConf() {
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        try {
            config.save(configFile);
            config.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
