package me.serbob.toastedchatwave.Managers;

import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatwaveUtil;
import me.serbob.toastedchatwave.Util.TierUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaveManager {
    public static boolean isActive=false;
    public static List<Player> playersReceived = new ArrayList<>();
    public static String currentWave;
    public static boolean receiveRewards(Player player) {
        if(playersReceived.contains(player)) {
            return false;
        }
        return true;
    }
    public static boolean isAvailable(Player player,String message) {
        if(!isActive) {
            return false;
        }
        if(!ChatColor.stripColor(message).equalsIgnoreCase(ToastedChatWave.instance.getConfig().getString("waves."+currentWave+".word"))) {
            return false;
        }
        return true;
    }
    public static void sendRewardMessages(Player player) {
        Bukkit.getScheduler().runTaskLater(ToastedChatWave.instance, () -> {
            for(String msg: ToastedChatWave.instance.getConfig().getStringList("reward-messages")) {
                if (!msg.equalsIgnoreCase("NONE")) {
                    player.sendMessage(ChatwaveUtil.c(msg));
                }
            }
        }, 1L);
    }
    public static void sendRewards(Player player) {
        Bukkit.getScheduler().runTask(ToastedChatWave.instance, new Runnable() {
            @Override
            public void run() {
                for(String msg: ToastedChatWave.instance.getConfig().getStringList("waves."+currentWave+".reward-commands."+ TierUtils.getHighestTier(player))) {
                    if(!msg.equalsIgnoreCase("NONE")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), msg
                                .replace("{player}",player.getName()));
                    }
                }
            }
        });
    }
    public static void manageAftermath(Player player) {
        playersReceived.add(player);
    }
    public static String formatFinalMessage(Player player, String message) {
        List<String> rewardColors = ToastedChatWave.instance.getConfig().getStringList("reward-colors");
        if (rewardColors.isEmpty()) {
            rewardColors.add(" ");
        }
        String randomColor = getRandomColor(rewardColors);
        String formattedConfigMessage = ToastedChatWave.instance.getConfig().getString("message_format");

        return ChatwaveUtil.c(formattedConfigMessage
                .replace("{player}",player.getName())
                .replace("{message}",message)
                .replace("{color}",randomColor));
    }

    private static String getRandomColor(List<String> rewardColors) {
        Random random = new Random();
        int index = random.nextInt(rewardColors.size());
        return rewardColors.get(index);
    }
}
