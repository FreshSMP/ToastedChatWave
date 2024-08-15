package me.serbob.toastedchatwave.TabCompleters;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatwaveTabCompleter implements TabCompleter {
    private static final String RELOAD_PERMISSION = "wave.reload";
    private static final String START_PERMISSION = "wave.start";

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission(RELOAD_PERMISSION)) {
                completions.add("reload");
            }
            if (sender.hasPermission(START_PERMISSION)) {
                completions.add("start");
            }
        } else if (args.length == 2 && "start".equalsIgnoreCase(args[0])) {
            ConfigurationSection wavesSection = ToastedChatWave.instance.getConfig().getConfigurationSection("waves");
            if (wavesSection != null) {
                completions.addAll(wavesSection.getKeys(false));
            }
        }

        return filterCompletions(completions, args[args.length - 1]);
    }

    private List<String> filterCompletions(List<String> completions, String arg) {
        String lowerArg = arg.toLowerCase();
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerArg))
                .collect(Collectors.toList());
    }
}