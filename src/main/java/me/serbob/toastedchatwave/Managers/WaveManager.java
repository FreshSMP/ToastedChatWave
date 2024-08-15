package me.serbob.toastedchatwave.Managers;

import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatUtil;
import me.serbob.toastedchatwave.Util.TierUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static me.serbob.toastedchatwave.APIs.PlaceholderAPI.isPAPIenabled;

public class WaveManager {
    public static boolean isActive = false;
    public static Set<Player> playersReceived = new HashSet<>();
    public static String currentWave;
    private static final Random random = new Random();

    public static boolean receiveRewards(Player player) {
        return !playersReceived.contains(player);
    }

    public static boolean isAvailable(String message) {
        if (!isActive) return false;

        String waveWord = ToastedChatWave.instance.getConfig().getString("waves." + currentWave + ".word", "");
        boolean isWildcard = ToastedChatWave.instance.getConfig().getBoolean("waves." + currentWave + ".wildcard", false);

        message = ChatColor.stripColor(message);

        return isWildcard ? containsWordIgnoreCase(message, waveWord)
                : message.equalsIgnoreCase(waveWord);
    }

    private static boolean containsWordIgnoreCase(String message, String word) {
        return Pattern.compile(Pattern.quote(word), Pattern.CASE_INSENSITIVE).matcher(message).find();
    }

    public static void sendRewardMessages(Player player) {
        Bukkit.getScheduler().runTaskLater(ToastedChatWave.instance, () ->
                ToastedChatWave.instance.getConfig().getStringList("reward-messages").stream()
                        .filter(msg -> !msg.equalsIgnoreCase("NONE"))
                        .forEach(msg -> player.sendMessage(ChatUtil.c(msg))), 1L);
    }

    public static void sendRewards(Player player) {
        Bukkit.getScheduler().runTask(ToastedChatWave.instance, () -> {
            String tierPath = "waves." + currentWave + ".reward-commands." + TierUtils.getHighestTier(player);
            ToastedChatWave.instance.getConfig().getStringList(tierPath).stream()
                    .filter(command -> !command.equalsIgnoreCase("NONE"))
                    .forEach(command -> executeCommand(command, player));
        });
    }

    private static void executeCommand(String command, Player player) {
        String[] parts = command.split(":", 2);
        if (parts.length == 2) {
            int probability = Integer.parseInt(parts[0]);
            if (ThreadLocalRandom.current().nextInt(100) < probability) {
                dispatchCommand(parts[1], player);
            }
        } else {
            dispatchCommand(command, player);
        }
    }

    private static void dispatchCommand(String command, Player player) {
        String finalCommand = command.replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
    }

    public static void manageAftermath(Player player) {
        playersReceived.add(player);
    }

    public static String formatFinalMessage(Player player, String message) {
        List<String> rewardColors = ToastedChatWave.instance.getConfig().getStringList("reward-colors");
        String randomColor = rewardColors.isEmpty() ? " " : getRandomColor(rewardColors);
        String formattedConfigMessage = ToastedChatWave.instance.getConfig().getString("message_format", "");

        String newMessage = ChatUtil.c(formattedConfigMessage
                .replace("{player}", player.getName())
                .replace("{playerName}", player.getName())
                .replace("{playerDisplayName}", player.getDisplayName())
                .replace("{message}", message)
                .replace("{color}", randomColor));

        return isPAPIenabled() ? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, newMessage) : newMessage;
    }

    private static String getRandomColor(List<String> rewardColors) {
        return rewardColors.get(ThreadLocalRandom.current().nextInt(rewardColors.size()));
    }
}