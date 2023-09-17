package me.serbob.toastedchatwave;

import me.serbob.toastedchatwave.Commands.ChatWaveCommand;
import me.serbob.toastedchatwave.Listeners.ChatWave;
import me.serbob.toastedchatwave.Metrics.Metrics;
import me.serbob.toastedchatwave.TabCompleters.ChatwaveTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public final class ToastedChatWave extends JavaPlugin {
    public static ToastedChatWave instance;
    private File configFile;
    private YamlConfiguration config;
    @Override
    public void onEnable() {
        instance=this;
        saveDefaultConfig();
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        getServer().getPluginManager().registerEvents(new ChatWave(),this);
        getCommand("wave").setExecutor(new ChatWaveCommand());
        getCommand("wave").setTabCompleter(new ChatwaveTabCompleter());
        registerPermissions();
        enableMetrics();
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
    public void registerPermissions() {
        for(String key:config.getStringList("permissions")) {
            String permissionName = "wave.reward."+key;
            if(getServer().getPluginManager().getPermission(permissionName)==null) {
                Permission permission = new Permission(permissionName);
                getServer().getPluginManager().addPermission(permission);
            }
        }
        System.out.println("Permissions reigstered!");
    }
    public void enableMetrics() {
        Metrics metrics = new Metrics(this,19314);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", new Callable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws Exception {
                Map<String, Integer> valueMap = new HashMap<>();
                valueMap.put("servers", 1);
                valueMap.put("players", Bukkit.getOnlinePlayers().size());
                return valueMap;
            }
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
