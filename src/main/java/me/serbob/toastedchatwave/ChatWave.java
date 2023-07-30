package me.serbob.toastedchatwave;

import me.serbob.toastedchatwave.Util.ChatwaveUtil;
import me.serbob.toastedchatwave.Util.TierUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatWave implements CommandExecutor, Listener {
    private static boolean isActive=false;
    private static List<Player> playersReceived = new ArrayList<>();
    private final ToastedChatWave plugin;
    public ChatWave(ToastedChatWave plugin) {
        this.plugin=plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) {
            sender.sendMessage(ChatColor.RED + "Usage: /wave <reload/start>");
            return false;
        }
        if(args[0].equalsIgnoreCase("start")) {
            if(!sender.hasPermission("wave.start")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
            if (isActive) {
                sender.sendMessage(ChatColor.RED + "Chat wave is already active");
                for(String message: plugin.getConfig().getStringList("wave-already-started")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        sender.sendMessage(ChatwaveUtil.c(message));
                    }
                }
                return true;
            }
            if(sender instanceof Player) {
                for(String message: plugin.getConfig().getStringList("wave-command-started")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        sender.sendMessage(ChatwaveUtil.c(message));
                    }
                }
            }
            isActive = true;
            playersReceived.clear();
            for(String message: plugin.getConfig().getStringList("wave-started")) {
                if(!message.equalsIgnoreCase("NONE")) {
                    Bukkit.broadcastMessage(ChatwaveUtil.c(message));
                }
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                isActive = false;
                for(String message: plugin.getConfig().getStringList("wave-ended")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        Bukkit.broadcastMessage(ChatwaveUtil.c(message));
                    }
                }
            }, 20L * plugin.getConfig().getInt("wave-length"));
        } else if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("wave.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            plugin.reloadConf();
            for(String message: plugin.getConfig().getStringList("wave-reloaded")) {
                if(!message.equalsIgnoreCase("NONE")) {
                    sender.sendMessage(ChatwaveUtil.c(message));
                }
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if(!isActive) {
            return;
        }
        if(!ChatColor.stripColor(message).equalsIgnoreCase(plugin.getConfig().getString("word"))) {
            return;
        }
        if(playersReceived.contains(player)) {
            return;
        }
        for(String msg: plugin.getConfig().getStringList("reward-messages")) {
            if (!msg.equalsIgnoreCase("NONE")) {
                player.sendMessage(ChatwaveUtil.c(msg));
            }
        }
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(String msg: plugin.getConfig().getStringList("reward-commands."+ TierUtils.getHighestTier(player))) {
                    if(!msg.equalsIgnoreCase("NONE")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg
                                .replace("{player}",player.getName()));
                    }
                }
            }
        });
        playersReceived.add(player);
    }
}
