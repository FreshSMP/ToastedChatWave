package me.serbob.toastedchatwave;

import me.serbob.toastedchatwave.Commands.ChatWaveCommand;
import me.serbob.toastedchatwave.Listeners.priorities.*;
import me.serbob.toastedchatwave.TabCompleters.ChatwaveTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ToastedChatWave extends JavaPlugin {
    public static ToastedChatWave instance;
    private YamlConfiguration config;

    @Override
    public void onEnable() {
        instance=this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        String eventPriority = getConfig().getString("chat_listener_priority");
        if(eventPriority==null) {
            getServer().getPluginManager().registerEvents(new HighestPriorityChatWave(), this);
        } else {
            registerEventHandlers(eventPriority);
        }

        getCommand("wave").setExecutor(new ChatWaveCommand());
        getCommand("wave").setTabCompleter(new ChatwaveTabCompleter());
        registerPermissions();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEventHandlers(String eventPriority) {
        switch (eventPriority.toLowerCase()) {
            case "lowest":
                getServer().getPluginManager().registerEvents(new LowestPriorityChatWave(), this);
                break;
            case "low":
                getServer().getPluginManager().registerEvents(new LowPriorityChatWave(), this);
                break;
            case "normal":
                getServer().getPluginManager().registerEvents(new NormalPriorityChatWave(), this);
                break;
            case "high":
                getServer().getPluginManager().registerEvents(new HighPriorityChatWave(), this);
                break;
            case "monitor":
                getServer().getPluginManager().registerEvents(new MonitorPriorityChatWave(), this);
                break;
            default:
                getServer().getPluginManager().registerEvents(new HighestPriorityChatWave(), this);
        }
    }

    public void registerPermissions() {
        for(String key:config.getStringList("permissions")) {
            String permissionName = "wave.reward."+key;
            if(getServer().getPluginManager().getPermission(permissionName)==null) {
                Permission permission = new Permission(permissionName);
                getServer().getPluginManager().addPermission(permission);
            }
        }

        Bukkit.getLogger().info("Permissions reigstered!");
    }
}
