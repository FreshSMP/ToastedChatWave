package me.serbob.toastedchatwave.TabCompleters;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatwaveTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if(args.length<2) {
            if(sender.hasPermission("wave.reload")) {
                list.add("reload");
            }
            if(sender.hasPermission("wave.start")) {
                list.add("start");
            }
            String letters = args[0].toLowerCase();
            list = list.stream().filter(s -> s.toLowerCase().startsWith(letters)).collect(Collectors.toList());
        } else if(args[0].equalsIgnoreCase("start")) {
            for(String key: ToastedChatWave.instance.getConfig().getConfigurationSection("waves").getKeys(false)) {
                list.add(key);
            }
            String letters = args[1].toLowerCase();
            list = list.stream().filter(s -> s.toLowerCase().startsWith(letters)).collect(Collectors.toList());
        }
        return list;
    }
}
