package me.serbob.toastedchatwave.Commands;

import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatUtil;
import me.serbob.toastedchatwave.Util.folia.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import static me.serbob.toastedchatwave.Managers.WaveManager.*;

public class ChatWaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatUtil.c("&cInvalid arguments."));
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                return handleStartCommand(sender, args);
            case "reload":
                return handleReloadCommand(sender);
            default:
                sender.sendMessage(ChatUtil.c("&cUnknown subcommand."));
                return false;
        }
    }

    private boolean handleStartCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("wave.start")) {
            sendNoPermissionMessage(sender);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatUtil.c("&cUsage: /wave start <wave>"));
            return false;
        }

        if (isActive) {
            sendMessages(sender, "wave-already-started");
            return true;
        }

        String waveName = args[1];
        if (ToastedChatWave.instance.getConfig().get("waves." + waveName) == null) {
            sender.sendMessage(ChatUtil.c("&cInvalid wave!"));
            return false;
        }

        if (sender instanceof Player) {
            sendMessages(sender, "wave-command-started");
        }

        startWave(waveName);
        return true;
    }

    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("wave.reload")) {
            sendNoPermissionMessage(sender);
            return false;
        }

        reloadConfiguration();
        sendMessages(sender, "wave-reloaded");
        return true;
    }

    private void reloadConfiguration() {
        try {
            Path tempPath = Files.createTempFile("config", ".yml");
            File tempFile = tempPath.toFile();
            tempFile.deleteOnExit();

            try (InputStream input = ToastedChatWave.instance.getResource("config.yml")) {
                Files.copy(input, tempPath, StandardCopyOption.REPLACE_EXISTING);
            }

            FileConfiguration newConfig = YamlConfiguration.loadConfiguration(tempFile);
            ToastedChatWave.instance.reloadConfig();
            ToastedChatWave.instance.getConfig().setDefaults(newConfig);
            ToastedChatWave.instance.saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadConfigValues();
    }

    private void loadConfigValues() {
        // To any contributor reading this...
        // If you want to change anything, please use managers,
        // then load them inside onEnable and put them here too for reload...
    }

    private void startWave(String waveName) {
        isActive = true;
        currentWave = waveName;
        playersReceived.clear();

        sendBroadcastMessages("waves." + currentWave + ".wave-started");

        FoliaScheduler.getAsyncScheduler().runDelayed(ToastedChatWave.instance, $ -> {
            isActive = false;
            sendBroadcastMessages("waves." + currentWave + ".wave-ended");
        }, 20L * ToastedChatWave.instance.getConfig().getInt("waves." + currentWave + ".wave-length") * 50, TimeUnit.MILLISECONDS);
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(ChatUtil.c("&cYou do not have permission to use this command!"));
    }

    private void sendMessages(CommandSender sender, String path) {
        for (String message : ToastedChatWave.instance.getConfig().getStringList(path)) {
            if (!message.equalsIgnoreCase("NONE")) {
                sender.sendMessage(ChatUtil.c(message));
            }
        }
    }

    private void sendBroadcastMessages(String path) {
        for (String message : ToastedChatWave.instance.getConfig().getStringList(path)) {
            if (!message.equalsIgnoreCase("NONE")) {
                Bukkit.broadcastMessage(ChatUtil.c(message));
            }
        }
    }
}