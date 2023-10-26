package me.serbob.toastedchatwave.Managers;

import me.serbob.toastedchatwave.APIs.PlaceholderAPI;
import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatwaveUtil;
import me.serbob.toastedchatwave.Util.TierUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.serbob.toastedchatwave.APIs.PlaceholderAPI.isPAPIenabled;

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
                Random random = new Random();
                for(String command: ToastedChatWave.instance.getConfig().getStringList("waves."+currentWave+".reward-commands."+ TierUtils.getHighestTier(player))) {
                    if(!command.equalsIgnoreCase("NONE")) {
                        String[] parts = command.split(":");
                        if (parts.length == 2) {
                            int probability = Integer.parseInt(parts[0]);
                            String cmd = parts[1];
                            int randomNumber = random.nextInt(100) + 1;
                            if (randomNumber <= probability) {
                                String finalCommand = cmd.replace("{player}", player.getName());
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                            }
                        } else {
                            String finalCommand = command.replace("{player}", player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                        }
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

        String newMessage = ChatwaveUtil.c(formattedConfigMessage
                .replace("{player}",player.getName())
                .replace("{playerName}",player.getName())
                .replace("{playerDisplayName}",player.getDisplayName())
                .replace("{message}",message)
                .replace("{color}",randomColor));
        if(isPAPIenabled()) {
            String possibleMessage = replacePlayerStatisticPlaceholder(player,newMessage);
            if(!possibleMessage.equalsIgnoreCase(" ")) {
                newMessage = possibleMessage;
            }
        }
        return newMessage;
    }
    private static String getRandomColor(List<String> rewardColors) {
        Random random = new Random();
        int index = random.nextInt(rewardColors.size());
        return rewardColors.get(index);
    }
    public static String replacePlayerStatisticPlaceholder(OfflinePlayer player, String input) {
        input = input.replace("{playerName}",player.getName());
        //System.out.println(getTotalBlocksMined(player)+"");
        while (input.contains("%")) {
            int startIndex;
            int endIndex;
            startIndex = input.indexOf("%");
            endIndex = input.indexOf("%",startIndex+1);

            if (startIndex < endIndex) {
                String placeholder = input.substring(startIndex, endIndex + 1);
                String statisticName = input.substring(startIndex + 1, endIndex);

                try {
                    String formattedValue;
                    if (isPAPIenabled()) {
                        try {
                            String parsedPlaceholder = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, "%"+statisticName+"%");
                            formattedValue = parsedPlaceholder;
                        } catch (Exception ignored) {
                            formattedValue = "";
                        }
                    } else {
                        formattedValue = " ";
                    }
                    formattedValue=formattedValue.replace("%","");
                    input = input.replace(placeholder, formattedValue);
                } catch (Exception ignored){
                    input = input.replace(placeholder, "");
                }
            } else {
                input = input.replace("}", "");
                input = input.replace("%","");
            }
        }

        return input;
    }
}
