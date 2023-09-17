package me.serbob.toastedchatwave.Commands;

import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatwaveUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.serbob.toastedchatwave.Managers.WaveManager.*;

public class ChatWaveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) {
            sender.sendMessage(ChatColor.RED + "Invalid args");
            return false;
        }
        if(args[0].equalsIgnoreCase("start")) {
            if(!sender.hasPermission("wave.start")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return true;
            }
            if(args.length<2) {
                sender.sendMessage(ChatColor.RED + "Usage: /wave start <wave>");
                return false;
            }
            if (isActive) {
                sender.sendMessage(ChatColor.RED + "Chat wave is already active");
                for(String message: ToastedChatWave.instance.getConfig().getStringList("wave-already-started")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        sender.sendMessage(ChatwaveUtil.c(message));
                    }
                }
                return true;
            }
            if(ToastedChatWave.instance.getConfig().get("waves."+args[1])==null) {
                sender.sendMessage(ChatwaveUtil.c("&cInvalid wave!"));
                return false;
            }
            if(sender instanceof Player) {
                for(String message: ToastedChatWave.instance.getConfig().getStringList("wave-command-started")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        sender.sendMessage(ChatwaveUtil.c(message));
                    }
                }
            }
            isActive = true;
            currentWave = args[1];
            playersReceived.clear();
            for(String message: ToastedChatWave.instance.getConfig().getStringList("waves."+currentWave+".wave-started")) {
                if(!message.equalsIgnoreCase("NONE")) {
                    Bukkit.broadcastMessage(ChatwaveUtil.c(message));
                }
            }
            Bukkit.getScheduler().runTaskLaterAsynchronously(ToastedChatWave.instance, () -> {
                isActive = false;
                for(String message: ToastedChatWave.instance.getConfig().getStringList("waves."+currentWave+".wave-ended")) {
                    if(!message.equalsIgnoreCase("NONE")) {
                        Bukkit.broadcastMessage(ChatwaveUtil.c(message));
                    }
                }
            }, 20L * ToastedChatWave.instance.getConfig().getInt("waves."+currentWave+".wave-length"));
        } else if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("wave.reload")) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
            ToastedChatWave.instance.reloadConf();
            for(String message: ToastedChatWave.instance.getConfig().getStringList("wave-reloaded")) {
                if(!message.equalsIgnoreCase("NONE")) {
                    sender.sendMessage(ChatwaveUtil.c(message));
                }
            }
        }
        return true;
    }
}
