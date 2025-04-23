package me.serbob.toastedchatwave;

import me.serbob.toastedchatwave.Commands.ChatWaveCommand;
import me.serbob.toastedchatwave.Listeners.priorities.*;
import me.serbob.toastedchatwave.Metrics.Metrics;
import me.serbob.toastedchatwave.TabCompleters.ChatwaveTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class ToastedChatWave extends JavaPlugin {
    public static ToastedChatWave instance;
    private YamlConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        String eventPriority = getConfig().getString("chat_listener_priority");
        if (eventPriority == null) {
            getServer().getPluginManager().registerEvents(new HighestPriorityChatWave(), this);
        } else {
            registerEventHandlers(eventPriority);
        }

        getCommand("wave").setExecutor(new ChatWaveCommand());
        getCommand("wave").setTabCompleter(new ChatwaveTabCompleter());
        registerPermissions();
        enableMetrics();
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
        for (String key:config.getStringList("permissions")) {
            String permissionName = "wave.reward." + key;
            if (getServer().getPluginManager().getPermission(permissionName)==null) {
                Permission permission = new Permission(permissionName);
                getServer().getPluginManager().addPermission(permission);
            }
        }

        Bukkit.getLogger().info("Permissions successfully registered!");
    }

    public void enableMetrics() {
        Metrics metrics = new Metrics(this,19314);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
        metrics.addCustomChart(new Metrics.DrilldownPie("java_version", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            String javaVersion = System.getProperty("java.version");
            Map<String, Integer> entry = new HashMap<>();
            entry.put(javaVersion, 1);
            if (javaVersion.startsWith("1.7")) {
                map.put("Java 1.7", entry);
            } else if (javaVersion.startsWith("1.8")) {
                map.put("Java 1.8", entry);
            } else if (javaVersion.startsWith("1.9")) {
                map.put("Java 1.9", entry);
            } else {
                map.put("Other", entry);
            }
            return map;
        }));
    }
}
